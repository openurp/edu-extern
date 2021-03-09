/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.edu.extern.exchange.web.action.admin

import java.time.ZoneId
import java.time.format.DateTimeFormatter

import org.beangle.commons.collection.{Collections, Properties}
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.transfer.exporter.ExportSetting
import org.beangle.webmvc.api.annotation.response
import org.beangle.webmvc.api.view.{PathView, View}
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.code.edu.model.{CourseTakeType, GradingMode}
import org.openurp.base.edu.model.{Course, Terms}
import org.openurp.boot.edu.helper.ProjectSupport
import org.openurp.edu.extern.exchange.service.{ExemptionCourse, ExemptionService}
import org.openurp.edu.extern.exchange.web.action.ExchangeGradePropertyExtractor
import org.openurp.edu.extern.model.{ExchangeGrade, ExchangeStudent}
import org.openurp.edu.grade.course.model.CourseGrade
import org.openurp.edu.grade.model.Grade
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.edu.program.model.PlanCourse

class GradeAction extends RestfulAction[ExchangeGrade] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _
  var exemptionService: ExemptionService = _

  override protected def getQueryBuilder: OqlBuilder[ExchangeGrade] = {
    val builder = super.getQueryBuilder
    getDate("fromAt") foreach { fromAt =>
      builder.where("exchangeGrade.updatedAt >= :fromAt", fromAt.atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant)
    }
    getDate("toAt") foreach { toAt =>
      builder.where(" exchangeGrade.updatedAt <= :toAt", toAt.plusDays(1).atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant)
    }
    getBoolean("hasCourse") foreach { hasCourse =>
      builder.where((if (hasCourse) "" else "not ") + "exists (from exchangeGrade.courses ec)")
    }
    builder
  }

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[ExchangeStudent], "es")
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
    val grade = entityDao.get(classOf[ExchangeGrade], longId("exchangeGrade"))
    put("grade", grade)
    val es = grade.exchangeStudent
    val std = es.std
    val plan = coursePlanProvider.getCoursePlan(grade.exchangeStudent.std)
    if (plan.isEmpty) return PathView("noPlanMsg")
    val planCourses=exemptionService.getConvertablePlanCourses(std,plan.get,grade.acquiredOn)
    put("convertedGrades", exemptionService.getConvertedGrades(std,grade.courses))
    put("planCourses", planCourses)
    put("gradingModes", getCodes(classOf[GradingMode]))
    forward()
  }

  def convert: View = {
    val eg = entityDao.get(classOf[ExchangeGrade], longId("grade"))
    val planCourses = entityDao.find(classOf[PlanCourse], longIds("planCourse"))
    val es = eg.exchangeStudent
    val ecs = Collections.newBuffer[ExemptionCourse]
    val std = es.std
    planCourses foreach { pc =>
      val semester = exemptionService.getSemester(std, eg.acquiredOn, termList(pc.terms).headOption).orNull
      val scoreText = get("scoreText" + pc.id)
      if (null != semester && scoreText.nonEmpty) {
        val gradingMode = entityDao.get(classOf[GradingMode], getInt("gradingMode.id" + pc.id, 0))
        val ec = ExemptionCourse(pc.course, pc.group.courseType, semester, pc.course.examMode, gradingMode,
          getFloat("score" + pc.id), scoreText)
        ecs += ec
      }
    }
    this.exemptionService.addExemption(eg, ecs.toSeq)
    redirect("search", "info.action.success")
  }

  private def termList(terms:Terms):List[Int]={
    val str = java.lang.Integer.toBinaryString(terms.value)
    var i = str.length - 1
    val result = new collection.mutable.ListBuffer[Int]
    while (i >= 0) {
      if (str.charAt(i) == '1') result += (str.length - i)
      i -= 1
    }
    result.toList
  }
  def removeCourseGrade: View = {
    val eg = entityDao.get(classOf[ExchangeGrade], longId("grade"))
    val cg = entityDao.get(classOf[CourseGrade], longId("courseGrade"))
    exemptionService.removeExemption(eg, cg.course)
    redirect("search", "info.action.success")
  }

  override def configExport(setting: ExportSetting): Unit = {
    super.configExport(setting)
    setting.context.extractor = new ExchangeGradePropertyExtractor
  }
}
