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

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.annotation.ignore
import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.std.model.Student
import org.openurp.code.edu.model.Certificate
import org.openurp.edu.extern.config.{CertSignupConfig, CertSignupScope, CertSignupSetting}
import org.openurp.starter.web.support.ProjectSupport

import scala.collection.mutable

class ScopeAction extends RestfulAction[CertSignupScope] with ProjectSupport {

  override protected def editSetting(scope: CertSignupScope): Unit = {
    getSignupConfig()
    put("levels", getProject.levels)
    forward()
  }

  override protected def getQueryBuilder: OqlBuilder[CertSignupScope] = {
    val query = super.getQueryBuilder
    val config = getSignupConfig()
    query.where("scope.setting.config=:config", config)
    query
  }

  private def getSignupConfig(): CertSignupConfig = {
    getLong("config.id") match {
      case Some(id) =>
        val config = entityDao.get(classOf[CertSignupConfig], id)
        if (config != null) {
          put("config", config)
        }
        config
      case None => null
    }
  }

  override protected def saveAndRedirect(entity: CertSignupScope): View = {
    entity.codes = None

    get("scope.codes") foreach { c =>
      var codes = Strings.replace(c, "\r", "")
      codes = Strings.replace(c, "\n", ",")
      codes = Strings.replace(c, "，", ",")
      val query = OqlBuilder.from(classOf[Student], "std")
      query.where("std.project=:project", getProject)
      query.where("std.code in(:codes)", Strings.split(codes).map(_.trim))
      query.where("std.level=:level", entity.level)
      query.orderBy("std.code")
      val stds = entityDao.search(query)
      val validCodes = stds.map(_.code).mkString(",")
      if Strings.isNotBlank(validCodes) then entity.codes = Some(validCodes)
    }
    super.saveAndRedirect(entity)
  }

  @ignore
  protected override def simpleEntityName: String = {
    "scope"
  }
}
