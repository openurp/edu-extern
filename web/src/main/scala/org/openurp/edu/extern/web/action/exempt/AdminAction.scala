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

import org.beangle.commons.collection.Collections
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.EmsApp
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.security.Securities
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.AuditStatus
import org.openurp.edu.extern.model.{CertExemptApply, CertificateGrade}
import org.openurp.edu.extern.service.CertExemptApplyService
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.starter.web.support.ProjectSupport

class AdminAction extends RestfulAction[CertExemptApply] with ProjectSupport {

  protected override def simpleEntityName: String = "apply"

  var businessLogger: WebBusinessLogger = _
  var certExemptApplyService: CertExemptApplyService = _
  var coursePlanProvider: CoursePlanProvider = _

  private val statuses = List(AuditStatus.Submited, AuditStatus.PassedByDepart, AuditStatus.RejectedByDepart,
    AuditStatus.Passed, AuditStatus.Rejected)

  override protected def indexSetting(): Unit = {
    put("statuses", statuses)
    super.indexSetting()
  }

  override protected def getQueryBuilder: OqlBuilder[CertExemptApply] = {
    val query = super.getQueryBuilder
    query.where("apply.std.project=:project", getProject)
    query.where("apply.status in(:statusList)", statuses)
    query
  }

  def auditForm(): View = {
    val apply = getEntity(classOf[CertExemptApply], "apply")
    put("apply", apply)
    put("editables", Set(AuditStatus.Submited, AuditStatus.PassedByDepart, AuditStatus.RejectedByDepart, AuditStatus.Passed, AuditStatus.Rejected))
    val repo = EmsApp.getBlobRepository(true)
    put("attachmentPath", repo.url(apply.attachmentPath))
    forward()
  }

  def audit(): View = {
    val applies = entityDao.find(classOf[CertExemptApply], longIds("apply"))
    val passed = getBoolean("passed", false)
    applies foreach { apply =>
      var msg: String = null
      if (passed) {
        certExemptApplyService.accept(apply)
        msg = s"${Securities.user}同意通过了${apply.std.code}的免修申请"
      } else {
        certExemptApplyService.reject(apply)
        msg = s"${Securities.user}驳回了${apply.std.code}的免修申请"
      }
      businessLogger.info(msg, apply.id, Map.empty)
    }
    redirect("search", "审批完成")
  }

}
