package publishers

import com.thenewmotion.akka.rabbitmq._
import models.Alert
import play.api.Play.{configuration, current}
import play.api.libs.json.Json
import play.libs.Akka

import scala.concurrent.duration.{Duration, MILLISECONDS}

object AlertChangePublisher {
  object AlertChangeType extends Enumeration {
    type AlertChangeType = Value
    val Created, Updated, Deleted = Value
  }

  // Connection configuration
  private val connectionName = configuration.getString("alert_publisher.connection_name").get
  private val reconnectionDelay = configuration.getInt("alert_publisher.reconnection_delay").get
  private val connectionFactory = setupConnectionFactory()

  private def setupConnectionFactory(): ConnectionFactory = {
    val factory = new ConnectionFactory()

    factory.setUsername(configuration.getString("amqp.user").get)
    factory.setPassword(configuration.getString("amqp.password").get)
    factory.setVirtualHost(configuration.getString("amqp.virtualhost").get)
    factory.setHost(configuration.getString("amqp.host").get)
    factory.setPort(configuration.getInt("amqp.port").get)
    factory
  }

  // Create the connection actor
  private val connectionActor = Akka.system.actorOf(ConnectionActor.props(connectionFactory, Duration(reconnectionDelay, MILLISECONDS)), connectionName)

  // Create the channel actor
  private val channelName = configuration.getString("alert_publisher.channel_name").get
  private val exchangeName = configuration.getString("alert_publisher.exchange_name").get
  private val publisherActor = connectionActor.createChannel(ChannelActor.props(), Some(channelName))

  /**
   * Publish an alert change (alert created/updated/deleted) as JSON on RabbitMQ
   * @param alert Alert to include in the message body
   * @param alertChangeType Type of change: Created, Updated, or Deleted
   */
  def publishAlertChange(alert: Alert, alertChangeType: AlertChangeType.AlertChangeType): Unit = {
    def publish(channel: Channel): Unit = {
      val routingKey = alertChangeType match {
        case AlertChangeType.Created => s"alerts.changed.created.${alert.alertModel.alert_id}"
        case AlertChangeType.Updated => s"alerts.changed.updated.${alert.alertModel.alert_id}"
        case AlertChangeType.Deleted => s"alerts.changed.deleted.${alert.alertModel.alert_id}"
      }

      channel.basicPublish(exchangeName, routingKey, null, Json.toJson(alert).toString().getBytes)
    }
    publisherActor ! ChannelMessage(publish, dropIfNoChannel = false)
  }
}
