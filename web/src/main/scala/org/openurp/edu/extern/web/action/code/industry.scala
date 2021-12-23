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

package org.openurp.edu.extern.web.action.code

import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.edu.extern.code.model.{CertificateCategory, CertificateSubject}

class CertificateSubjectAction extends RestfulAction[CertificateSubject] {
  override protected def editSetting(subject: CertificateSubject): Unit = {
    put("categories", entityDao.getAll(classOf[CertificateCategory]))
  }
}

class CertificateCategoryAction extends RestfulAction[CertificateCategory]
