package com.freshmail.config

import java.util.concurrent.TimeUnit.MILLISECONDS

import com.typesafe.config.Config
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

class CheckpointingConfig(config: Config) extends Serializable {

  val enabled: Boolean = config.getBoolean("checkpointing.enabled")
  val interval: Long = config.getDuration("checkpointing.interval", MILLISECONDS)
  val minProcessing: Long = config.getDuration("checkpointing.min-processing", MILLISECONDS)
  val timeout: Long = config.getDuration("checkpointing.timeout", MILLISECONDS)
  val maxConcurrent: Int = config.getInt("checkpointing.max-concurrent")

  def configure(env: StreamExecutionEnvironment): Unit = {
    if (enabled) {
      env.enableCheckpointing(interval)

      val checkpointingConfig = env.getCheckpointConfig
      checkpointingConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
      checkpointingConfig.setMinPauseBetweenCheckpoints(minProcessing)
      checkpointingConfig.setCheckpointTimeout(timeout)
      checkpointingConfig.setMaxConcurrentCheckpoints(maxConcurrent)
      checkpointingConfig.enableExternalizedCheckpoints(RETAIN_ON_CANCELLATION)
    }
  }

}
