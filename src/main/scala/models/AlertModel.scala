package models

import java.util.UUID

import play.api.libs.json.{Json, Format}

case class AlertModel(
                       alert_id: UUID,
                       name: String,
                       activated: Boolean,
                       measurement_id: UUID
                       ) {
}

object AlertModel {
  implicit val jsonFormat: Format[AlertModel] = Json.format[AlertModel]
}
