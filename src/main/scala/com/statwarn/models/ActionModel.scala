package com.statwarn
package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.{JsObject, Json, Format}

case class ActionModel(
                      alert_id: UUID,
                      action_id: String,
                      action_configuration: JsObject,
                      createdAt: DateTime,
                      updatedAt: DateTime
                        ) {
}

object ActionModel {
  implicit val jsonFormat: Format[ActionModel] = Json.format[ActionModel]
}
