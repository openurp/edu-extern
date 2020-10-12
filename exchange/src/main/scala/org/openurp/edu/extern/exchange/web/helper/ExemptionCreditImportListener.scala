package org.openurp.edu.extern.exchange.web.helper

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
      val query = OqlBuilder.from(classOf[ExemptionCredit], "ec")
      query.where("ec.std=:std", std)
      val ess = entityDao.search(query)
      if (ess.nonEmpty) {
        transfer.current = ess.head
      } else {
        val ec = new ExemptionCredit
        ec.std = std
        transfer.current = ec
      }
    }
  }

  override def onItemFinish(tr: ImportResult): Unit = {
    val ec = transfer.current.asInstanceOf[ExemptionCredit]
    entityDao.saveOrUpdate(ec)
  }
}
