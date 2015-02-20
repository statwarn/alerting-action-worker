package subscriber

import akka.actor.FSM.{SubscribeTransitionCallBack, Transition}
import akka.actor.{Actor, ActorSystem, Props}
import com.thenewmotion.akka.rabbitmq._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.ExecutionContext.Implicits.global

object AlertTriggerSubscriber {
  val system = ActorSystem("system")
  val configuration = ConfigFactory.load()

  println(configuration.getConfig("amqp"))
  println(configuration.getConfig("alert_subscriber"))

  // Connection configuration
  private val connectionName = configuration.getString("alert_subscriber.connection_name")
  private val reconnectionDelay = configuration.getInt("alert_subscriber.reconnection_delay")
  private val connectionFactory = setupConnectionFactory()

  private def setupConnectionFactory(): ConnectionFactory = {
    val factory = new ConnectionFactory()

    factory.setUsername(configuration.getString("amqp.user"))
    factory.setPassword(configuration.getString("amqp.password"))
    factory.setVirtualHost(configuration.getString("amqp.virtualhost"))
    factory.setHost(configuration.getString("amqp.host"))
    factory.setPort(configuration.getInt("amqp.port"))
    factory
  }

  // Create the connection actor
  private val connectionActor = system.actorOf(ConnectionActor.props(connectionFactory, Duration(reconnectionDelay, MILLISECONDS)), connectionName)

  // Create the channel actor
  private val channelName = configuration.getString("alert_subscriber.channel_name")
  private val queueName = configuration.getString("alert_subscriber.queue_name")
  private val subscriberActor = connectionActor.createChannel(ChannelActor.props(), Some(channelName))

  /**
   * Start listening for messages on RabbitMQ and triggering actions
   */
  def handleMessages(): Unit = {
    def subscribe(channel: Channel): Unit = {
      // Queue declaration options
      val (durable, exclusive, autoDelete, arguments) = (true, false, false, null)
      val queue = channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments).getQueue

      // Queue consume options
      val autoAck = false
      val multipleAck = false
      val requeue = true
      val consumer = new DefaultConsumer(channel) {
        override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
          AlertTriggerProcessor.processAlertTrigger(new String(body, "UTF-8")).onSuccess({
            case true => channel.basicAck(envelope.getDeliveryTag, multipleAck)
            case false => channel.basicNack(envelope.getDeliveryTag, multipleAck, requeue)
          })
        }
      }
      channel.basicConsume(queue, autoAck, consumer)
    }

    // Don't drop the message if the channel is not created yet
    subscriberActor ! ChannelMessage(subscribe, dropIfNoChannel = false)
  }

  val disconnectionHandler = system.actorOf(Props(new Actor {
    override def receive: Receive = {
      // Cannot check the type of "newState" because it is private to akka-rabbitmq, so we have to stringify it
      case Transition(actor, oldState, newState) if newState.toString == "Disconnected" =>
        system.shutdown()
    }
  }))

  connectionActor ! SubscribeTransitionCallBack(disconnectionHandler)
}
