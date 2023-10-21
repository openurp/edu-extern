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

package org.openurp.edu.extern.web.action.signup

import org.beangle.data.transfer.exporter.ExportContext
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.Project
import org.openurp.edu.extern.code.{CertificateCategory, CertificateSubject}
import org.openurp.edu.extern.model.CertSignup
import org.openurp.edu.extern.web.helper.PETSPropertyExtractor
import org.openurp.starter.web.support.ProjectSupport

import scala.util.Random

class ManageAction extends RestfulAction[CertSignup], ExportSupport[CertSignup], ProjectSupport {

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("project", project)
    put("currentSemester", getSemester)
    put("departments", getDeparts)
    put("categories", getCodes(classOf[CertificateCategory]))
    put("subjects", getCodes(classOf[CertificateSubject]))
    super.indexSetting()
  }

  def batchUpdateExamRoom(): View = {
    val signups = entityDao.find(classOf[CertSignup], getLongIds("signup"))
    val examRoom = get("examRoom")
    val head = signups.head
    signups foreach { signup =>
      signup.examRoom = examRoom
    }
    entityDao.saveOrUpdate(signups)
    examRoom.foreach { room =>
      var roomSigns = entityDao.findBy(classOf[CertSignup], "semester" -> head.semester, "subject" -> head.subject, "examRoom" -> examRoom.get)
      roomSigns = Random.shuffle(roomSigns)
      var i = 1;
      roomSigns.foreach { r => r.seatNo = i; i += 1 }
      entityDao.saveOrUpdate(roomSigns)
    }

    redirect("search", "info.save.success")
  }

  @ignore
  protected override def simpleEntityName: String = {
    "signup"
  }

  @ignore
  protected override def configExport(context: ExportContext): Unit = {
    super.configExport(context)
    context.extractor = new PETSPropertyExtractor(entityDao)
  }
}
