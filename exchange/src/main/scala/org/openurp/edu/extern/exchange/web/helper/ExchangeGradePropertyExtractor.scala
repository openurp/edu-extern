package org.openurp.edu.extern.exchange.web.action

import java.time.format.DateTimeFormatter

import org.beangle.data.transfer.exporter.DefaultPropertyExtractor
import org.openurp.edu.extern.model.ExchangeGrade

class ExchangeGradePropertyExtractor extends DefaultPropertyExtractor {
  override def getPropertyValue(target: Object, property: String): Any = {
    val eg = target.asInstanceOf[ExchangeGrade]
    property match {
      case "courseCode" => "01"
      case "creditHours" => "0"
      case "acquiredOn" => DateTimeFormatter.ofPattern("yyyyMM").format(eg.acquiredOn)
      case "courseCodes" => eg.courses.map(c => s"${c.code}").mkString("\r\n")
      case "courseNames" => eg.courses.map(c => s"${c.name}").mkString("\r\n")
      case "courseCredits" => eg.courses.map(c => s"${c.credits}").mkString("\r\n")
      case "courses" =>
        if (eg.courses.isEmpty) {
          "--"
        } else {
          eg.courses.map(c => s"${c.name} ${c.credits}分").mkString("\r\n")
        }
      case _ =>
        super.getPropertyValue(target, property)
    }
  }
}
