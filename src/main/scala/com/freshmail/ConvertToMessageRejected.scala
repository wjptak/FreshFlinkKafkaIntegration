package com.freshmail

import org.apache.flink.api.common.functions.RichFlatMapFunction
import com.freshmail.events.parserly.message_delivery_state
import com.freshmail.model.MessageRejected
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper
import org.apache.flink.metrics.Meter
import org.apache.flink.util.Collector

import scala.util.{Failure, Success}

class ConvertToMessageRejected extends RichFlatMapFunction[message_delivery_state, MessageRejected] with LazyLogging {
  @transient
  private var failureMeter: Meter = _

  override def open(config: Configuration): Unit = {
    this.failureMeter = getRuntimeContext.getMetricGroup
      .addGroup("convert-message")
      .meter("failures", new DropwizardMeterWrapper(new com.codahale.metrics.Meter()))
  }

  override def flatMap(value: message_delivery_state, out: Collector[MessageRejected]): Unit = {
    val msg = MessageRejected(value)
    msg match {
      case message: MessageRejected => out.collect(message)
      case _ =>
        failureMeter.markEvent()
        logger.warn(s"Failed to parse message: $value.")
    }
  }
}
