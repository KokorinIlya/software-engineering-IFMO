package com.github.kokorin.watcher.config

import com.typesafe.config.Config
import java.time.Duration

interface ActorConfig {
    val timeout: Duration
}

class ActorConfigImpl(private val conf: Config) : ActorConfig {
    override val timeout: Duration
        get() = conf.getDuration("timeout")
}
