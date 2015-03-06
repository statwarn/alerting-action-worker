package com.statwarn
package models

import play.api.libs.json.{JsObject, JsValue, Json, Format}

case class MeasurementModel(
                             id: String,
                             data: JsValue,
                             metadata: JsObject
                             ) {
}

object MeasurementModel {
  implicit val jsonFormat: Format[MeasurementModel] = Json.format[MeasurementModel]
}
