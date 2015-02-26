package com.statwarn

import clevercloud.HTTPThread
import com.typesafe.config.ConfigFactory
import subscriber.AlertTriggerSubscriber

object ApplicationMain extends App {
  val configuration = ConfigFactory.load()

  AlertTriggerSubscriber.handleMessages()

  // Listen on 8080 for CleverCloud
  new HTTPThread(8080).run()
}
