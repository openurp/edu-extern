/*
 * Copyright (C) 2005, The OpenURP Software.
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

package org.openurp.edu.extern.service.signup.impl

import org.beangle.data.dao.OqlBuilder
import org.openurp.base.edu.model.Student
import org.openurp.edu.extern.model.{CertExamSignupSetting, CertificateGrade}
import org.openurp.edu.extern.service.CertificateGradeService
import org.openurp.edu.extern.service.signup.{CertSignupChecker, CertSignupService}

import java.time.LocalDate
import java.util
import java.util.List

class SignupBuildInChecker extends CertSignupChecker {
  var certificateGradeService: CertificateGradeService = _
  var examSignupService: CertSignupService = _

  override def check(student: Student, setting: CertExamSignupSetting): String = {
    //    if (!student.within(LocalDate.now)) {
    //      return "不在籍同学不能报名"
    //    }

    val exclusive = setting.scopes.exists(s => s.matchStd(student) && !s.includeIn)
    if (exclusive) return "不在报名许可名单中"
    val inclusive = setting.scopes.exists(s => s.matchStd(student) && s.includeIn)
    if (!inclusive) return "不在报名许可名单中"

    if (setting.dependsOn.nonEmpty) {
      if (!certificateGradeService.isPass(student, setting.dependsOn.get)) return "尚未通过先修科目"
    }

    if (examSignupService.get(student, setting).nonEmpty) {
      return "不能重复报名"
    }

    if (isTimeCollision(setting, student)) {
      return "考试时间冲突"
    }

    if (!setting.reExamAllowed) {
      if (certificateGradeService.isPass(student, setting.subject)) {
        return setting.subject.name + "已通过，无需报名"
      }
    }
    null
  }

  private def isTimeCollision(setting: CertExamSignupSetting, student: Student): Boolean = {
    setting.examOn match {
      case None => false
      case Some(e) =>
        val config = setting.config
        val signed = examSignupService.search(student, config)
        signed exists { signup =>
          config.getSetting(signup.subject).find(_.isTimeCollision(setting)).nonEmpty
        }
    }
  }
}