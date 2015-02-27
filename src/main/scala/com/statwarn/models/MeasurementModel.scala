package com.statwarn
package models

import play.api.libs.json.{JsObject, Json, Format}

case class MeasurementModel(
                             id: String,
                             data: JsObject,
                             metadata: JsObject
                             ) {
}

object MeasurementModel {
  implicit val jsonFormat: Format[MeasurementModel] = Json.format[MeasurementModel]
}
