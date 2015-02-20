package models

import java.util.UUID

import play.api.libs.json.{JsObject, Json, Format}

case class MeasurementModel(
                             id: UUID,
                             data: JsObject,
                             metadata: JsObject
                             ) {
}

object MeasurementModel {
  implicit val jsonFormat: Format[MeasurementModel] = Json.format[MeasurementModel]
}
