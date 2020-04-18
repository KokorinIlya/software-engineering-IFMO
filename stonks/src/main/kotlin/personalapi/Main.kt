package personalapi

import com.typesafe.config.ConfigFactory
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import personalapi.dao.PersonalAccountDaoImpl
import personalapi.http.KtorStockMarketHttpClient
import java.nio.file.Paths
import io.ktor.routing.get
import io.ktor.routing.routing
import personalapi.config.PersonalApiConfigImpl
import personalapi.managers.PersonalAccountManager

fun main() = runBlocking {
    val configFile = Paths.get("src/main/resources/personal_api.conf").toFile()
    val config = ConfigFactory.parseFile(configFile)
    val personalApiConfig = PersonalApiConfigImpl(config)

    val parser = Json(JsonConfiguration.Stable)
    val stockMarketHttpClient = KtorStockMarketHttpClient(personalApiConfig.stockMarketClientConfig, parser)
    val dao = PersonalAccountDaoImpl(stockMarketHttpClient)
    val manager = PersonalAccountManager(dao, Json(JsonConfiguration.Stable))
    embeddedServer(Netty, port = personalApiConfig.apiConfig.port) {
        install(StatusPages) {
            exception<Exception> { ex ->
                call.respondText(
                    "Exception during executing request: ${ex.message}",
                    status = HttpStatusCode.BadRequest
                )
            }
        }
        routing {
            get("/add_user") {
                val userName = call.request.queryParameters["name"]
                    ?: throw IllegalArgumentException("User name should be provided")
                val response = manager.addUser(userName)
                call.respondText(response)
            }
            get("/add_money") {
                val id = call.request.queryParameters["id"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val count = call.request.queryParameters["count"]?.toInt()
                    ?: throw IllegalArgumentException("Amount of money should be provided")
                val response = manager.addMoney(id, count)
                call.respondText(response)
            }
            get("/get_balance") {
                val id = call.request.queryParameters["id"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val response = manager.getMoneyAmount(id)
                call.respondText(response)
            }
            get("/get_stock_prices") {
                val id = call.request.queryParameters["id"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val response = manager.getStocksPrices(id)
                call.respondText(response)
            }
            get("/get_total_balance") {
                val id = call.request.queryParameters["id"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val response = manager.getStocksPriceSum(id)
                call.respondText(response)
            }
            get("/buy_stocks") {
                val id = call.request.queryParameters["id"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("User id should be provided")
                val count = call.request.queryParameters["count"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val price = call.request.queryParameters["price"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val response = manager.buyStocks(id, company, count, price)
                call.respondText(response)
            }
            get("/sell_stocks") {
                val id = call.request.queryParameters["id"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val company = call.request.queryParameters["company"]
                    ?: throw IllegalArgumentException("User id should be provided")
                val count = call.request.queryParameters["count"]?.toInt()
                    ?: throw IllegalArgumentException("User id should be provided")
                val response = manager.sellStocks(id, company, count)
                call.respondText(response)
            }
        }
    }.start(wait = true)
    Unit
}
