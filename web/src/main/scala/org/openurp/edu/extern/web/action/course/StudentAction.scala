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

package org.openurp.edu.extern.web.action.course

import org.beangle.commons.collection.Properties
import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.annotation.response
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.{ExternSchool, Project}
import org.openurp.base.std.model.{ExternStudent, Student}
import org.openurp.code.edu.model.{EduCategory, EducationLevel}
import org.openurp.code.std.model.StudentStatus
import org.openurp.edu.extern.service.ExemptionService
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.starter.web.support.ProjectSupport

import java.time.format.DateTimeFormatter

class StudentAction extends RestfulAction[ExternStudent] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _

  var exemptionService: ExemptionService = _

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("studentStatuses", getCodes(classOf[StudentStatus]))
  }

  override def info(id: String): View = {
    put("externStudent", entityDao.get(classOf[ExternStudent], id.toLong))
    forward()
  }

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[Student], "std")
    query.where("std.code=:code", get("q", ""))
    val yyyyMM = DateTimeFormatter.ofPattern("yyyy-MM")
    entityDao.search(query).map { std =>
      val p = new Properties()
      p.put("id", std.id)
      p.put("name", s"${std.state.get.department.name} ${std.name}")
      p
    }
  }

  override protected def editSetting(entity: ExternStudent): Unit = {
    put("schools", entityDao.getAll(classOf[ExternSchool]))
    val project = getProject
    put("levels", entityDao.getAll(classOf[EducationLevel]))
    put("categories", entityDao.getAll(classOf[EduCategory]))
    put("project", project)
    super.editSetting(entity)
  }

}
