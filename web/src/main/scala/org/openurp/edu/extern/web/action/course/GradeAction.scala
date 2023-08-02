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
import org.beangle.data.transfer.exporter.ExportContext
import org.beangle.web.action.annotation.response
import org.beangle.web.action.view.{PathView, View}
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.edu.model.Course
import org.openurp.base.model.{Project, Semester}
import org.openurp.base.std.model.ExternStudent
import org.openurp.code.edu.model.{CourseTakeType, GradingMode}
import org.openurp.edu.exempt.service.ExemptionService
import org.openurp.edu.extern.model.ExternGrade
import org.openurp.edu.extern.web.helper.ExternGradePropertyExtractor
import org.openurp.edu.grade.model.CourseGrade
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.edu.program.model.PlanCourse
import org.openurp.starter.web.support.ProjectSupport

import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GradeAction extends RestfulAction[ExternGrade], ExportSupport[ExternGrade], ProjectSupport {

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
      builder.where((if (hasCourse) "" else "not ") + "exists (from externGrade.exempts ec)")
    }
    builder
  }

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[ExternStudent], "es")
    query.where("es.std.code=:code", get("q", ""))
    val yyyyMM = DateTimeFormatter.ofPattern("yyyy-MM")
    entityDao.search(query).map { es =>
      val p = new Properties()
      p.put("value", es.id.toString)
      p.put("text", s"${es.std.code} ${es.std.name} ${es.school.name}(${es.beginOn.format(yyyyMM)})")
      p
    }
  }

  def convertList(): View = {
    val grade = entityDao.get(classOf[ExternGrade], getLongId("externGrade"))

    given project: Project = grade.externStudent.std.project

    put("grade", grade)
    val es = grade.externStudent
    val std = es.std
    val plan = coursePlanProvider.getCoursePlan(grade.externStudent.std)
    if (plan.isEmpty) return PathView("noPlanMsg")
    val planCourses = exemptionService.getConvertablePlanCourses(std, plan.get)
    put("convertedGrades", exemptionService.getConvertedGrades(std, grade.exempts))
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

  def convert(): View = {
    val eg = entityDao.get(classOf[ExternGrade], getLongId("grade"))
    val courses = entityDao.find(classOf[Course], getLongIds("course"))
    val exemptCourses = Collections.newSet[Course]
    courses foreach { c =>
      val scoreText = get("scoreText_" + c.id, "")
      if (scoreText.nonEmpty) {
        exemptCourses += c
      }
    }
    this.exemptionService.addExemption(eg, exemptCourses)
    redirect("search", "info.action.success")
  }

  def removeCourseGrade(): View = {
    val eg = entityDao.get(classOf[ExternGrade], getLongId("grade"))
    val cg = entityDao.get(classOf[CourseGrade], getLongId("courseGrade"))
    exemptionService.removeExemption(eg, cg.course)
    redirect("search", "info.action.success")
  }

  override def configExport(context: ExportContext): Unit = {
    super.configExport(context)
    context.extractor = new ExternGradePropertyExtractor
  }
}
