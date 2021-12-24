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

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.base.edu.model.{Project, Student}
import org.openurp.edu.extern.model.{CertExamSignup, CertExamSignupConfig, CertExamSignupSetting}
import org.openurp.edu.extern.service.signup.{CertSignupChecker, CertSignupService}

import java.time.LocalDate
import java.time.Instant
import scala.collection.mutable

class DefaultCertSignupService extends CertSignupService {
  var entityDao: EntityDao = _
  var checkerStack: mutable.Buffer[CertSignupChecker] = mutable.ArrayBuffer.empty

  /**
   * 报名
   */
  override def signup(signup: CertExamSignup, setting: CertExamSignupSetting): String = {
    val msg = canSignup(signup.std, setting)
    if (Strings.isEmpty(msg)) { // 检查科目设置中的最大人数
      if (setting.maxStd >= 0) {
        setting.synchronized {
          val countStd = getSignupCount(setting)
          if (setting.maxStd <= countStd) {
            return "超过最大报名人数"
          }
        }
      }
      signup.semester = setting.config.semester
      signup.subject = setting.subject
      signup.updatedAt = Instant.now
      entityDao.saveOrUpdate(signup)
    }
    msg
  }

  private def getSignupCount(setting: CertExamSignupSetting): Int = {
    val query = OqlBuilder.from[Number](classOf[CertExamSignup].getName, "signup")
    query.where("signup.semester = :semester", setting.config.semester)
    query.where("signup.subject = :subject", setting.subject)
    query.select("count(*)")
    entityDao.search(query).head.intValue
  }

  /**
   * 判断是否能够报名，检查报名条件
   */
  override def canSignup(student: Student, setting: CertExamSignupSetting): String = {
    var msg: String = null
    for (checker <- checkerStack if null == msg) {
      msg = checker.check(student, setting)
    }
    msg
  }

  override def cancel(std: Student, setting: CertExamSignupSetting): String = { // 判断时间
    if (!setting.config.opened || !setting.config.isTimeSuitable) {
      CertSignupChecker.notInTime
    } else {
      get(std, setting) foreach { signup =>
        entityDao.remove(signup)
      }
      null
    }
  }

  override def get(std: Student, setting: CertExamSignupSetting): Option[CertExamSignup] = {
    val config = setting.config
    val query = OqlBuilder.from(classOf[CertExamSignup], "signup")
    query.where("signup.std = :std", std)
    query.where("signup.updatedAt between :start and :end", config.beginAt, config.endAt)
    query.where("signup.subject = :subject", setting.subject)
    entityDao.search(query).headOption
  }

  override def search(std: Student, start: LocalDate, end: LocalDate): Iterable[CertExamSignup] = {
    val query = OqlBuilder.from(classOf[CertExamSignup], "signup")
    query.where("signup.std = :std", std)
    query.where("signup.updatedAt between :start and :end", start, end)
    entityDao.search(query)
  }

  /**
   * 获得这次开放的期号中某个学生的所有报名记录
   */
  override def search(std: Student, config: CertExamSignupConfig): Iterable[CertExamSignup] = {
    val query = OqlBuilder.from(classOf[CertExamSignup], "signup")
    query.where("signup.semester = :semester", config.semester)
    query.where("signup.std = :std", std)
    query.where("signup.subject in (:subjects)", config.subjects)
    entityDao.search(query)
  }

  override def getOpenedSettings(project: Project): Iterable[CertExamSignupSetting] = {
    val query = OqlBuilder.from(classOf[CertExamSignupSetting], "setting")
    query.where("setting.config.opened = true")
    query.where("setting.config.project = :project", project)
    query.where(":now  between setting.config.beginAt and setting.config.endAt", Instant.now)
    entityDao.search(query)
  }

  /**
   * 根据考试类型id来获得某个考试类型当前开放的期号
   */
  override def getOpenedConfigs(project: Project): Iterable[CertExamSignupConfig] = {
    val query = OqlBuilder.from(classOf[CertExamSignupConfig], "config")
    query.where("config.project = :project", project)
    query.where("config.opened = true")
    query.where(":time between config.beginAt and config.endAt ", Instant.now)
    entityDao.search(query)
  }

  override def isExist(signup: CertExamSignup): Boolean = {
    val builder  = OqlBuilder.from(classOf[CertExamSignup], "signup")
    builder.where("signup.subject  =:subject", signup.subject)
    builder.where("signup.semester  =:semester", signup.semester)
    builder.where("signup.std =:std", signup.std)
    if (signup.persisted) builder.where("signup.id <>:id", signup.id)
    entityDao.search(builder).nonEmpty
  }
}
