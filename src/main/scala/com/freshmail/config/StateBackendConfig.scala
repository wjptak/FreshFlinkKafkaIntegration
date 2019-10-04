package com.freshmail.config

import com.typesafe.config.Config
import org.apache.flink.contrib.streaming.state.{PredefinedOptions, RocksDBStateBackend}
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment


class StateBackendConfig(val config: Config) extends Serializable {

  val checkpointDir: String = config.getString("state.checkpoints.dir")

  def configure(env: StreamExecutionEnvironment): Unit = {
    val fsStateBackend = new FsStateBackend(checkpointDir)
    val rocksDBStateBackend = new RocksDBStateBackend(fsStateBackend)
    rocksDBStateBackend.setPredefinedOptions(PredefinedOptions.SPINNING_DISK_OPTIMIZED)

    env.setStateBackend(rocksDBStateBackend)
  }

}
