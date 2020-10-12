package org.openurp.edu.extern.exchange.web.helper

import java.time.Instant

import org.beangle.commons.lang.Numbers
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.data.transfer.importer.{ImportListener, ImportResult}
import org.openurp.edu.base.model.{Project, Student}
import org.openurp.edu.extern.model.ExemptionCredit

class ExemptionCreditImportListener(project: Project, entityDao: EntityDao) extends ImportListener {
  override def onStart(tr: ImportResult): Unit = {}

  override def onFinish(tr: ImportResult): Unit = {}

  override def onItemStart(tr: ImportResult): Unit = {
    var std: Student = null
    transfer.curData.get("stdCode") foreach { stdCode =>
      val builder = OqlBuilder.from(classOf[Student], "s")
      builder.where("s.project=:project and s.user.code=:stdCode", project, stdCode)
      val stds = entityDao.search(builder)
      if (stds.nonEmpty) {
        std = stds.head
      }
    }
    if (null == std) {
      tr.addFailure("错误的学号", transfer.curData.get("stdCode").orNull)
    } else {
      var maxValue: Float = -1
      transfer.curData.get("maxValue") foreach (d => maxValue = Numbers.toFloat(d.toString))
      if (maxValue < 0) {
        tr.addFailure("错误的学分上限", transfer.curData.get("maxValue").orNull)
        return
      }
      val query = OqlBuilder.from(classOf[ExemptionCredit], "ec")
      query.where("ec.std=:std", std)
      val ecs = entityDao.search(query)
      val ec =
        if (ecs.nonEmpty) {
          ecs.head
        } else {
          val ec = new ExemptionCredit
          ec.std = std
          ec
        }
      ec.maxValue = maxValue
      ec.updatedAt= Instant.now
      transfer.current = ec
    }
  }

  override def onItemFinish(tr: ImportResult): Unit = {
    if (null != transfer.current) {
      val ec = transfer.current.asInstanceOf[ExemptionCredit]
      entityDao.saveOrUpdate(ec)
    }
  }
}
