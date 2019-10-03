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

import java.util.Properties

import collection.JavaConverters._
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import com.freshmail.events.parserly.message_delivery_state
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord

import scala.io.Source

object Job {
  def main(args: Array[String]) {
    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val inputTopic = "external_parserly_message-rejected"

    val inputStream : DataStream[GenericRecord] = env.addSource(
      messageSource(inputTopic)
    ).uid("message-source")

    inputStream.print()

    // execute program
    env.execute("Flink Scala API Skeleton")
  }

  private def messageSource(inputTopic: String): FlinkKafkaConsumer[GenericRecord] = {

    val properties = new Properties()
    properties.setProperty("bootstrap.servers",
      "kafka-b1af18ba.prod.freshmail.network:9092," +
        "kafka-c610a63d.prod.freshmail.network:9092")
    properties.setProperty("group.id", "flink-fresh-kafka")

    val schemaRegistryUrl = "http://kafka-b1af18ba.prod.freshmail.network:8081"

    /**
     * /*ConfluentRegistryAvroDeserializationSchema.forSpecific[message_delivery_state](classOf[message_delivery_state], schemaRegistryUrl),*/
     */

    new FlinkKafkaConsumer[GenericRecord](
      inputTopic,
      ConfluentRegistryAvroDeserializationSchema.forGeneric(Schema.parse(Source.fromFile("./src/main/avro/message-state.avsc").mkString), schemaRegistryUrl),
      properties
    )
  }
}
