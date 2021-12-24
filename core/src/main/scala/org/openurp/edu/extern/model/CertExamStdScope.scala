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

package org.openurp.edu.extern.model

import org.beangle.commons.lang.Strings
import org.beangle.data.model.LongId
import org.openurp.base.edu.model.Student
import org.openurp.code.edu.model.EducationLevel

import scala.collection.mutable

class CertExamStdScope extends LongId {

  var setting: CertExamSignupSetting = _

  var grades: Option[String] = None

  var level: EducationLevel = _

  var includeIn: Boolean = _

  var codes: mutable.Set[String] = new mutable.HashSet[String]

  def matchStd(std: Student): Boolean = {
    if (codes.nonEmpty) {
      codes.contains(std.user.code)
    } else {
      if (std.level == this.level) {
        grades match {
          case None => true
          case Some(g) => Strings.split(g).toSet.contains(std.state.get.grade)
        }
      } else {
        false
      }
    }
  }
}
