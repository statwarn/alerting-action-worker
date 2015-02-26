package com.statwarn
package actions

import models.{AlertModel, ActionModel, MeasurementModel}

import scala.concurrent.Future

trait Action {
  /**
   * Trigger the action
   * @param measurement Measurement that caused the alert to be triggered
   * @param triggeredAction Triggered action
   * @param alert Triggered alert
   * @return
   */
  def send(measurement: MeasurementModel, triggeredAction: ActionModel, alert: AlertModel): Future[Boolean]
}
