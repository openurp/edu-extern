package org.openurp.edu.extern.exchange.service

import java.time.LocalDate

import org.openurp.code.edu.model.{ExamMode, GradingMode}
import org.openurp.edu.base.code.model.CourseType
import org.openurp.edu.base.model.{Course, Semester, Student}
import org.openurp.edu.extern.model.{CertificateGrade, ExchangeGrade}
import org.openurp.edu.grade.course.model.CourseGrade
import org.openurp.edu.program.model.{CoursePlan, PlanCourse}

trait ExemptionService {

  def getSemester(std: Student, acquiredOn: LocalDate, term: Option[Int]): Option[Semester]

  def getConvertablePlanCourses(std: Student, plan: CoursePlan, acquiredOn: LocalDate): Seq[PlanCourse]

  def getConvertedGrades(std: Student, courses: collection.Iterable[Course]): Seq[CourseGrade]

  def recalcExemption(std: Student): Unit

  def removeExemption(cg: CertificateGrade, course: Course): Unit

  def removeExemption(eg: ExchangeGrade, course: Course): Unit

  def addExemption(eg: ExchangeGrade, ecs: Seq[ExemptionCourse]): Unit

  def addExemption(cg: CertificateGrade, ecs: Seq[ExemptionCourse]): Unit
}

case class ExemptionCourse(course: Course, courseType: CourseType, semester: Semester,
                           examMode: ExamMode, gradingMode: GradingMode, score: Option[Float], scoreText: Option[String])
