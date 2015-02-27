package com.statwarn
package actions

import akka.actor.ActorSystem
import com.statwarn.models.{ActionModel, AlertModel, MeasurementModel}
import play.api.libs.json.Json
import spray.client.pipelining.{Post, sendReceive}
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.PlayJsonSupport.playJsonMarshaller

import scala.concurrent.Future

object WebhookAction extends Action {
  implicit val system = ActorSystem("system")
  import com.statwarn.actions.WebhookAction.system.dispatcher

  /**
   * Send the webhook according to its configuration
   * @param measurement Measurement that caused the alert to be triggered
   * @param triggeredAction Triggered action
   * @param alert Triggered alert
   * @return
   */
  override def send(measurement: MeasurementModel, triggeredAction: ActionModel, alert: AlertModel): Future[Boolean] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    (triggeredAction.action_configuration \ "url").asOpt[String] match {
      case Some(url) =>
        val json = Json.obj(
          "measurement" -> measurement,
          "triggeredAction" -> triggeredAction,
          "alert" -> alert
        )
        pipeline(Post(url, json)).map(_ => true).fallbackTo(Future.successful(false))

      case _ => Future.successful(false)
    }
  }
}
