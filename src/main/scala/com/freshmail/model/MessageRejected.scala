package com.freshmail.model

import java.time.{Instant, LocalDateTime}
import java.util.TimeZone

import com.freshmail.events.parserly.message_delivery_state
import javax.lang.model.element.Name

case class MessageRejected(timestamp: LocalDateTime,
                           system: String,
                           subsystem: String,
                           messageType: String,
                           hostname: String,
                           message_id: String,
                           message: String,
                           relay_ip: Option[String],
                           sender_ip: Option[String],
                           envelopeFrom: Option[String],
                           envelopeFromDomain: Option[String]
                          )

object MessageRejected {
  def apply(msg: message_delivery_state): MessageRejected = {
    new MessageRejected(LocalDateTime.ofInstant(Instant.ofEpochMilli(msg.getTimestamp * 1000), TimeZone.getDefault().toZoneId()),
      msg.getSystem.toString,
      msg.getSubsystem.toString,
      msg.getType.toString,
      msg.getHostname.toString,
      msg.getMessageId.toString,
      msg.getMessage.toString,
      msg.getRelayIp match {
        case null => None
        case a => Some(a.toString)
      },
      msg.getSenderIp match {
        case null => None
        case a => Some(a.toString)
      },
      msg.getEnvelopeFrom match {
        case null => None
        case a => Some(a.toString)
      },
      msg.getEnvelopeFromDomain match {
        case null => None
        case a => Some(a.toString)
      }
    )
  }
}