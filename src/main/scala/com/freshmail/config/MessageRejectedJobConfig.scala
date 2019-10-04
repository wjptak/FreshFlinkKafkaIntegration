package com.freshmail.config

import com.typesafe.config.{Config, ConfigFactory}

class MessageRejectedJobConfig extends Serializable {
  private val config: Config = ConfigFactory.load("MessageRejectedJob.conf")

  val inputTopic : String = config.getString("input-topic")
  val parallelism: Int = config.getInt("parallelism")

  val kafkaConfig: KafkaConfig = new KafkaConfig(config.withOnlyPath("kafka"))
  val checkpointingConfig: CheckpointingConfig = new CheckpointingConfig(config)
  val stateBackendConfig: StateBackendConfig = new StateBackendConfig(config)
  val schemaRegistryConfig: SchemaRegistryConfig = new SchemaRegistryConfig(config)
}
