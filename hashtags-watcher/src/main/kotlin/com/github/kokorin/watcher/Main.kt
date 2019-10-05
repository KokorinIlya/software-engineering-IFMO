package com.github.kokorin.watcher

import com.github.kokorin.watcher.config.VkConfigImpl
import com.github.kokorin.watcher.actors.VkHashTagWatcherActor
import com.github.kokorin.watcher.clients.http.AsyncHttpClientImpl
import com.github.kokorin.watcher.clients.http.RPSLimitHttpClient
import com.github.kokorin.watcher.clients.vk.AsyncVkClientImpl
import com.github.kokorin.watcher.config.ActorConfigImpl
import com.github.kokorin.watcher.model.HashTagCount
import com.github.kokorin.watcher.model.IncorrectVkAnswer
import com.github.kokorin.watcher.model.NoResponse
import com.github.kokorin.watcher.time.TimeConverter
import com.typesafe.config.ConfigFactory
import io.ktor.client.HttpClient
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

fun main(args: Array<String>) = runBlocking {
    val numberPattern = Pattern.compile("[1-9][0-9]*")
    require(args.size == 2 && numberPattern.matcher(args[1]).matches()) {
        "Using: <hashtag to look for> <number of hours>"
    }
    val hashTag = args[0]
    val hours = Integer.parseInt(args[1])
    require(hours in 1..24) { "Hours must be between 1 and 24" }

    val log = LoggerFactory.getLogger("com.github.kokorin.watcher.main-logger")
    log.info("Application started")

    val vkConfig = VkConfigImpl(
        ConfigFactory.parseFile(File("src/main/resources/application.conf")).getConfig("vk")
    )
    val vkClient = AsyncVkClientImpl(
        RPSLimitHttpClient(
            AsyncHttpClientImpl(HttpClient()),
            vkConfig.rps
        ),
        vkConfig
    )

    val watcherActorConfig = ActorConfigImpl(
        ConfigFactory.parseFile(File("src/main/resources/application.conf")).getConfig("watcher-actor")
    )
    val searchActorConfig = ActorConfigImpl(
        ConfigFactory.parseFile(File("src/main/resources/application.conf")).getConfig("search-actor")
    )
    val timeConverter = TimeConverter(Date())
    val hashTagWatcher = VkHashTagWatcherActor(vkClient, timeConverter, hashTag, watcherActorConfig, searchActorConfig)

    val result = GlobalScope.async(Dispatchers.IO) {
        hashTagWatcher.use {
            it.doRequest(hours, this).toList()
        }
    }
    for ((hour, apiResult) in result.await().withIndex()) {
        val toPrint = "За ${hour + 1} часов " +
                when (apiResult) {
                    is HashTagCount -> "было ${apiResult.count} новостей с хештегом #$hashTag"
                    is IncorrectVkAnswer -> "ВК апи вернуло некорректный результат"
                    is NoResponse -> "не пришло ответа от ВК апи"
                }
        println(toPrint)
    }
}
