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

package org.openurp.edu.extern.web.action.exempt

import org.beangle.ems.app.Ems
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.edu.model.Course
import org.openurp.edu.extern.code.CertificateSubject
import org.openurp.edu.extern.config.{CertExemptConfig, CertExemptSetting}

class SettingAction extends RestfulAction[CertExemptSetting] {
  @ignore
  protected override def simpleEntityName: String = "setting"

  protected override def editSetting(setting: CertExemptSetting): Unit = {
    //refresh setting config
    val config = entityDao.get(classOf[CertExemptConfig], setting.config.id)
    setting.config = config

    put("project", config.project)
    val subjects = entityDao.getAll(classOf[CertificateSubject]).toBuffer
    subjects --= config.settings.map(_.subject)
    if (null != setting.subject) subjects += setting.subject

    put("subjects", subjects)
    put("urp", Ems)

    super.editSetting(setting)
  }

  override protected def saveAndRedirect(setting: CertExemptSetting): View = {
    val courseIds = longIds("course")
    setting.courses.clear()
    setting.courses ++= entityDao.find(classOf[Course], courseIds)
    super.saveAndRedirect(setting)
  }
}
