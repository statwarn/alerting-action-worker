package com.statwarn

import clevercloud.HTTPThread

object ApplicationMain extends App {
  ApplicationRunner.run()

  // Listen on 8080 for CleverCloud
  new HTTPThread(8080).run()
}
