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

package org.openurp.edu.extern.web.action.signup

import org.beangle.commons.lang.Strings
import org.beangle.commons.logging.Logging
import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View
import org.beangle.web.servlet.util.RequestUtils
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.edu.model.Student
import org.openurp.edu.extern.model.{CertExamSignup, CertExamSignupSetting, CertificateGrade}
import org.openurp.edu.extern.service.signup.CertSignupService
import org.openurp.starter.edu.helper.ProjectSupport

import java.time.Instant
import scala.collection.mutable

class StdAction extends ActionSupport with EntityAction[CertExamSignup] with ProjectSupport with Logging {

  var examSignupService: CertSignupService = _

  def index(): View = {
    val std = getUser(classOf[Student])
    val builder = OqlBuilder.from(classOf[CertExamSignup], "signUp").where("signUp.std =:std", std)
    val signUpList = entityDao.search(builder)
    val gradeBuilder = OqlBuilder.from(classOf[CertificateGrade], "grade").where("grade.std =:std", std)
    put("grades", entityDao.search(gradeBuilder))
    put("signUps", signUpList)
    forward()
  }

  /**
   * 列举出可以报名的期号设置(操作第一步)
   */
  def configs(): View = {
    val std = getUser(classOf[Student])
    // 可以开放的期号设置
    val configs = examSignupService.getOpenedConfigs(std.project)
    if (configs.isEmpty) {
      forward("noconfig")
    } else {
      val signUpList = new mutable.ArrayBuffer[CertExamSignup]
      configs foreach { config =>
        val signUps = examSignupService.search(std, config)
        signUpList ++= signUps
      }
      if (!signUpList.isEmpty) put("signUpSubjects", signUpList.map(_.subject).toSet)
      // 查询已有成绩
      val grades = entityDao.findBy(classOf[CertificateGrade], "std", List(std))
      val passedSubjects = grades.filter(_.passed).map(_.subject)

      put("passedSubjects", passedSubjects)
      put("signUpList", signUpList)
      put("configs", configs)
      put("student", std)
      forward()
    }
  }

  /**
   * 显示报名须知(操作第二步)
   */
  def notice(): View = {
    val setting = entityDao.get(classOf[CertExamSignupSetting], longId("setting"))
    val std = getUser(classOf[Student])
    val msg = examSignupService.canSignup(std, setting)
    if (null != msg) {
      //FIXME 如果configs页面提交过来，又要重定向到configs页面,相同的内容firefox不会再次渲染，所以增加了一个随机参数
      return redirect("configs", "&t=" + System.currentTimeMillis(), msg)
    }
    val config = setting.config
    if (Strings.isBlank(config.notice)) {
      forward(to(this, "signUpForm"))
    } else {
      put("config", config)
      forward()
    }
  }

  def signUpForm(): View = {
    val std = getUser(classOf[Student])
    val setting = entityDao.get(classOf[CertExamSignupSetting], longId("setting"))
    put("setting", setting)
    put("student", std)
    forward()
  }

  def save(): View = {
    val std = getUser(classOf[Student])
    val setting = entityDao.get(classOf[CertExamSignupSetting], longId("setting"))
    val config = setting.config
    if (!(config.isTimeSuitable && config.opened)) {
      return redirect("configs", "不在报名时间段内")
    }
    val signup = new CertExamSignup
    signup.std = std
    signup.updatedAt = Instant.now
    signup.ip = RequestUtils.getIpAddr(request)
    signup.semester = setting.config.semester
    signup.subject = setting.subject
    val project = std.project

    if (config.isTimeSuitable) {
      var msg = examSignupService.signup(signup, setting)
      if (null == msg) {
        msg = if (config.prediction) "预报名成功" else "报名成功"
        logger.info(std.user.code + " 报名 " + signup.subject.name + " @" + remoteAddr)
      }
      redirect("configs", msg)
    } else {
      redirect("configs", "不在报名时间内")
    }
  }

  def cancel(): View = {
    val signup = entityDao.get(classOf[CertExamSignup], longId("signup"))
    val std = getUser(classOf[Student])
    if (signup.std != std) {
      return redirect("configs", "非法操作，只能取消自己的报名信息!")
    }
    val configs = examSignupService.getOpenedConfigs(std.project)
    var openConfig = false
    for (config <- configs if !openConfig) {
      if (examSignupService.search(std, config).toSeq.contains(signup)) {
        openConfig = true
      }
    }
    if (openConfig) {
      entityDao.remove(signup)
      val remoteAddr = RequestUtils.getIpAddr(request)
      logger.info(std.user.code + " 取消了 " + signup.subject.name + " @" + remoteAddr)
      redirect("configs", "取消报名成功!")
    } else {
      redirect("configs", "不在报名时间内")
    }
  }

}
