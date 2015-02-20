package actions

import models.{MeasurementModel, ActionModel, AlertModel}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WebhookAction extends Action {
  val builder = new com.ning.http.client.AsyncHttpClientConfig.Builder()
  val client = new play.api.libs.ws.ning.NingWSClient(builder.build())

  /**
   * Send the webhook according to its configuration
   * @param measurement Measurement that caused the alert to be triggered
   * @param triggeredAction Triggered action
   * @param alert Triggered alert
   * @return
   */
  override def send(measurement: MeasurementModel, triggeredAction: ActionModel, alert: AlertModel): Future[Boolean] = {
    val webhookURL = (triggeredAction.action_configuration \ "url").asOpt[String]

    webhookURL match {
      case Some(url) =>
        client.url(url).post(Json.obj(
          "alert" -> alert.name
        )).map(_ => true).fallbackTo(Future.successful(false))
      case None =>
        Future.successful(false)
    }
  }
}
