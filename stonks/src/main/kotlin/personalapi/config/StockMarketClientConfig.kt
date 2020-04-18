package personalapi.config

import com.typesafe.config.Config

interface StockMarketClientConfig {
    val host: String
    val port: Int
    val schema: String
}

class StockMarketClientConfigImpl(conf: Config) : StockMarketClientConfig {
    override val host: String = conf.getString("host")
    override val port: Int = conf.getInt("port")
    override val schema: String = conf.getString("schema")
}
