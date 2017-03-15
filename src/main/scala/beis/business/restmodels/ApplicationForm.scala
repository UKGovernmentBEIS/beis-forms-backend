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

package beis.business.restmodels

import play.api.libs.json.JsArray
import beis.business.models.{ApplicationFormId, OpportunityId}

case class Question(key: String, text: String, description: Option[String], helpText: Option[String])

case class ApplicationFormSection(
                                   sectionNumber: Int,
                                   title: String,
                                   questions: Seq[Question],
                                   sectionType: String,
                                   fields: JsArray)

case class ApplicationForm(id: ApplicationFormId, opportunityId: OpportunityId, sections: Seq[ApplicationFormSection])