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

package beis.business.controllers

import javax.inject.{Inject, Named}

import cats.data.OptionT
import cats.instances.future._
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.libs.json.{JsObject, _}
import play.api.mvc.{Action, Controller}
import beis.business.Config
import beis.business.actions.ApplicationAction
import beis.business.data.{ApplicationFormOps, ApplicationOps, OpportunityOps}
import beis.business.models._
import beis.business.notifications.NotificationService
import beis.business.restmodels.ApplicationDetail

import scala.concurrent.{ExecutionContext, Future}

class ApplicationController @Inject()(applications: ApplicationOps,
                                      appForms: ApplicationFormOps,
                                      opps: OpportunityOps,
                                      notifications: NotificationService,
                                      ApplicationAction: ApplicationAction)
                                     (implicit val ec: ExecutionContext) extends Controller with ControllerUtils {
  def byId(id: ApplicationId) = Action.async(applications.byId(id).map(jsonResult(_)))

  def applicationForForm(applicationFormId: ApplicationFormId) = Action.async { implicit request =>
    val userId = request.headers.get("UserId").getOrElse("")

    applications.forForm(applicationFormId, UserId(userId)).map(jsonResult(_))
  }

  def applicationForFormCreate(applicationFormId: ApplicationFormId) = Action.async { implicit request =>
    val userId = request.headers.get("UserId").getOrElse("")

    applications.createForm(applicationFormId, UserId(userId)).map(jsonResult(_))
  }

  def application(applicationId: ApplicationId) =
    Action.async(applications.application(applicationId).map(jsonResult(_)))

  def userApplications = Action.async { implicit request =>
        val userId = request.headers.get("UserId").getOrElse("")
        applications.userApplications(Option(UserId(userId))).map {
        os => ( Ok(Json.toJson(os)))
      }
  }


  def detail(applicationId: ApplicationId) = Action.async {
    val ft = for {
      a <- OptionT(applications.application(applicationId))
      f <- OptionT(appForms.byId(a.applicationFormId))
      o <- OptionT(opps.opportunity(f.opportunityId))
    } yield {
      ApplicationDetail(a.id, a.personalReference, a.appStatus, f.sections.length, a.sections.count(_.completedAt.isDefined), o.summary, f, a.sections)
    }

    ft.value.map(jsonResult(_))
  }

  val emptyJsObject: JsObject = JsObject(Seq())

  /**
    * Returns the structure of application with its sections, but without any answers.
    * Useful for situations where the client is just looking for the status of the
    * application and sections without needing the full content.
    */
  def overview(applicationId: ApplicationId) = Action.async {
    applications.application(applicationId).map {
      _.map(app => app.copy(sections = app.sections.map(_.copy(answers = emptyJsObject))))
    }.map(jsonResult(_))
  }

  def delete(id: ApplicationId) = Action.async { implicit request =>
    applications.delete(id).map(_ => NoContent)
  }

  def deleteAll() = Action.async { implicit request =>
    applications.deleteAll.map(_ => NoContent)
  }

  def submit(id: ApplicationId) = ApplicationAction(id).async { request =>
    val f = for {
      submissionRef <- OptionT(applications.submit(id))
      _ <- OptionT.liftF(sendSubmissionNotifications(submissionRef))
    } yield submissionRef


    f.value.map(ref => Ok(JsObject(Seq("applicationRef" -> Json.toJson(ref)))))
  }

  private def sendSubmissionNotifications(submissionRef: SubmittedApplicationRef) = {
    import Config.config.beis.{email => emailConfig}

    val from = emailConfig.replyto
    val to = emailConfig.dummyapplicant
    val mgrEmail = emailConfig.dummymanager

    val fs = Seq(
      ("Manager", notifications.notifyManagerAppSubmitted(submissionRef, from, to)),
      ("Applicant", notifications.notifyApplicant(submissionRef, DateTime.now(DateTimeZone.UTC), from, to, mgrEmail))
    ).map {
      case (who, f) => f.recover { case t =>
        Logger.error(s"Failed to send email to $who on an application submission", t)
        None
      }
    }
    Future.sequence(fs).map(_ => ())
  }

  def savePersonalRef(id: ApplicationId) = Action.async(parse.json[JsString]) { implicit request =>
    val newVal = request.body.as[String] match {
      case "" => None
      case s => Some(s)
    }
    applications.updatePersonalReference(id, newVal).map {
      case 0 => NotFound
      case _ => NoContent
    }
  }

  def saveAppStatus(id: ApplicationId) = Action.async(parse.json[JsString]) { implicit request =>
    val newVal = request.body.as[String] match {
      case "" => None
      case s => Some(s)
    }
    applications.updateAppStatus(id, newVal).map {
      case 0 => NotFound
      case _ => NoContent
    }
  }

//  def saveAppMessage(id: ApplicationId) = Action.async(parse.json[JsString]) { implicit request =>
//    val newVal = request.body.as[String] match {
//      case "" => None
//      case s => Some(s)
//    }
//    applications.saveAppMessage(id, newVal).map {
//      case 0 => NotFound
//      case _ => NoContent
//    }
//  }
}
