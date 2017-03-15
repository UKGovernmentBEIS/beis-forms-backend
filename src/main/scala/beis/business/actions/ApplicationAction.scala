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

package beis.business.actions

import javax.inject.Inject

import play.api.mvc.Results._
import play.api.mvc._
import beis.business.data.ApplicationOps
import beis.business.models.ApplicationId
import beis.business.restmodels.Application

import scala.concurrent.{ExecutionContext, Future}

case class ApplicationRequest[A](Application: Application, request: Request[A]) extends WrappedRequest[A](request)

class ApplicationAction @Inject()(opportunities: ApplicationOps)(implicit ec: ExecutionContext) {
  def apply(id: ApplicationId): ActionBuilder[ApplicationRequest] =
    new ActionBuilder[ApplicationRequest] {
      override def invokeBlock[A](request: Request[A], next: (ApplicationRequest[A]) => Future[Result]): Future[Result] = {
        opportunities.application(id).flatMap {
          case Some(opp) => next(ApplicationRequest(opp, request))
          case None => Future.successful(NotFound(s"No application with id ${id.id} exists"))
        }
      }
    }
}