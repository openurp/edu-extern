/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.edu.extern.exchange.service.impl

import java.time.Instant

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.edu.base.States
import org.openurp.edu.base.model.Student
import org.openurp.edu.extern.exchange.service.ExchangeStudentService
import org.openurp.edu.extern.model.{ExchangeStudent, ExemptionCredit}

class ExchangeStudentServiceImpl extends ExchangeStudentService {

  var entityDao: EntityDao = _

  override def recalcExemption(std: Student): Unit = {
    //重新统计已经免修的学分
    val ecBuilder = OqlBuilder.from(classOf[ExemptionCredit], "ec")
    ecBuilder.where("ec.std=:std", std)
    val ec = entityDao.search(ecBuilder).headOption match {
      case Some(e) => e
      case None =>
        val e = new ExemptionCredit
        e.std = std
        e
    }
    var exempted: Float = 0
    entityDao.findBy(classOf[ExchangeStudent], "std", List(std)).filter(_.state == States.Finalized) foreach { fes =>
      fes.grades foreach { fgrade =>
        fgrade.courses foreach { c =>
          exempted += c.credits
        }
      }
    }
    ec.exempted = exempted
    ec.updatedAt = Instant.now
    entityDao.saveOrUpdate(ec)
  }
}
