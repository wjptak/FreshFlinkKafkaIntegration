package com.freshmail.config

import java.util.Properties

import com.typesafe.config.Config

import scala.collection.JavaConverters._

class KafkaConfig(val config: Config) extends Serializable {

  val propertiesMap: Map[String, String] =
    config.withOnlyPath("kafka").entrySet.asScala
      .map(p => p.getKey)
      .map(p => (p.replace("kafka.", ""), config.getString(p)))
      .toMap

  def asProperties: Properties = {
    val properties: Properties = new Properties
    propertiesMap.foreach(p => properties.setProperty(p._1, p._2))
    properties
  }

}