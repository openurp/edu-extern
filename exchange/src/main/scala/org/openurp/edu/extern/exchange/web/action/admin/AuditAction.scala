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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.beangle.commons.collection.{Collections, Properties}
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.EmsApp
import org.beangle.webmvc.api.annotation.response
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.code.edu.model.{GradingMode, StudentStatus}
import org.openurp.edu.base.AuditStates
import org.openurp.edu.base.code.model.CourseType
import org.openurp.edu.base.model.{Course, Semester, Student}
import org.openurp.edu.extern.exchange.service.{CourseGradeConvertor, ExemptionCourse, ExemptionService}
import org.openurp.edu.extern.model.{ExchangeSchool, ExchangeStudent}
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.edu.web.ProjectSupport

class AuditAction extends RestfulAction[ExchangeStudent] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _

  var exemptionService: ExemptionService = _

  override def indexSetting(): Unit = {
    put("studentStatuses", getCodes(classOf[StudentStatus]))
  }

  override def info(id: String): View = {
    val es = getModel[ExchangeStudent](entityName, convertId(id))
    val repo = EmsApp.getBlobRepository(true)
    es.transcriptPath foreach { p =>
      put("transcriptPath", repo.url(p))
    }
    put("exchangeStudent", es)
    forward()
  }

  override protected def editSetting(entity: ExchangeStudent): Unit = {
    put("schools", entityDao.getAll(classOf[ExchangeSchool]))
    val project=getProject
    put("levels", project.levels)
    put("project",project)
    super.editSetting(entity)
  }

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[Student], "std")
    query.where("std.user.code=:code", get("q", ""))
    val yyyyMM = DateTimeFormatter.ofPattern("yyyy-MM")
    entityDao.search(query).map { std =>
      val p = new Properties()
      p.put("id", std.id)
      p.put("name", s"${std.state.get.department.name} ${std.user.name}")
      p
    }
  }

  private def getSemester(date: LocalDate): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
      .where("semester.calendar in(:calendars)", getProject.calendars)
    builder.where("semester.endOn > :date", date)
    builder.orderBy("semester.beginOn")
    builder.limit(1, 1)
    entityDao.search(builder).head
  }

  private def buildCourseTypes(std: Student): Map[Course, CourseType] = {
    coursePlanProvider.getCoursePlan(std) match {
      case None => Map.empty
      case Some(plan) =>
        val courseTypes = Collections.newMap[Course, CourseType]
        for (cg <- plan.groups) {
          cg.planCourses foreach { x => courseTypes.put(x.course, x.group.courseType) }
        }
        courseTypes.toMap
    }
  }

  def audit(): View = {
    val esId = longId("exchangeStudent")
    val es = entityDao.get(classOf[ExchangeStudent], esId)
    val passed = getBoolean("passed", false)
    val courseTypes = buildCourseTypes(es.std)
    if (passed) {
      es.auditState = AuditStates.Finalized
      val allCourses = Collections.newSet[Course]
      es.grades foreach { eg =>
        val ecs = Collections.newBuffer[ExemptionCourse]
        val semester = getSemester(eg.acquiredOn)
        val gradingMode = entityDao.get(classOf[GradingMode], GradingMode.Percent)
        eg.courses foreach { c =>
          if (!allCourses.contains(c)) {
            val ec = ExemptionCourse(c, courseTypes.getOrElse(c, c.courseType), semester, c.examMode, gradingMode, None, None)
            ecs += ec
            allCourses += c
          }
        }
        if (ecs.nonEmpty) {
          exemptionService.addExemption(eg, ecs.toSeq)
        }
      }
    } else {
      es.auditState = AuditStates.Rejected
    }
    entityDao.saveOrUpdate(es)
    redirect("search", "info.save.success")
  }
}
