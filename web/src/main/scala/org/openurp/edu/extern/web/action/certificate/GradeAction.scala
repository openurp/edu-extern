/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.edu.extern.web.action.certificate

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.excel.schema.ExcelSchema
import org.beangle.data.transfer.exporter.ExportSetting
import org.beangle.data.transfer.importer.ImportSetting
import org.beangle.data.transfer.importer.listener.ForeignerListener
import org.beangle.web.action.annotation.response
import org.beangle.web.action.view.{PathView, Stream, View}
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.edu.code.CourseType
import org.openurp.base.edu.model.Course
import org.openurp.base.model.{Project, Semester}
import org.openurp.base.service.SemesterService
import org.openurp.base.std.model.Student
import org.openurp.code.edu.model.{CourseTakeType, ExamStatus, GradingMode}
import org.openurp.edu.extern.code.{CertificateCategory, CertificateSubject}
import org.openurp.edu.extern.model.CertificateGrade
import org.openurp.edu.extern.service.ExemptionService
import org.openurp.edu.extern.web.helper.{CertificateGradeImportListener, CertificateGradePropertyExtractor}
import org.openurp.edu.grade.model.{CourseGrade, Grade}
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.edu.program.model.PlanCourse
import org.openurp.starter.web.support.ProjectSupport

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.time.{Instant, ZoneId}
import scala.collection.mutable

class GradeAction extends RestfulAction[CertificateGrade] with ProjectSupport {

