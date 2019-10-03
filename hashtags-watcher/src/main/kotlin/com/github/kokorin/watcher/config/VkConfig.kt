package com.github.kokorin.watcher.config

import com.typesafe.config.Config

interface VkConfig {
    val version: VkVersion

    val accessToken: VkAccessToken
}

class VkConfigImpl(private val conf: Config) : VkConfig {
    override val version: VkVersion
        get() = VkVersion(conf.getString("version"))
    override val accessToken: VkAccessToken
        get() = VkAccessToken(conf.getString("access-token"))
}

data class VkVersion(val version: String)

data class VkAccessToken(val accessToken: String)
