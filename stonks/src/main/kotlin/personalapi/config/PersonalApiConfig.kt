package personalapi.config

import com.typesafe.config.Config

interface PersonalApiConfig {
    val apiConfig: ApiConfig
    val stockMarketClientConfig: StockMarketClientConfig
}

class PersonalApiConfigImpl(conf: Config) : PersonalApiConfig {
    override val apiConfig: ApiConfig = ApiConfigImpl(conf.getConfig("api"))
    override val stockMarketClientConfig: StockMarketClientConfig =
        StockMarketClientConfigImpl(conf.getConfig("client"))

}
