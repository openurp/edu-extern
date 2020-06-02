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

import java.time.Instant

import org.beangle.commons.collection.Collections
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.code.edu.model.{CourseTakeType, GradingMode}
import org.openurp.edu.base.States
import org.openurp.edu.base.model.Course
import org.openurp.edu.base.model.{Semester, Student}
import org.openurp.edu.extern.exchange.service.{CourseGradeConvertor, ExchangeStudentService, ExemptionCourse}
import org.openurp.edu.extern.model.{ExchangeGrade, ExchangeStudent, ExemptionCredit}
import org.openurp.edu.grade.course.model.CourseGrade

class ExchangeStudentServiceImpl extends ExchangeStudentService {

  var entityDao: EntityDao = _

  override def addExemption(eg: ExchangeGrade,ecs: Seq[ExemptionCourse]): Unit = {
    val convertor = new CourseGradeConvertor(entityDao)
    val es = eg.exchangeStudent
    val std = es.std
    ecs foreach { ec =>
      val grade = convertor.convert(eg,std, ec)
      entityDao.saveOrUpdate(grade)
      eg.courses += grade.course
    }
    entityDao.saveOrUpdate(eg)
    recalcExemption(std)
  }

  override def removeExemption(eg: ExchangeGrade, course: Course): Unit = {
    eg.courses.subtractOne(course)
    entityDao.saveOrUpdate(eg)
    val es=eg.exchangeStudent
    val cgQuery = OqlBuilder.from(classOf[CourseGrade], "cg")
    cgQuery.where("cg.std=:std and cg.course=:course", es.std, course)
    cgQuery.where("cg.courseTakeType.id=:exemption",CourseTakeType.Exemption)
    val cgs= entityDao.search(cgQuery)
    if(cgs.size>1){
      throw new RuntimeException(s"found ${cgs.size} exemption grades of ${es.std.user.code}")
    }else{
    entityDao.remove(cgs)
    this.recalcExemption(es.std)
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
    entityDao.findBy(classOf[ExchangeStudent], "std", List(std)).filter(_.state == States.Finalized) foreach { fes =>
      fes.grades foreach { fgrade =>
        exemptedCourses ++= fgrade.courses
      }
    }
    ec.exempted =  exemptedCourses.toList.map(_.credits).sum
    ec.updatedAt = Instant.now
    entityDao.saveOrUpdate(ec)
  }
}
