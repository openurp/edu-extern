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

import org.openurp.edu.extern.model.ExternGrade

import java.time.format.DateTimeFormatter
import org.beangle.commons.bean.DefaultPropertyExtractor
class ExternGradePropertyExtractor extends DefaultPropertyExtractor {
  override def get(target: Object, property: String): Any = {
    val eg = target.asInstanceOf[ExternGrade]
    val std = eg.externStudent.std
    property match {
      case "courseCode" => "01"
      case "creditHours" => "0"
      case "acquiredIn" => DateTimeFormatter.ofPattern("yyyyMM").format(eg.acquiredIn)
      case "courseCodes" => eg.exempts.map(c => s"${c.code}").mkString("\r\n")
      case "courseNames" => eg.exempts.map(c => s"${c.name}").mkString("\r\n")
      case "courseCredits" => eg.exempts.map(c => s"${c.getCredits(std.level)}").mkString("\r\n")
      case "courses" =>
        if (eg.exempts.isEmpty) {
          "--"
        } else {
          eg.exempts.map(c => s"${c.name} ${c.getCredits(std.level)}分").mkString("\r\n")
        }
      case _ =>
        super.get(target, property)
    }
  }
}