  var exemptionService: ExemptionService = _
  var coursePlanProvider: CoursePlanProvider = _

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("certificateSubjects", getCodes(classOf[CertificateSubject]))
    put("certificateCategories", getCodes(classOf[CertificateCategory]))
    put("departments", getDeparts)
    put("project", getProject)
  }

  override def editSetting(entity: CertificateGrade): Unit = {
    super.editSetting(entity)

    given project: Project = getProject

    put("certificateSubjects", getCodes(classOf[CertificateSubject]))
    put("certificateCategories", getCodes(classOf[CertificateCategory]))
    put("semesters", entityDao.getAll(classOf[Semester])) //error
    put("gradingModes", getCodes(classOf[GradingMode]))
    put("examStatuses", getCodes(classOf[ExamStatus]))
  }

  override def saveAndRedirect(grade: CertificateGrade): View = {
    val project = getProject
    val stdCode = get("certificateGrade.std.code", "")
    if (grade.std == null && Strings.isNotBlank(stdCode)) {
      val q = OqlBuilder.from(classOf[Student], "s")
      q.where("s.code=:code and s.project=:project", stdCode, project)
      entityDao.search(q).foreach { std =>
        grade.std = std
      }
    }
    if (grade.std == null) return redirect("search", "保存失败,学号不存在")
    if (isExist(grade)) return redirect("search", "保存失败,成绩重复")
    grade.status = Grade.Status.Published
    grade.updatedAt = Instant.now
    super.saveAndRedirect(grade)
  }

  private def isExist(grade: CertificateGrade): Boolean = {
    val query = OqlBuilder.from(classOf[CertificateGrade], "grade")
    query.where("grade.std= :std", grade.std)
    query.where("grade.acquiredOn = :acquiredOn", grade.acquiredOn)
    query.where("grade.subject = :subject", grade.subject)
    if (!grade.persisted) {
      entityDao.search(query).nonEmpty
    } else {
      query.where("grade.id <>:id", grade.id)
      entityDao.search(query).nonEmpty
    }
  }

  def convertList(): View = {
    given project: Project = getProject

    val grade = entityDao.get(classOf[CertificateGrade], longId("certificateGrade"))
    put("grade", grade)
    val std = grade.std
    val plan = coursePlanProvider.getCoursePlan(grade.std)
    if (plan.isEmpty) return PathView("noPlanMsg")
    val planCourses = exemptionService.getConvertablePlanCourses(std, plan.get)
    val semesters = Collections.newMap[PlanCourse, Semester]
    planCourses foreach { pc =>
      exemptionService.getSemester(plan.get.program, pc.terms.termList.headOption) foreach { s =>
        semesters.put(pc, s)
      }
    }
    put("semesters", semesters)
    put("convertedGrades", exemptionService.getConvertedGrades(std, grade.courses))
    put("planCourses", planCourses)
    put("ExemptionType", entityDao.get(classOf[CourseTakeType], CourseTakeType.Exemption))
    put("gradingModes", getCodes(classOf[GradingMode]))
    forward()
  }

  def convert(): View = {
    val eg = entityDao.get(classOf[CertificateGrade], longId("grade"))
    val courses = entityDao.find(classOf[Course], longIds("course"))
    val exemptionCourses = Collections.newSet[Course]
    courses foreach { c =>
      val scoreText = get("scoreText_" + c.id, "")
      if scoreText.nonEmpty then exemptionCourses.addOne(c)
    }
    this.exemptionService.addExemption(eg, exemptionCourses)
    redirect("search", "info.action.success")
  }

  def removeCourseGrade(): View = {
    val grade = entityDao.get(classOf[CertificateGrade], longId("grade"))
    val cg = entityDao.get(classOf[CourseGrade], longId("courseGrade"))
    exemptionService.removeExemption(grade, cg.course)
    redirect("search", "info.action.success")
  }

  @response
  def downloadTemplate(): Any = {
    given project: Project = getProject

    val gradingModes = getCodes(classOf[GradingMode]).map(x => x.code + " " + x.name)
    val subjects = getCodes(classOf[CertificateSubject]).map(x => x.code + " " + x.name)
    val schema = new ExcelSchema()
    val sheet = schema.createScheet("数据模板")
    sheet.title("校外证书成绩模板")
    sheet.remark("特别说明：\n1、不可改变本表格的行列结构以及批注，否则将会导入失败！\n2、须按照规格说明的格式填写。\n3、可以多次导入，重复的信息会被新数据更新覆盖。\n4、保存的excel文件名称可以自定。")
    sheet.add("学号", "certificateGrade.std.code").length(15).required()
    sheet.add("考试科目", "certificateGrade.subject.code").ref(subjects).required()
    sheet.add("成绩", "certificateGrade.scoreText").required()
    sheet.add("是否通过", "certificateGrade.passed").bool().required()
    sheet.add("获得日期", "certificateGrade.acquiredOn").date().required()
    sheet.add("成绩记录方式", "certificateGrade.gradingMode.code").ref(gradingModes).required()
    sheet.add("证书编号", "certificateGrade.certificate")
    sheet.add("准考证号", "certificateGrade.examNo")
    sheet.add("免修课程代码", "courseCodes").remark("多个课程可用半角逗号分隔")

    val os = new ByteArrayOutputStream()
    schema.generate(os)
    Stream(new ByteArrayInputStream(os.toByteArray), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "证书信息.xlsx")
  }

  override protected def configImport(setting: ImportSetting): Unit = {
    val fl = new ForeignerListener(entityDao)
    setting.listeners = List(fl, new CertificateGradeImportListener(entityDao, getProject, exemptionService))
  }

  override def configExport(setting: ExportSetting): Unit = {
    super.configExport(setting)
    setting.context.extractor = new CertificateGradePropertyExtractor()
  }

  override protected def getQueryBuilder: OqlBuilder[CertificateGrade] = {
    val builder = super.getQueryBuilder
    getFloat("from") foreach { from =>
      builder.where("certificateGrade.score >=:F", from)
    }
    getFloat("to") foreach { to =>
      builder.where("certificateGrade.score <=:T", to)
    }

    getDate("fromAt") foreach { fromAt =>
      builder.where("certificateGrade.updatedAt >= :fromAt", fromAt.atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant)
    }
    getDate("toAt") foreach { toAt =>
      builder.where(" certificateGrade.updatedAt <= :toAt", toAt.plusDays(1).atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant)
    }
    getBoolean("hasCourse") foreach { hasCourse =>
      builder.where((if (hasCourse) "" else "not ") + "exists (from certificateGrade.courses ec)")
    }
    builder
  }
}
