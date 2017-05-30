/*
 * Copyright (C) 2016  Department for Business, Energy and Industrial Strategy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package beis.business.tables

import javax.inject.Inject

import beis.business.data.MessageBoardOps
import beis.business.models._
import beis.business.restmodels.{Application, Message}
import beis.business.slicks.modules._
import beis.business.slicks.support.DBBinding
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

class MessageBoardTables @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends MessageBoardModule
    with MessageBoardOps
    with DBBinding
    with PgSupport {

  import api._

  override def byId(id: MessageId): Future[Option[MessageRow]] = db.run(messageBoardTable.filter(_.id === id).result).map { os =>
    os.map(m => MessageRow(m.id, m.userId, m.applicationId, m.sectionNumber, m.sentBy, m.sentAt, m.message)).headOption
  }
  override def userMessages(userId: UserId): Future[Set[MessageRow]] = db.run(messageBoardTable.filter(_.userId === userId).result).map { os =>
    os.map(m => {
      val msg = m.message match {
        case ms => if(ms.get.charAt(20) != -1) ms.get.substring(0, 20) else ms.get
        case None => "No Message"
      }
      MessageRow(m.id, m.userId, m.applicationId, m.sectionNumber, m.sentBy, m.sentAt, Option(msg + " . . ."))
    }).toSet
  }
}
