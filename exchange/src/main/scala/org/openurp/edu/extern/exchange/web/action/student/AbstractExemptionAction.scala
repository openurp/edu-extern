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
package org.openurp.edu.extern.exchange.web.action.student

import java.time.{Instant, LocalDate}

import javax.servlet.http.Part
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.app.UrpApp
import org.openurp.edu.base.States
import org.openurp.edu.base.model.{Course, Student}
import org.openurp.edu.base.web.ProjectSupport
import org.openurp.edu.extern.model.{ExemptionCredit, ExchangeGrade, ExchangeSchool, ExchangeStudent}
import org.openurp.edu.grade.course.model.CourseGrade
import org.openurp.edu.program.domain.CoursePlanProvider

abstract class AbstractExemptionAction extends RestfulAction[ExchangeStudent] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _

  override def index(): View = {
    val std = getStudent
    val query = OqlBuilder.from(classOf[ExchangeStudent], "es")
    query.where("es.std=:std", std)
    query.orderBy("es.beginOn")
    val exchangeStudents = entityDao.search(query)
    put("exchangeStudents", exchangeStudents)
    if (exchangeStudents.isEmpty) {
      forward("welcome")
    } else {
      val repo = UrpApp.getBlobRepository(true)
      val paths = exchangeStudents.filter(_.transcriptPath.isDefined).map(x => (x, repo.url(x.transcriptPath.get)))
      put("transcriptPaths", paths.toMap)
      forward()
    }
  }

  override def save(): View = {
    var std = getStudent
    var school: ExchangeSchool = null
    val schoolId = getInt("exchangeStudent.school.id")
    schoolId match {
      case Some(sid) => school = entityDao.get(classOf[ExchangeSchool], sid.toInt)
      case None =>
        get("newSchool") foreach { nsname =>
          val schools = entityDao.findBy(classOf[ExchangeSchool], "name", List(nsname))
          if (schools.nonEmpty) {
            school = schools.head
          } else {
            school = new ExchangeSchool
            school.name = nsname
            school.project = std.project
            school.beginOn = LocalDate.now
            school.updatedAt = Instant.now
            school.code = "user_add_" + System.currentTimeMillis()
            entityDao.saveOrUpdate(school)
          }
        }
    }

    val es = populateEntity()
    es.std = std
    es.school = school
    es.updatedAt = Instant.now

    val parts = getAll("transcript", classOf[Part])
    if (parts.nonEmpty && parts.head.getSize > 0) {
      val repo = UrpApp.getBlobRepository(true)
      val part = parts.head
      es.transcriptPath foreach { p =>
        repo.remove(p)
      }
      val meta = repo.upload("/exchange", part.getInputStream, part.getSubmittedFileName, es.std.user.code + " " + es.std.user.name);
      es.transcriptPath = Some(meta.path)
    }
    entityDao.saveOrUpdate(es)
    redirect("editGrades", "&exchangeStudent.id=" + es.id, "info.save.success")
  }

  def editGrades(): View = {
    val es = getEntity(entityType, simpleEntityName)
    put("exchangeStudent", es)
    forward();
  }

  def saveGrades(): View = {
    val es = getEntity(entityType, simpleEntityName)
    es.grades.clear()
    var credits = 0f
    (1 to 20) foreach { i =>
      val grade = populateEntity(classOf[ExchangeGrade], "grade_" + i)
      if (Strings.isNotEmpty(grade.courseName) && Strings.isNotEmpty(grade.scoreText) && null != grade.acquiredOn) {
        grade.exchangeStudent = es
        credits += grade.credits
        grade.updatedAt=Instant.now
        es.grades.addOne(grade)
      }
    }
    es.credits = credits
    entityDao.saveOrUpdate(es)
    put("exchangeStudent", es)
    redirect("editApplies", "&exchangeStudent.id=" + es.id, "info.save.success")
  }

  private def getPlanCourses(std: Student): collection.Seq[Course] = {
    val courses = Collections.newSet[Course]
    coursePlanProvider.getCoursePlan(std) foreach { plan =>
      for (group <- plan.groups) {
        if (group.planCourses.isEmpty && group.children.isEmpty) {
          courses.addAll(entityDao.findBy(classOf[Course], "courseType", List(group.courseType)))
        } else {
          for (planCourse <- group.planCourses) {
            courses.addOne(planCourse.course)
          }
        }
      }
    }

    val query = OqlBuilder.from[Course](classOf[CourseGrade].getName, "cg")
    query.where("cg.std=:std and cg.passed=true", std)
    query.select("cg.course")
    courses.subtractAll(entityDao.search(query))
    courses.toBuffer
  }

  def editApplies(): View = {
    val es = getEntity(entityType, simpleEntityName)
    put("exchangeStudent", es)
    entityDao.findBy(classOf[ExemptionCredit], "std", List(es.std)) foreach { e =>
      put("exemptionCredit", e)
    }
    put("planCourses", getPlanCourses(es.std))
    forward()
  }

  @mapping(method = "delete")
  override def remove(): View = {
    val id = getId("exchangeStudent", classOf[Long])
    val es = entityDao.get(classOf[ExchangeStudent], id.get)
    val std = getStudent
    if (es.std == std && es.state != States.Accepted) {
      es.transcriptPath foreach { p =>
        UrpApp.getBlobRepository(true).remove(p)
      }
      entityDao.remove(es)
      redirect("index", "info.remove.success")
    } else {
      redirect("index", "删除失败")
    }
  }

  def saveApplies(): View = {
    val es = getEntity(entityType, simpleEntityName)
    if (es.state == States.Accepted) {
      return redirect("index", "已经审核通过的申请不能再次冲抵")
    }
    val courseSet = Collections.newSet[Course]
    es.grades foreach { m =>
      val courses = getAll(s"grade_${m.id}.courses") map (x => entityDao.get(classOf[Course], x.toString.toLong))
      m.courses.clear()
      courseSet.addAll(courses)
      m.courses.addAll(courses)
    }
    es.updatedAt = Instant.now
    es.exemptionCredits = courseSet.toSeq.map(_.credits).sum
    entityDao.saveOrUpdate(es)
    val limit = entityDao.findBy(classOf[ExemptionCredit], "std", List(es.std)).headOption
    limit match {
      case None => es.state = States.Submited
      case Some(l) =>
        val totalCredits = entityDao.findBy(classOf[ExchangeStudent], "std", List(es.std)).map(_.exemptionCredits).sum
        if (l.maxValue == 0  || java.lang.Float.compare(l.maxValue, totalCredits) >= 0) {
          es.state = States.Submited
        } else {
          es.state = States.Draft
        }
    }
    entityDao.saveOrUpdate(es)
    if (es.state == States.Submited) {
      redirect("index", "info.save.success")
    } else {
      redirect("editApplies", "&exchangeStudent.id=" + es.id, "超出认定学分上限，请重新选择课程")
    }
  }
}
