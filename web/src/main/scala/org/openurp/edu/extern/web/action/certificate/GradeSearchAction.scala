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

package org.openurp.edu.extern.web.action.certificate

import org.beangle.commons.text.inflector.en.EnNounPluralizer
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.data.transfer.exporter.ExportContext
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{EntityAction, ExportSupport}
import org.openurp.base.model.Project
import org.openurp.code.edu.model.{Certificate, CertificateCategory}
import org.openurp.edu.extern.model.CertificateGrade
import org.openurp.edu.extern.web.helper.CertificateGradePropertyExtractor
import org.openurp.starter.web.support.ProjectSupport

import java.time.YearMonth

class GradeSearchAction extends ActionSupport, EntityAction[CertificateGrade], ExportSupport[CertificateGrade], ProjectSupport {

  var entityDao: EntityDao = _

  def index(): View = {
    given project: Project = getProject

    put("certificateCategories", getCodes(classOf[CertificateCategory]))
    put("certificates", getCodes(classOf[Certificate]))
    put("departments", getDeparts)
    put("project", project)
    put("semester", getSemester)
    forward()
  }

  def search(): View = {
    put(EnNounPluralizer.pluralize(simpleEntityName), entityDao.search(getQueryBuilder))
    forward()
  }

  override def configExport(context: ExportContext): Unit = {
    super.configExport(context)
    context.extractor = new CertificateGradePropertyExtractor()
  }

  override protected def getQueryBuilder: OqlBuilder[CertificateGrade] = {
    val builder = super.getQueryBuilder
    getFloat("from") foreach { from => builder.where("certificateGrade.score >=:F", from) }
    getFloat("to") foreach { to => builder.where("certificateGrade.score <=:T", to) }
    getBoolean("hasCourse") foreach { hasCourse =>
      builder.where((if (hasCourse) "" else "not ") + "exists (from certificateGrade.exempts ec)")
    }
    get("acquiredOn", classOf[YearMonth]) foreach { ym =>
      builder.where("to_char(certificateGrade.acquiredOn,'yyyy-MM')=:acquiredOn", ym.toString)
    }
    builder
  }
}
