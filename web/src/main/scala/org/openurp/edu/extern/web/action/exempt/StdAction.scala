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

import jakarta.servlet.http.Part
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.ems.app.EmsApp
import org.beangle.ems.app.log.BusinessLogProto.BusinessLogEvent
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.edu.model.Course
import org.openurp.base.model.{AuditStatus, Project}
import org.openurp.base.service.SemesterService
import org.openurp.base.std.model.Student
import org.openurp.code.edu.model.GradingMode
import org.openurp.code.service.CodeService
import org.openurp.edu.extern.code.CertificateSubject
import org.openurp.edu.extern.config.{CertExemptConfig, CertExemptSetting}
import org.openurp.edu.extern.model.{CertExemptApply, CertificateGrade}
import org.openurp.starter.web.support.StudentSupport

import java.time.Instant

class StdAction extends StudentSupport with EntityAction[CertExemptApply] {

  var businessLogger: WebBusinessLogger = _

  protected override def projectIndex(student: Student): View = {
    val cgQuery = OqlBuilder.from(classOf[CertificateGrade], "cg")
    cgQuery.where("size(cg.courses)>0")
    cgQuery.where("cg.std=:std", student)
    val grades = entityDao.search(cgQuery)
    put("grades", grades)

    val applies = entityDao.findBy(classOf[CertExemptApply], "std", student)
    put("applies", applies)
    val repo = EmsApp.getBlobRepository(true)
    val paths = applies.map(x => (x, repo.url(x.attachmentPath)))
    put("attachmentPaths", paths.toMap)

    put("editables", Set(AuditStatus.Draft, AuditStatus.Submited, AuditStatus.Rejected, AuditStatus.RejectedByDepart))

    val configQuery = OqlBuilder.from(classOf[CertExemptConfig], "config")
    configQuery.where("config.project=:project", student.project)
    configQuery.where("config.level=:level and config.eduType=:eduType", student.level, student.eduType)
    configQuery.where("config.endAt > :now ", Instant.now)
    put("configs", entityDao.search(configQuery))
    forward()
  }

  def edit(): View = {
    val setting = entityDao.get(classOf[CertExemptSetting], longId("setting"))
    val apply =
      getLong("apply.id") match {
        case None =>
          val ap = new CertExemptApply
          ap.std = getStudent()
          ap
        case Some(id) => entityDao.get(classOf[CertExemptApply], id)
      }

    given project: Project = apply.std.project

    val std = apply.std
    val courses = setting.courses.filter { x => x.levels.exists(l => l.level == std.level) }
    put("gradingModes", codeService.get(classOf[GradingMode]))
    put("courses", courses)
    if (courses.size == 1) {
      apply.courses.clear()
      apply.courses ++= courses
    }
    apply.subject = setting.subject
    put("setting", setting)
    put("apply", apply)
    forward()
  }

  def remove(): View = {
    val std = getStudent()
    val apply = entityDao.get(classOf[CertExemptApply], longId("apply"))
    if (apply.std == std) {
      if (apply.status == AuditStatus.Passed || apply.status == AuditStatus.PassedByDepart) {
        redirect("index", "已经审核通过，暂时不能删除")
      } else {
        val repo = EmsApp.getBlobRepository(true)
        if (Strings.isNotEmpty(apply.attachmentPath)) {
          repo.remove(apply.attachmentPath)
        }
        entityDao.remove(apply)
        val details = Map("scoreText" -> apply.scoreText, "acquiredOn" -> apply.acquiredOn.toString, "certificate" -> apply.certificate)
        businessLogger.info(s"${std.code} ${std.name}删除了${apply.subject.name}免修申请", apply.id, details)
        redirect("index", "删除成功")
      }
    } else {
      redirect("index", "删除成功")
    }
  }

  def save(): View = {
    val std = getStudent()
    val apply = populateEntity(classOf[CertExemptApply], "apply")
    val setting = entityDao.get(classOf[CertExemptSetting], longId("setting"))
    if (apply.status == AuditStatus.Passed || apply.status == AuditStatus.PassedByDepart) {
      redirect("index", "已经审核通过，暂时不能修改")
    } else if (!setting.config.within(Instant.now)) {
      redirect("index", "不在申请时间段内，暂时不能申请")
    } else {
      apply.std = std
      apply.updatedAt = Instant.now
      apply.subject = setting.subject
      apply.auditDepart = setting.auditDepart
      val courses = entityDao.find(classOf[Course], longIds("course"))
      if (courses.size <= setting.maxCount) {
        apply.courses.clear()
        apply.courses ++= courses
      }
      apply.semester = setting.config.semester

      val parts = getAll("attachment", classOf[Part])
      if (parts.nonEmpty && null != parts.head && parts.head.getSize > 0) {
        val repo = EmsApp.getBlobRepository(true)
        if (Strings.isNotEmpty(apply.attachmentPath)) {
          repo.remove(apply.attachmentPath)
        }
        val part = parts.head
        val meta = repo.upload("/extern/certificate", part.getInputStream,
          std.code + "_" + part.getSubmittedFileName, std.code + " " + std.name);
        apply.attachmentPath = meta.filePath
      }

      if (Strings.isNotBlank(apply.attachmentPath)) {
        apply.status = AuditStatus.Submited
        entityDao.saveOrUpdate(apply)
        val details = Map("scoreText" -> apply.scoreText, "acquiredOn" -> apply.acquiredOn.toString, "certificate" -> apply.certificate)
        businessLogger.info(s"${std.code} ${std.name}提交了${setting.subject.name}免修申请", apply.id, details)
        redirect("index", s"&projectId=${std.project.id}", "提交成功")
      } else {
        redirect("edit", s"&settingId=${setting.id}&projectId=${std.project.id}&apply.id=${apply.id}", "缺少附件")
      }
    }
  }
}
