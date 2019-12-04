package com.github.kokorin.todo.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.springframework.stereotype.Component
import java.io.File

interface ConfigProvider {
    val config: Config
}

@Component
object ConfigProviderImpl : ConfigProvider {
    override val config: Config = ConfigFactory.parseFile(File("src/main/resources/application.conf"))
}
