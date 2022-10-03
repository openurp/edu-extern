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

package org.openurp.edu.extern.web.helper

import org.beangle.commons.collection.Collections
import org.beangle.commons.conversion.impl.DefaultConversion
import org.beangle.commons.lang.{Numbers, Strings}
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.data.transfer.importer.{ImportListener, ImportResult}
import org.openurp.base.edu.model.Course
import org.openurp.base.model.Project
import org.openurp.code.edu.model.ExamStatus
import org.openurp.edu.extern.code.CertificateSubject
import org.openurp.edu.extern.model.CertificateGrade
import org.openurp.edu.extern.service.ExemptionService
import org.openurp.edu.program.domain.CoursePlanProvider

import java.time.{Instant, LocalDate}

class CertificateGradeImportListener(entityDao: EntityDao, project: Project, exemptionService: ExemptionService) extends ImportListener {

  override def onItemStart(tr: ImportResult): Unit = {
    val data = transfer.curData
    for (code <- data.get("certificateGrade.std.code"); subjectCode <- data.get("certificateGrade.subject.code"); acquiredOn <- data.get("certificateGrade.acquiredOn")) {
      val q = if subjectCode.toString.contains(" ") then Strings.substringBefore(subjectCode.toString, " ") else subjectCode.toString
      val sQuery = OqlBuilder.from(classOf[CertificateSubject], "cs")
      sQuery.where("cs.code = :q or cs.name = :q", q)
      sQuery.cacheable()
      val subjects = entityDao.search(sQuery)
      if (subjects.size == 1) {
        val acquiredOnDate = DefaultConversion.Instance.convert(acquiredOn, classOf[LocalDate])
        val query = OqlBuilder.from(classOf[CertificateGrade], "cg")
        query.where("cg.std.project = :project", project)
        query.where("cg.std.code = :stdCode", code)
        query.where("cg.subject = :subject", subjects.head)
        query.where("cg.acquiredOn = :acquiredOn", acquiredOnDate)
        val grades = entityDao.search(query)
        if (grades.nonEmpty) transfer.current = grades.head
      }
    }
  }

  override def onItemFinish(tr: ImportResult): Unit = {
    val grade = transfer.current.asInstanceOf[CertificateGrade]
    if null == grade.examStatus then grade.examStatus = new ExamStatus(ExamStatus.Normal)
    if (Numbers.isDigits(grade.scoreText)) {
      if (null == grade.score || grade.score.isEmpty) {
        grade.score = Some(Numbers.toFloat(grade.scoreText))
      }
    }
    grade.updatedAt = Instant.now
    entityDao.saveOrUpdate(grade)

    transfer.curData.get("courseCodes") foreach { courseCodes =>
      if (null != courseCodes && Strings.isNotBlank(courseCodes.toString)) {
        val codeList = Strings.split(courseCodes.toString)
        val courseSets = Collections.newSet[Course]
        codeList foreach { courseCode =>
          val cQuery = OqlBuilder.from(classOf[Course], "c")
          cQuery.where("c.project=:project", project)
          cQuery.where("c.code=:code", courseCode)
          val courses = entityDao.search(cQuery)
          if (courses.size == 1) {
            courseSets ++= courses
          } else {
            tr.addFailure("找不到课程代码", courseCode)
          }
        }
        if codeList.size == courseSets.size then exemptionService.addExemption(grade, courseSets)
      }
    }
  }
}
