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

package beis.business.slicks.modulesdefs

import com.wellfactored.slickgen.{ModuleDefinition, ModuleSpec}
import beis.business.models._

object ApplicationModuleDef extends ModuleDefinition {
  val spec = ModuleSpec("ApplicationModule")
    .withTableFor[ApplicationRow]
    .withTableFor[ApplicationSectionRow]
    .dependsOn(ApplicationFormModuleDef)
}