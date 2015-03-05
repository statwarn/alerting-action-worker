package com.statwarn
package actions

import akka.actor.ActorSystem
import com.statwarn.models.{ActionModel, AlertModel, MeasurementModel}
import play.api.libs.json.{JsObject, Json}
import spray.client.pipelining.sendReceive
import spray.http.ContentTypes._
import spray.http._

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
    val httpMethod: HttpMethod = extractHttpMethodFromConfiguration(triggeredAction.action_configuration)

    (triggeredAction.action_configuration \ "url").asOpt[String] match {
      case Some(url) =>
        val json = Json.obj(
          "measurement" -> measurement,
          "triggeredAction" -> triggeredAction,
          "alert" -> alert
        )
        val httpEntity = HttpEntity(`application/json`, json.toString())
        val httpRequest = HttpRequest(method = httpMethod, uri = url, entity = httpEntity)

        println(s"Sending webhook: ${httpRequest.method} ${httpRequest.uri} ${httpRequest.entity.asString}")
        pipeline(httpRequest).map(_ => true).fallbackTo(Future.successful(false))

      case _ => Future.successful(false)
    }
  }

  private def extractHttpMethodFromConfiguration(configuration: JsObject): HttpMethod = {
    // Get HTTP method specified in action configuration, default to POST if not set
    val httpMethodString: String = (configuration \ "method").asOpt[String].getOrElse("POST")

    httpMethodString.toUpperCase match {
      case "POST" => HttpMethods.POST
      case "PUT" => HttpMethods.PUT
      case "DELETE" => HttpMethods.DELETE
    }
  }
}
