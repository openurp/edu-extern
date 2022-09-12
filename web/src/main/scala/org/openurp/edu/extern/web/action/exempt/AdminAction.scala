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
import org.beangle.commons.lang.Numbers
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.security.Securities
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.AuditStatus
import org.openurp.code.edu.model.ExamStatus
import org.openurp.edu.extern.model.{CertExemptApply, CertificateGrade}
import org.openurp.edu.extern.service.{ExemptionCourse, ExemptionService}
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.starter.web.support.ProjectSupport

import java.time.Instant

class AdminAction extends RestfulAction[CertExemptApply] with ProjectSupport {

  protected override def simpleEntityName: String = "apply"

  var businessLogger: WebBusinessLogger = _
  var exemptionService: ExemptionService = _
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

  def audit(): View = {
    val applies = entityDao.find(classOf[CertExemptApply], longIds("apply"))
    val passed = getBoolean("passed", false)
    applies foreach { apply =>
      val grade = convert(apply)
      val ecs = Collections.newBuffer[ExemptionCourse]
      grade.courses foreach { c =>
        val ec = ExemptionCourse(c, coursePlanProvider.getCourseType(apply.std, c), apply.semester, c.examMode,
          grade.gradingMode, grade.score, apply.scoreText)
        ecs.addOne(ec)
      }

      var msg: String = null
      if (passed) {
        exemptionService.addExemption(grade, ecs.toSeq)
        apply.status = AuditStatus.Passed
        msg = s"${Securities.user}同意通过了${apply.std.code}的免修申请"
      } else {
        grade.courses foreach { c =>
          exemptionService.removeExemption(grade, c)
        }
        apply.status = AuditStatus.Rejected
        msg = s"${Securities.user}驳回了${apply.std.code}的免修申请"
      }
      entityDao.saveOrUpdate(apply)
      businessLogger.info(msg, apply.id, Map.empty)
    }
    redirect("search", "审批完成")
  }

  private def convert(apply: CertExemptApply): CertificateGrade = {
    val query = OqlBuilder.from(classOf[CertificateGrade], "cg")
    query.where("cg.std = :std", apply.std)
    query.where("cg.subject = :subject", apply.subject)
    query.where("cg.acquiredOn = :acquiredOn", apply.acquiredOn)
    val grades = entityDao.search(query)

    val grade = grades.headOption match {
      case None =>
        val g = new CertificateGrade
        g.std = apply.std
        g.subject = apply.subject
        g.acquiredOn = apply.acquiredOn
        g
      case Some(g) => g
    }

    grade.scoreText = apply.scoreText
    if (Numbers.isDigits(apply.scoreText)) {
      grade.score = Some(Numbers.toFloat(apply.scoreText))
    }
    grade.passed = true
    grade.certificate = apply.certificate
    grade.status = 2
    grade.examStatus = new ExamStatus(ExamStatus.Normal)
    grade.courses ++= apply.courses
    grade.gradingMode = apply.gradingMode
    grade.updatedAt = Instant.now
    entityDao.saveOrUpdate(grade)
    grade
  }
}
