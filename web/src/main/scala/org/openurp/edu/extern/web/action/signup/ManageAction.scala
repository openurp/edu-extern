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
import org.openurp.base.model.{Project, Semester}
import org.openurp.edu.extern.code.{CertificateCategory, CertificateSubject}
import org.openurp.edu.extern.model.CertSignup
import org.openurp.edu.extern.web.helper.PETSPropertyExtractor
import org.openurp.starter.web.support.ProjectSupport

class ManageAction extends RestfulAction[CertSignup] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("project", project)
    put("currentSemester", getSemester)
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
