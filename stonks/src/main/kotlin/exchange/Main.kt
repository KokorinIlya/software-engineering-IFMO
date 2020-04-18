package exchange

import exchange.dao.StockMarketDaoImpl
import exchange.managers.StockMarketManager
import exchange.model.Stocks
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.Exception

fun main() = runBlocking {
    val parser = Json(JsonConfiguration.Stable)
    val dao = StockMarketDaoImpl()
    val manager = StockMarketManager(dao)

    embeddedServer(Netty, port = 8080) {
        install(StatusPages) {
            exception<Exception> { ex ->
                call.respondText(
                    "Exception during executing request: ${ex.message}",
                    status = HttpStatusCode.BadRequest
                )
            }
        }

        routing {
            get("/add_company") {
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("Company name should be provided")
                val count = call.request.queryParameters["count"]?.toInt()
                    ?: throw IllegalArgumentException("Stocks count should be provided")
                val price = call.request.queryParameters["price"]?.toInt()
                    ?: throw IllegalArgumentException("Stocks price should be provided")
                val result = manager.addCompany(company, count, price)
                call.respondText(result)
            }
            get("/add_stocks") {
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("Company name should be provided")
                val count = call.request.queryParameters["count"]?.toInt()
                    ?: throw IllegalArgumentException("Stocks count should be provided")
                val result = manager.addStocks(company, count)
                call.respondText(result)
            }
            get("/get_stocks") {
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("Company name should be provided")
                val stocks = manager.getStocks(company)
                val response = stocks?.let { parser.stringify(Stocks.serializer(), stocks) }
                    ?: "Company doesn't exist"
                call.respondText(response)
            }
            get("/buy_stocks") {
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("Company name should be provided")
                val count = call.request.queryParameters["count"]?.toInt()
                    ?: throw IllegalArgumentException("Stocks count should be provided")
                val price = call.request.queryParameters["price"]?.toInt()
                    ?: throw IllegalArgumentException("Stocks price should be provided")
                val response = manager.buyStocks(company, count, price)
                call.respondText(response)
            }
            get("/sell_stocks") {
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("Company name should be provided")
                val count = call.request.queryParameters["count"]?.toInt()
                    ?: throw IllegalArgumentException("Stocks count should be provided")
                val response = manager.sellStocks(company, count)
                call.respondText(response)
            }
            get("/change_price") {
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("Company name should be provided")
                val price = call.request.queryParameters["price"]?.toInt()
                    ?: throw IllegalArgumentException("New stonks price should be provided")
                val response = manager.changePrice(company, price)
                call.respondText(response)
            }
        }
    }.start(wait = true)
    Unit
}
