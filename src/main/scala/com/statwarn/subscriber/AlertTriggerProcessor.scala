package com.statwarn
package subscriber

import actions.WebhookAction
import models.{ActionModel, ActionType, AlertModel, MeasurementModel}
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Success, Try}

object AlertTriggerProcessor {
  def processAlertTrigger(messageBody: String): Future[Boolean] = {
    // Make sure the JSON is valid
    val jsBody: Option[JsObject] = Json.parse(messageBody).asOpt[JsObject]
    val measurementOpt: Option[MeasurementModel] = jsBody.flatMap(body => (body \ "measurement").asOpt[MeasurementModel])
    val actionOpt: Option[ActionModel] = jsBody.flatMap(body => (body \ "action").asOpt[ActionModel])
    val alertOpt: Option[AlertModel] = jsBody.flatMap(body => (body \ "alert").asOpt[AlertModel])

    (for {
      measurement <- measurementOpt
      action <- actionOpt
      alert <- alertOpt
    } yield (measurement, action, alert)).fold({
      // At least one json parsing failed
      Future.successful(false)
    })({
      case (measurement, action, alert) =>
        // The json is valid, the models were successfully parsed
        processAlertTrigger(measurement, action, alert)
    })
  }

  def processAlertTrigger(measurement: MeasurementModel, triggeredAction: ActionModel, alert: AlertModel): Future[Boolean] = {
    Try(ActionType.withName(triggeredAction.action_id.capitalize)) match {
      case Success(ActionType.Webhook) => WebhookAction.send(measurement, triggeredAction, alert)
      case _ => Future.successful(false)
    }
  }
}
