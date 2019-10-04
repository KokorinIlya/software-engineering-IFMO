package com.github.kokorin.watcher.config

import com.typesafe.config.Config
import java.lang.IllegalArgumentException

interface VkConfig {
    val version: VkVersion

    val accessToken: String

    val schema: Schema

    val host: String

    val port: Int

    val rps: Int
}

class VkConfigImpl(private val conf: Config) : VkConfig {
    override val schema: Schema
        get() = Schema.fromString(conf.getString("schema"))
    override val host: String
        get() = conf.getString("host")
    override val port: Int
        get() = conf.getInt("port")
    override val rps: Int
        get() = conf.getInt("rps")
    override val version: VkVersion
        get() {
            val versionConfig = conf.getConfig("version")
            return VkVersion(major = versionConfig.getInt("major"), minor = versionConfig.getInt("minor"))
        }
    override val accessToken: String
        get() = conf.getString("access-token")
}

data class VkVersion(val major: Int, val minor: Int)

enum class Schema {
    HTTPS,
    HTTP;

    override fun toString(): String {
        return when (this) {
            HTTP -> "http"
            HTTPS -> "https"
        }
    }

    companion object {
        fun fromString(stringRepresentation: String): Schema {
            return when (stringRepresentation.toLowerCase()) {
                "http" -> HTTP
                "https" -> HTTPS
                else -> throw IllegalArgumentException(
                    "Unknown schema $stringRepresentation, select one of the following ${values().map { it.name }}"
                )
            }
        }
    }
}
