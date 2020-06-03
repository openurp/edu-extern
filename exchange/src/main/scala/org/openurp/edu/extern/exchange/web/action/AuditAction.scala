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
package org.openurp.edu.extern.exchange.web.action

import java.time.LocalDate

import org.beangle.commons.collection.Collections
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.app.UrpApp
import org.openurp.code.edu.model.GradingMode
import org.openurp.edu.base.States
import org.openurp.edu.base.code.model.CourseType
import org.openurp.edu.base.model.{Course, Semester, Student}
import org.openurp.edu.base.web.ProjectSupport
import org.openurp.edu.extern.exchange.service.{CourseGradeConvertor, ExemptionCourse, ExemptionService}
import org.openurp.edu.extern.model.ExchangeStudent
import org.openurp.edu.program.domain.CoursePlanProvider

class AuditAction extends RestfulAction[ExchangeStudent] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _

  var exemptionService: ExemptionService = _

  override def info(id: String): View = {
    val es = getModel[ExchangeStudent](entityName, convertId(id))
    val repo = UrpApp.getBlobRepository(true)
    es.transcriptPath foreach { p =>
      put("transcriptPath", repo.url(p))
    }
    put("exchangeStudent", es)
    forward()
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
    val convertor = new CourseGradeConvertor(entityDao)
    val courseTypes = buildCourseTypes(es.std)
    if (passed) {
      es.state = States.Finalized
      es.grades foreach { eg =>
        val ecs = Collections.newBuffer[ExemptionCourse]
        val semester=getSemester(eg.acquiredOn)
        val gradingMode= entityDao.get(classOf[GradingMode],GradingMode.Percent)
        eg.courses foreach { c =>
          val ec = ExemptionCourse(c, courseTypes.getOrElse(c, c.courseType),semester,c.examMode, gradingMode,           None,None)
          ecs += ec
        }
        exemptionService.addExemption(eg,ecs.toSeq)
      }
    } else {
      es.state = States.Rejected
    }
    entityDao.saveOrUpdate(es)
    redirect("search", "info.save.success")
  }
}
