import clevercloud.HTTPThread
import subscriber.AlertTriggerSubscriber

object ApplicationMain extends App {
  AlertTriggerSubscriber.handleMessages()

  // Listen on 8080 for CleverCloud
  new HTTPThread(8080).run()
}
