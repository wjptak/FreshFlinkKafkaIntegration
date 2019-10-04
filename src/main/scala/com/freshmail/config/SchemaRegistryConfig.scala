package com.freshmail.config

import com.typesafe.config.Config

import scala.collection.JavaConverters._

class SchemaRegistryConfig(config: Config) extends Serializable {

  val url: String = config.getString("schema-registry.url")

}
