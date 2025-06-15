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
import org.beangle.doc.transfer.importer.{ImportListener, ImportResult}
import org.openurp.base.edu.model.Course
import org.openurp.base.model.Project
import org.openurp.base.service.SemesterService
import org.openurp.code.edu.model.{Certificate, ExamStatus}
import org.openurp.edu.exempt.service.ExemptionService
import org.openurp.edu.extern.model.CertificateGrade

import java.time.{Instant, YearMonth}

class CertificateGradeImportListener(entityDao: EntityDao, project: Project,
                                     exemptionService: ExemptionService, semesterService: SemesterService) extends ImportListener {

  override def onItemStart(tr: ImportResult): Unit = {
    val data = transfer.curData
    for (code <- data.get("certificateGrade.std.code"); certificateCode <- data.get("certificateGrade.certificate.code"); acquiredIn <- data.get("certificateGrade.acquiredIn")) {
      val q = if certificateCode.toString.contains(" ") then Strings.substringBefore(certificateCode.toString, " ") else certificateCode.toString
      val sQuery = OqlBuilder.from(classOf[Certificate], "cs")
      sQuery.where("cs.code = :q or cs.name = :q", q)
      sQuery.cacheable()
      val certificates = entityDao.search(sQuery)
      if (certificates.size == 1) {
        val acquired = DefaultConversion.Instance.convert(acquiredIn, classOf[YearMonth])
        val query = OqlBuilder.from(classOf[CertificateGrade], "cg")
        query.where("cg.std.project = :project", project)
        query.where("cg.std.code = :stdCode", code)
        query.where("cg.certificate = :certificate", certificates.head)
        query.where("cg.acquiredIn = :acquiredIn", acquired)
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
    grade.semester = semesterService.get(project, grade.acquiredIn.atDay(1))
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
        if codeList.size == courseSets.size then
          exemptionService.addExemption(grade, courseSets, exemptionService.calcExemptScore(grade))
      }
    }
  }
}
