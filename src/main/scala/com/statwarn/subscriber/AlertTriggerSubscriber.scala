package com.statwarn
package subscriber

import akka.actor.{ActorRef, ActorSystem}
import com.thenewmotion.akka.rabbitmq._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, MILLISECONDS}

object AlertTriggerSubscriber {
  val system = ActorSystem("system")
  val configuration = ApplicationMain.configuration
  val connectionActor = getConnectionActor

  /**
   * Start listening for messages on RabbitMQ and triggering actions
   */
  def handleMessages(): Unit = {
    // Create the channel actor
    val channelName = configuration.getString("alert_subscriber.channel_name")

    connectionActor ! CreateChannel(ChannelActor.props(setupChannel), Some(channelName))
  }

  private def getConnectionActor: ActorRef = {
    // Connection configuration
    val connectionName = configuration.getString("alert_subscriber.connection_name")
    val reconnectionDelay = configuration.getInt("alert_subscriber.reconnection_delay")
    val connectionFactory = getConnectionFactory

    // Create the connection actor
    system.actorOf(ConnectionActor.props(connectionFactory, Duration(reconnectionDelay, MILLISECONDS)), connectionName)
  }

  private def getConnectionFactory: ConnectionFactory = {
    val factory = new ConnectionFactory()

    factory.setUsername(configuration.getString("amqp.user"))
    factory.setPassword(configuration.getString("amqp.password"))
    factory.setVirtualHost(configuration.getString("amqp.virtualhost"))
    factory.setHost(configuration.getString("amqp.host"))
    factory.setPort(configuration.getInt("amqp.port"))
    factory
  }

  private def setupChannel(channel: Channel, actor: ActorRef): Unit ={
    val queueName = configuration.getString("alert_subscriber.queue_name")
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
}
