package personalapi.http

import exchange.model.Stocks
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import personalapi.config.StockMarketClientConfig

class KtorStockMarketHttpClient(private val config: StockMarketClientConfig, private val parser: Json) :
    AbstractHttpClient(),
    StockMarketHttpClient {
    override suspend fun getCompanyStockPrices(companies: List<String>): List<Int> {
        return coroutineScope {
            companies.map {
                async {
                    val url = "${config.schema}://${config.host}:${config.port}/get_stocks?company=$it"
                    doGet(url)
                }
            }
        }.map {
            it.await()
        }.map {
            parser.parse(Stocks.serializer(), it).price
        }
    }

    override suspend fun buyStocks(company: String, count: Int, price: Int): Boolean {
        val url =
            "${config.schema}://${config.host}:${config.port}/buy_stocks?company=$company&count=$count&price=$price"
        return doGet(url) == "Stonks!"
    }

    override suspend fun sellStocks(company: String, count: Int): Int {
        val url = "${config.schema}://${config.host}:${config.port}/sell_stocks?company=$company&count=$count"
        return doGet(url).toInt()
    }

    override fun close() {
        client.close()
    }

    override val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
        }
        expectSuccess = false
    }
}
