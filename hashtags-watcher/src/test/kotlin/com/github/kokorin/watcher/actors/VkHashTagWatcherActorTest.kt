package com.github.kokorin.watcher.actors

import com.github.kokorin.watcher.clients.vk.AsyncVkClient
import com.github.kokorin.watcher.time.TimeConverter
import io.mockk.mockk
import java.util.*

class VkHashTagWatcherActorTest {
    fun testReceive() {
        val client = mockk<AsyncVkClient>()
        val curDate = Date()
        val timeConverter = TimeConverter(curDate)
        val hashtag = "Вконтакте"
    }
}
