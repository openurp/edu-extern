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

package org.openurp.edu.extern.web.action.course

import org.beangle.commons.collection.{Collections, Properties}
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.transfer.exporter.ExportSetting
import org.beangle.web.action.annotation.response
import org.beangle.web.action.view.{PathView, View}
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.edu.code.CourseType
import org.openurp.base.edu.model.Course
import org.openurp.base.model.Semester
import org.openurp.base.std.model.ExternStudent
import org.openurp.code.edu.model.{CourseTakeType, GradingMode}
import org.openurp.edu.extern.model.ExternGrade
import org.openurp.edu.extern.service.{ExemptionCourse, ExemptionService}
import org.openurp.edu.extern.web.helper.ExternGradePropertyExtractor
import org.openurp.edu.grade.model.CourseGrade
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.edu.program.model.PlanCourse
import org.openurp.starter.edu.helper.ProjectSupport

import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GradeAction extends RestfulAction[ExternGrade] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _
  var exemptionService: ExemptionService = _

  override protected def getQueryBuilder: OqlBuilder[ExternGrade] = {
    val builder = super.getQueryBuilder
    getDate("fromAt") foreach { fromAt =>
      builder.where("externGrade.updatedAt >= :fromAt", fromAt.atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant)
    }
    getDate("toAt") foreach { toAt =>
      builder.where(" externGrade.updatedAt <= :toAt", toAt.plusDays(1).atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant)
    }
    getBoolean("hasCourse") foreach { hasCourse =>
      builder.where((if (hasCourse) "" else "not ") + "exists (from externGrade.courses ec)")
    }
    builder
  }

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[ExternStudent], "es")
    query.where("es.std.user.code=:code", get("q", ""))
    val yyyyMM = DateTimeFormatter.ofPattern("yyyy-MM")
    entityDao.search(query).map { es =>
      val p = new Properties()
      p.put("value", es.id.toString)
      p.put("text", s"${es.std.user.code} ${es.std.user.name} ${es.school.name}(${es.beginOn.format(yyyyMM)})")
      p
    }
  }

  def convertList: View = {
    val grade = entityDao.get(classOf[ExternGrade], longId("externGrade"))
    put("grade", grade)
    val es = grade.externStudent
    val std = es.std
    val plan = coursePlanProvider.getCoursePlan(grade.externStudent.std)
    if (plan.isEmpty) return PathView("noPlanMsg")
    val planCourses = exemptionService.getConvertablePlanCourses(std, plan.get, grade.acquiredOn)
    put("convertedGrades", exemptionService.getConvertedGrades(std, grade.courses))
    val semesters = Collections.newMap[PlanCourse, Semester]
    planCourses foreach { pc =>
      exemptionService.getSemester(plan.get.program, pc.terms.termList.headOption) foreach { s =>
        semesters.put(pc, s)
      }
    }
    put("semesters", semesters)
    put("planCourses", planCourses)
    put("ExemptionType", entityDao.get(classOf[CourseTakeType], CourseTakeType.Exemption))
    put("gradingModes", getCodes(classOf[GradingMode]))
    forward()
  }

  def convert: View = {
    val eg = entityDao.get(classOf[ExternGrade], longId("grade"))
    val courses = entityDao.find(classOf[Course], longIds("course"))
    val ecs = Collections.newBuffer[ExemptionCourse]
    courses foreach { c =>
      val scoreText = get("scoreText_" + c.id,"")
      if (scoreText.nonEmpty) {
        val courseType = entityDao.get(classOf[CourseType], getInt(s"courseType_${c.id}").getOrElse(0))
        val semester = entityDao.get(classOf[Semester], getInt(s"semester_${c.id}").getOrElse(0))
        val gradingMode = entityDao.get(classOf[GradingMode], getInt("gradingMode_" + c.id, 0))
        val ec = ExemptionCourse(c, courseType, semester, c.examMode, gradingMode,
          getFloat("score_" + c.id), scoreText)
        ecs += ec
      }
    }
    this.exemptionService.addExemption(eg, ecs.toSeq)
    redirect("search", "info.action.success")
  }

  def removeCourseGrade: View = {
    val eg = entityDao.get(classOf[ExternGrade], longId("grade"))
    val cg = entityDao.get(classOf[CourseGrade], longId("courseGrade"))
    exemptionService.removeExemption(eg, cg.course)
    redirect("search", "info.action.success")
  }

  override def configExport(setting: ExportSetting): Unit = {
    super.configExport(setting)
    setting.context.extractor = new ExternGradePropertyExtractor
  }
}
