package com.github.kokorin.watcher

import com.github.kokorin.watcher.config.VkConfigImpl
import com.github.kokorin.watcher.vk.QueryMaker
import com.typesafe.config.ConfigFactory
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.File

suspend fun sequentialRequests(str: String): String {
    HttpClient().use {
        return it.get<String>(str)
    }
}


fun main()  = runBlocking {
    val logger = LoggerFactory.getLogger("com.github.kokorin.watcher.main-logger")
    logger.debug("Application started")

    val vkConfig = VkConfigImpl(
        ConfigFactory.parseFile(File("src/main/resources/application.conf")).getConfig("vk")
    )

    val str = QueryMaker(vkConfig.version, vkConfig.accessToken, "вконтакте").makeQuery(2)

    val task = GlobalScope.launch(Dispatchers.IO) {
        println(sequentialRequests(str))
    }
    task.join()

}
