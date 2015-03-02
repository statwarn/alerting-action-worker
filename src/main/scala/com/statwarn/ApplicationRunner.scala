package com.statwarn

import com.typesafe.config.ConfigFactory
import subscriber.AlertTriggerSubscriber

object ApplicationRunner {
  val configuration = ConfigFactory.load()

  def run(): Unit = {
    AlertTriggerSubscriber.handleMessages()
  }
}
