package com.freshmail

import com.freshmail.model.{EvaluatedMessageRejected, MessageRejected}
import org.apache.flink.api.common.functions.MapFunction

class EvaluateMessage() extends MapFunction[MessageRejected, EvaluatedMessageRejected] {
  override def map(msg: MessageRejected): EvaluatedMessageRejected = {
    val wordCount = msg.message.split(" ")
      .filter(_.nonEmpty)
      .groupBy(_.toLowerCase)
      .mapValues(_.size).toSeq
      .sortWith { case ((_, qty1), (_, qty2)) => qty1 > qty2 }
      .zipWithIndex

    EvaluatedMessageRejected(msg.message_id, wordCount)
  }
}
