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

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.annotation.ignore
import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.code.edu.model.EducationLevel
import org.openurp.edu.exempt.config.CertExemptConfig
import org.openurp.starter.web.support.ProjectSupport

class ConfigAction extends RestfulAction[CertExemptConfig] with ProjectSupport {

  @ignore
  protected override def simpleEntityName: String = {
    "config"
  }

  protected override def indexSetting(): Unit = {
    put("project", getProject)
    super.indexSetting()
  }

  override protected def getQueryBuilder: OqlBuilder[CertExemptConfig] = {
    val query = super.getQueryBuilder
    query.where("config.project=:project", getProject)
    query
  }

  override protected def saveAndRedirect(entity: CertExemptConfig): View = {
    entity.project = getProject
    entity.levels.clear()
    entity.levels.addAll(entityDao.find(classOf[EducationLevel], getIntIds("level")))
    super.saveAndRedirect(entity)
  }

  protected override def editSetting(config: CertExemptConfig): Unit = {
    put("project", getProject)
  }
}
