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
package org.openurp.edu.extern.exchange.service.impl

import java.time.{Instant, LocalDate}

import org.beangle.commons.collection.Collections
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.code.edu.model.CourseTakeType
import org.openurp.edu.base.AuditStates
import org.openurp.edu.base.model.{Course, Semester, Student}
import org.openurp.edu.base.service.SemesterService
import org.openurp.edu.extern.exchange.service.{CourseGradeConvertor, ExemptionCourse, ExemptionService}
import org.openurp.edu.extern.model.{CertificateGrade, ExchangeGrade, ExchangeStudent, ExemptionCredit}
import org.openurp.edu.grade.course.model.CourseGrade
import org.openurp.edu.grade.model.Grade
import org.openurp.edu.program.model.{CoursePlan, PlanCourse}

class ExemptionServiceImpl extends ExemptionService {

  var entityDao: EntityDao = _

  var semesterService: SemesterService = _

  override def getSemester(std: Student, acquiredOn: LocalDate, term: Option[Int]): Option[Semester] = {
    if (acquiredOn.isBefore(std.beginOn)) {
      term match {
        case Some(t) => semesterService.get(std.project, std.beginOn, std.endOn, t)
        case None => None
      }
    } else {
      semesterService.get(std.project, acquiredOn)
    }
  }

  override def getConvertablePlanCourses(std: Student, plan: CoursePlan, acquiredOn: LocalDate): Seq[PlanCourse] = {
    val coursesMap = Collections.newMap[Course, PlanCourse]
    plan.planCourses foreach { pc =>
      coursesMap.put(pc.course, pc)
    }
    val query = OqlBuilder.from(classOf[CourseGrade], "cg")
    query.where("cg.std=:std and cg.status=:status", std, Grade.Status.Published)
    val courseGrades = entityDao.search(query)
    for (courseGrade <- courseGrades) {
      if (courseGrade.passed) coursesMap.remove(courseGrade.course)
    }
    if (acquiredOn.isBefore(std.beginOn)) {
      coursesMap.filterInPlace { case (_, pc) =>
        pc.terms.termList.nonEmpty && semesterService.get(std.project, std.beginOn, std.endOn, pc.terms.termList.head).nonEmpty
      }
    }
    coursesMap.values.toSeq
  }

  override def getConvertedGrades(std: Student, courses: collection.Iterable[Course]): Seq[CourseGrade] = {
    if (courses.isEmpty) {
      List.empty
    } else {
      val query2 = OqlBuilder.from(classOf[CourseGrade], "cg")
      query2.where("cg.std=:std", std)
      query2.where("cg.course in(:courses)", courses)
      query2.where("cg.courseTakeType.id=:exemption", CourseTakeType.Exemption)
      entityDao.search(query2)
    }
  }

  override def recalcExemption(std: Student): Unit = {
    //重新统计已经免修的学分
    val ecBuilder = OqlBuilder.from(classOf[ExemptionCredit], "ec")
    ecBuilder.where("ec.std=:std", std)
    val ec = entityDao.search(ecBuilder).headOption match {
      case Some(e) => e
      case None =>
        val e = new ExemptionCredit
        e.std = std
        e
    }
    var exemptedCourses = Collections.newSet[Course]
    entityDao.findBy(classOf[ExchangeStudent], "std", List(std)).filter(_.auditState == AuditStates.Finalized) foreach { fes =>
      fes.grades foreach { fgrade =>
        exemptedCourses ++= fgrade.courses
      }
    }
    ec.exempted = exemptedCourses.toList.map(_.credits).sum
    ec.updatedAt = Instant.now
    entityDao.saveOrUpdate(ec)
  }

  override def removeExemption(eg: ExchangeGrade, course: Course): Unit = {
    eg.courses.subtractOne(course)
    entityDao.saveOrUpdate(eg)
    val es = eg.exchangeStudent
    removeExemption(es.std, course)
    entityDao.saveOrUpdate(eg)
  }

  override def removeExemption(cg: CertificateGrade, course: Course): Unit = {
    cg.courses.subtractOne(course)
    entityDao.saveOrUpdate(cg)
    removeExemption(cg.std, course)
    entityDao.saveOrUpdate(cg)
  }

  private def removeExemption(std: Student, course: Course): Unit = {
    val cgQuery = OqlBuilder.from(classOf[CourseGrade], "cg")
    cgQuery.where("cg.std=:std and cg.course=:course", std, course)
    cgQuery.where("cg.courseTakeType.id=:exemption", CourseTakeType.Exemption)
    val cgs = entityDao.search(cgQuery)
    if (cgs.size > 1) {
      throw new RuntimeException(s"found ${cgs.size} exemption grades of ${std.user.code}")
    } else {
      entityDao.remove(cgs)
      this.recalcExemption(std)
    }
  }

  override def addExemption(eg: ExchangeGrade, ecs: Seq[ExemptionCourse]): Unit = {
    val remark = eg.exchangeStudent.school.name + " " + eg.courseName + " " + eg.scoreText
    val std = eg.exchangeStudent.std
    addExemption(std, ecs, remark)
    ecs foreach { ec =>
      eg.courses += ec.course
    }
    entityDao.saveOrUpdate(eg)
    recalcExemption(std)
  }

  override def addExemption(cg: CertificateGrade, ecs: Seq[ExemptionCourse]): Unit = {
    val remark = cg.subject.name + " " + cg.scoreText
    val std = cg.std
    addExemption(std, ecs, remark)
    ecs foreach { ec =>
      cg.courses += ec.course
    }
    entityDao.saveOrUpdate(cg)
    recalcExemption(std)
  }

  private def addExemption(std: Student, ecs: Seq[ExemptionCourse], remark: String): Unit = {
    val convertor = new CourseGradeConvertor(entityDao)
    ecs foreach { ec =>
      val grade = convertor.convert(std, ec, remark)
      entityDao.saveOrUpdate(grade)
    }
  }
}
