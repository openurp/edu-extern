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

import org.beangle.commons.collection.Order
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.edu.extern.code.model.CertificateSubject
import org.openurp.edu.extern.model.{CertExamSignupConfig, CertExamSignupSetting}
import org.openurp.starter.edu.helper.ProjectSupport

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * 考试科目设置
 *
 * @author chaostone
 */
class SettingAction extends RestfulAction[CertExamSignupSetting] with ProjectSupport {
  /**
   * 新增和修改
   */
  override protected def editSetting(setting: CertExamSignupSetting): Unit = {
    val config = getSignupConfig()
    val categoryId = config.category.id
    // 查询报考科目
    val query = OqlBuilder.from(classOf[CertificateSubject], "subject")
    query.where("subject.category.id = :categoryId", categoryId)
    query.where("not exists (from " + classOf[CertExamSignupSetting].getName +
      " setting where setting.subject.id =subject.id and setting.config.id =:configId)", config.id)
    val set = new mutable.HashSet[CertificateSubject]
    val subjects = entityDao.search(query)
    set.addAll(subjects)
    if (setting.persisted) set.add(setting.subject)
    else setting.config = config
    put("subjects", set)
    // 查询必须通过的科目
    val query2 = OqlBuilder.from(classOf[CertificateSubject], "subject")
    query2.where("subject.category.id=:categoryId", categoryId)
    put("dependsOn", entityDao.search(query2))
  }

  override def search(): View = {
    val config = getSignupConfig()
    val query = OqlBuilder.from(classOf[CertExamSignupSetting], "setting")
    query.where("setting.config=:config", config)
    populateConditions(query)
    query.limit(getPageLimit)
    query.orderBy(Order.parse(get("orderBy").orNull))
    put("settings", entityDao.search(query))
    forward()
  }

  def batchEdit(): View = {
    put("settings", entityDao.find(classOf[CertExamSignupSetting], longIds("setting")))
    getSignupConfig()
    forward()
  }

  private def getSignupConfig(): CertExamSignupConfig = {
    var configId = getLong("config.id")
    if (configId.isEmpty) configId = getLong("setting.config.id")
    configId match {
      case Some(id) =>
        val config = entityDao.get(classOf[CertExamSignupConfig], id)
        if (config != null) {
          put("config", config)
        }
        config
      case None => null
    }
  }

  def batchSave(): View = {
    val settingSize = getInt("settingSize").get
    val settings = new ArrayBuffer[CertExamSignupSetting]
    for (i <- 0 until settingSize) {
      settings.addOne(populateEntity(classOf[CertExamSignupSetting], "setting" + i))
    }
    val config = getSignupConfig()
    entityDao.saveOrUpdate(settings)
    redirect("search", "&setting.config.id=" + config.id, "info.save.success")
  }

  @ignore
  protected override def simpleEntityName: String = {
    "setting"
  }

}
