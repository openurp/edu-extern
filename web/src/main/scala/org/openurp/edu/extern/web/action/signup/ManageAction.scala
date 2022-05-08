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

import org.beangle.data.dao.OqlBuilder
import org.beangle.data.transfer.exporter.ExportSetting
import org.beangle.web.action.annotation.ignore
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.Semester
import org.openurp.edu.extern.code.model.{CertificateCategory, CertificateSubject}
import org.openurp.edu.extern.model.CertExamSignup
import org.openurp.edu.extern.web.helper.PETSPropertyExtractor
import org.openurp.starter.edu.helper.ProjectSupport

class ManageAction extends RestfulAction[CertExamSignup] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    put("project", getProject)
    val semester = getId("semester") match {
      case Some(sid) => entityDao.get(classOf[Semester], sid.toInt)
      case None => getCurrentSemester
    }
    put("currentSemester", semester)
    put("departments", getDeparts)
    put("categories", getCodes(classOf[CertificateCategory]))
    put("subjects", getCodes(classOf[CertificateSubject]))
    super.indexSetting()
  }

  @ignore
  protected override def simpleEntityName: String = {
    "signup"
  }

  @ignore
  protected override def configExport(setting: ExportSetting): Unit = {
    super.configExport(setting)
    setting.context.extractor = new PETSPropertyExtractor(entityDao)
  }
}
