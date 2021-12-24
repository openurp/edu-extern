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
import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.edu.extern.code.model.{CertificateCategory, CertificateSubject}
import org.openurp.edu.extern.model.CertExamSignupConfig
import org.openurp.starter.edu.helper.ProjectSupport

import scala.collection.mutable

class ConfigAction extends RestfulAction[CertExamSignupConfig] with ProjectSupport {

  override def indexSetting(): Unit = {
    put("categories", getCodes(classOf[CertificateCategory]))
  }

  @ignore
  protected override def simpleEntityName: String = {
    "config"
  }

  protected override def editSetting(config: CertExamSignupConfig): Unit = {
    put("semesters", getProject.calendars.head.semesters)
    val examCategories = getCodes(classOf[CertificateCategory])
    put("categories", examCategories)
    if (!config.persisted) {
      config.opened = true
      config.prediction = false
    }
    if (config.category != null) {
      val query = OqlBuilder.from(classOf[CertificateSubject], "subject").where("subject.category=:category", config.category)
      put("subjects", entityDao.search(query))
    } else {
      if (examCategories.isEmpty) {
        put("categorySubjects", Map.empty)
      } else {
        val categorySubjects = new mutable.HashMap[Int, mutable.ArrayBuffer[CertificateSubject]]
        val subjects = entityDao.findBy(classOf[CertificateSubject], "category", examCategories)
        subjects foreach { examSubject =>
          val oneCategorySubjects = categorySubjects.getOrElseUpdate(examSubject.category.id, new mutable.ArrayBuffer[CertificateSubject])
          oneCategorySubjects += examSubject
        }
        put("categorySubjects", categorySubjects)
      }
    }
  }

  override protected def saveAndRedirect(config: CertExamSignupConfig): View = {
    val query = OqlBuilder.from(classOf[CertExamSignupConfig], "config")
    query.where("config.code = :configCode", config.code)
    query.where("config.name = :configName", config.name)
    val configs = entityDao.search(query)
    if (configs.nonEmpty && !config.persisted) {
      return redirect("edit", "期号名称重复")
    }
    config.project = getProject
    super.saveAndRedirect(config)
  }
  //
  //  public String save() throws ParseException {
  //    ExamSignupConfig config = (ExamSignupConfig) populateEntity()

  //    String campusIdSeq = get("selectCampus")
  //    config.getCampuses().clear()
  //    if (Strings.isNotEmpty(campusIdSeq)) {
  //      config.addCampuses(entityDao.get(Campus.
  //      class, Strings.splitToInt(campusIdSeq)
  //      ) )
  //    }
  //    config.setProject(getProject())
  //    boolean createDefaultSubject = getBool("createDefaultSubject")
  //    if (createDefaultSubject) {
  //      examSignupConfigService.configDefaultSubject(
  //        entityDao.get(ExamCategory.
  //      class, getInt("examSignupConfig.category.id")
  //      ), config
  //      )
  //    }
  //    // 生成考试科目
  //    config.getExclusiveSubjects().clear()
  //    Collection < ExclusiveSubject > exclusiveList = CollectUtils.newArrayList()
  //    String subjectOneString = get("subjectOne")
  //    String subjectTwoString = get("subjectTwo")
  //    String[] subjectOnes = Strings.split(subjectOneString, ",")
  //    String[] subjectTwos = Strings.split(subjectTwoString, ",")
  //    if (null != subjectOnes && null != subjectTwos) {
  //      for (int i
  //      = 0
  //      i < subjectOnes.length
  //      i ++
  //      )
  //      {
  //        Integer subjectOneId = new Integer(subjectOnes[i])
  //        ExamSubject subjectOne = entityDao.get(ExamSubject.
  //        class, subjectOneId
  //        )
  //        for (int j
  //        = 0
  //        j < subjectTwos.length
  //        j ++
  //        )
  //        {
  //          Integer subjectTwoId = new Integer(subjectTwos[j])
  //          ExamSubject subjectTwo = entityDao.get(ExamSubject.
  //          class, subjectTwoId
  //          )
  //          ExclusiveSubject exclusive = Model.newInstance(ExclusiveSubject.
  //          class)
  //          exclusive.setSubjectOne(subjectOne)
  //          exclusive.setSubjectTwo(subjectTwo)
  //          exclusive.setConfig(config)
  //          exclusiveList.add(exclusive)
  //        }
  //      }
  //    }
  //    config.getExclusiveSubjects().addAll(exclusiveList)
  //    try {
  //      entityDao.saveOrUpdate(config)
  //      return redirect("search", "info.save.success")
  //    } catch (Exception e) {
  //      logger.info("saveAndForwad failure", e)
  //      return redirect("search", "info.save.failure")
  //    }
  //  }
  //
  //  public String getExternExamSubjects() {
  //    Long categoryId = getLong("categoryId")
  //    if (categoryId != null) {
  //      OqlBuilder < ExamSubject > query = OqlBuilder.from(ExamSubject.
  //      class, "subject"
  //      )
  //      query.where("subject.category.id =:categoryId", categoryId)
  //      List < ExamSubject > subjects = entityDao.search(query)
  //      put("datas", subjects)
  //    } else {
  //      put("datas", Collections.emptyList())
  //    }
  //    return forward("examSubject")
  //  }
}
