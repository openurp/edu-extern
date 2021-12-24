/*
 * Copyright (C) 2005, The OpenURP Software.
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

package org.openurp.edu.extern.web.helper

import org.beangle.data.dao.EntityDao
import org.beangle.data.transfer.exporter.DefaultPropertyExtractor
import org.openurp.edu.extern.model.{CertExamSignup, ExternGrade}
import org.openurp.std.info.model.{Examinee, Graduation}
import org.springframework.format.datetime.DateFormatter

import java.time.format.DateTimeFormatter

class PETSPropertyExtractor(entityDao: EntityDao) extends DefaultPropertyExtractor {

  override def getPropertyValue(target: Object, property: String): Any = {
    val signup = target.asInstanceOf[CertExamSignup]
    val std = signup.std
    property match {
      case "std.enrollYear" => std.beginOn.getYear.toString
      case "std.graduateStatus" =>
        val graduations = entityDao.findBy(classOf[Graduation], "std", List(std))
        if (graduations.isEmpty) "在籍" else "毕业"
      case "dummy1" => ""
      case "dummy2" => ""
      case "std.person.birthday" => std.person.birthday.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
      case "std.examineeCode" =>
        val examinees = entityDao.findBy(classOf[Examinee], "std", List(std))
        if (examinees.isEmpty) "" else examinees.head.code
      case _ => super.getPropertyValue(target, property)
    }
  }
}