package com.freshmail

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.freshmail.config.MessageRejectedJobConfig
import com.freshmail.events.parserly.message_delivery_state
import com.freshmail.model.MessageRejected
import org.apache.flink.api.scala._
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

object MessagesRejectedJob {
  def main(args: Array[String]) {
    val config: MessageRejectedJobConfig = new MessageRejectedJobConfig

    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(config.parallelism)
    config.stateBackendConfig.configure(env)
    config.checkpointingConfig.configure(env)

    val inputStream : DataStream[MessageRejected] =
      env.addSource(messageSource(config)).name("message-source").uid("message-source")
        .flatMap(new ConvertToMessageRejected).name("conversion")

    val counts = inputStream.flatMap{ _.message.toLowerCase.split("\\W+") filter { _.nonEmpty } }
      .map { (_, 1) }
      .keyBy(0)
      .sum(1)
      .print()

    // execute program
    env.execute("Flink Scala API Skeleton")
  }

  private def messageSource(config: MessageRejectedJobConfig): FlinkKafkaConsumer[message_delivery_state] = {
    new FlinkKafkaConsumer[message_delivery_state](
      config.inputTopic,
      ConfluentRegistryAvroDeserializationSchema.forSpecific[message_delivery_state](classOf[message_delivery_state],
        config.schemaRegistryConfig.url),
      config.kafkaConfig.asProperties
    )
  }
}
