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
import org.beangle.webmvc.annotation.ignore
import org.beangle.webmvc.support.action.RestfulAction
import org.beangle.webmvc.view.View
import org.openurp.base.model.Project
import org.openurp.code.edu.model.{Certificate, CertificateCategory}
import org.openurp.edu.extern.config.CertSignupConfig
import org.openurp.starter.web.support.ProjectSupport

class ConfigAction extends RestfulAction[CertSignupConfig], ProjectSupport {

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("categories", getCodes(classOf[CertificateCategory]))
  }

  @ignore
  protected override def simpleEntityName: String = {
    "config"
  }

  protected override def editSetting(config: CertSignupConfig): Unit = {
    given project: Project = getProject

    put("semesters", project.calendar.semesters)
    val examCategories = getCodes(classOf[CertificateCategory])
    put("categories", examCategories)
    if (!config.persisted) {
      config.prediction = false
    }

    if (examCategories.isEmpty) {
      put("certificates", Map.empty)
    } else {
      val query = OqlBuilder.from(classOf[Certificate], "cert")
      query.where("cert.category in (:categories)", examCategories)
      query.where("cert.endOn is null")
      val certificates = entityDao.search(query)
      put("certificates", certificates.groupBy(_.category.id))
    }
  }

  override protected def saveAndRedirect(config: CertSignupConfig): View = {
    val query = OqlBuilder.from(classOf[CertSignupConfig], "config")
    query.where("config.name = :configName", config.name)
    query.where("config.semester = :semester", config.semester)

    val configs = entityDao.search(query)
    if (configs.nonEmpty && !config.persisted) {
      return redirect("edit", "期号名称重复")
    }
    config.project = getProject
    entityDao.saveOrUpdate(config)
    entityDao.evict(classOf[CertSignupConfig])
    super.saveAndRedirect(config)
  }
}
