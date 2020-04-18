package utils.http

import exchange.model.Stocks
import io.ktor.client.HttpClient
import personalapi.config.StockMarketClientConfig
import personalapi.http.AbstractHttpClient

class StockMarketAdminHttpClient(private val config: StockMarketClientConfig) : AbstractHttpClient() {
    suspend fun addCompany(company: String, stocks: Stocks) {
        val url =
            "${config.schema}://${config.host}:${config.port}/add_company?company=$company&" +
                    "count=${stocks.count}&price=${stocks.price}"
        doGet(url)
    }

    suspend fun changePrice(company: String, price: Int) {
        val url = "http://${config.host}:${config.port}/change_price?company=$company&price=$price"
        doGet(url)
    }

    override val client = HttpClient { expectSuccess = false }
}
