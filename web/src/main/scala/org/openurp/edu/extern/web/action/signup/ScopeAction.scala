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
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.edu.extern.code.model.CertificateSubject
import org.openurp.edu.extern.model.{CertExamSignupConfig, CertExamSignupSetting, CertExamStdScope}
import org.openurp.starter.edu.helper.ProjectSupport

import scala.collection.mutable

class ScopeAction extends RestfulAction[CertExamStdScope] with ProjectSupport {

  override protected def editSetting(scope: CertExamStdScope): Unit = {
    getSignupConfig()
    put("levels", getProject.levels)
    put("codes", scope.codes.mkString(","))
    forward()
  }

  private def getSignupConfig(): CertExamSignupConfig = {
    getLong("config.id") match {
      case Some(id) =>
        val config = entityDao.get(classOf[CertExamSignupConfig], id)
        if (config != null) {
          put("config", config)
        }
        config
      case None => null
    }
  }

  override protected def saveAndRedirect(entity: CertExamStdScope): View = {
    get("codes") foreach { c =>
      var codes = Strings.replace(c, "\r", "")
      codes = Strings.replace(c, "\n", ",")
      codes = Strings.replace(c, "，", ",")
      entity.codes.clear()
      entity.codes ++= Strings.split(codes)
    }
    super.saveAndRedirect(entity)
  }

  @ignore
  protected override def simpleEntityName: String = {
    "scope"
  }
}
