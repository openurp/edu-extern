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

package org.openurp.edu.extern.web.helper

import org.beangle.data.dao.EntityDao
import org.beangle.commons.bean.DefaultPropertyExtractor
import org.openurp.base.std.model.Graduate
import org.openurp.edu.extern.model.{CertSignup, ExternGrade}
import org.openurp.std.info.model.Examinee
import org.springframework.format.datetime.DateFormatter

import java.time.format.DateTimeFormatter

class PETSPropertyExtractor(entityDao: EntityDao) extends DefaultPropertyExtractor {

  override def get(target: Object, property: String): Any = {
    val signup = target.asInstanceOf[CertSignup]
    val std = signup.std
    property match {
      case "std.enrollYear" => std.beginOn.getYear.toString
      case "std.graduateStatus" =>
        val graduates = entityDao.findBy(classOf[Graduate], "std", List(std))
        if (graduates.isEmpty) "在籍" else "毕业"
      case "dummy1" => ""
      case "dummy2" => ""
      case "std.person.birthday" =>
        std.person.birthday match {
          case None => ""
          case Some(b) => b.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        }
      case "std.examineeCode" =>
        val examinees = entityDao.findBy(classOf[Examinee], "std", List(std))
        if (examinees.isEmpty) "" else examinees.head.code
      case _ => super.get(target, property)
    }
  }
}
