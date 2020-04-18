package personalapi.dao

import org.slf4j.LoggerFactory
import personalapi.http.StockMarketHttpClient
import personalapi.model.CompanyStocks
import personalapi.model.User
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

class PersonalAccountDaoImpl(private val client: StockMarketHttpClient) : PersonalAccountDao {
    private val usersInMemoryDatabase = ConcurrentHashMap<Int, User>()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun addUser(name: String): Int {
        while (true) {
            val newId = usersInMemoryDatabase.size
            usersInMemoryDatabase.putIfAbsent(newId, User(name, 0, emptyMap())) ?: return newId
        }
    }

    override fun addMoneyToUser(id: Int, count: Int): Boolean {
        return usersInMemoryDatabase.computeIfPresent(id) { _, user ->
            user.copy(balance = user.balance + count)
        } != null
    }

    override fun getUserBalance(id: Int): Int? = usersInMemoryDatabase[id]?.balance

    override suspend fun getUserStocks(id: Int): List<CompanyStocks>? {
        return usersInMemoryDatabase[id]?.let {
            val userCompanies = it.stocksAmountByCompanyName.keys
            val prices = client.getCompanyStockPrices(userCompanies.toList())
            return it.stocksAmountByCompanyName.toList().zip(prices).map { (stock, price) ->
                val companyName = stock.first
                val stocksAmount = stock.second
                CompanyStocks(companyName, stocksAmount, price)
            }
        }
    }

    override suspend fun getTotalUserMoney(id: Int): Int? {
        val stocksPrice = getUserStocks(id)?.sumBy { it.count * it.price }
        val balance = getUserBalance(id)
        return stocksPrice?.plus(balance ?: 0)
    }

    private fun checkUserExists(id: Int) {
        require(usersInMemoryDatabase.containsKey(id)) {
            "User with id = $id doesn't exist"
        }
    }

    override suspend fun buyStocks(id: Int, company: String, count: Int, price: Int): Boolean {
        checkUserExists(id)
        var hasEnoughMoney = false
        usersInMemoryDatabase.computeIfPresent(id) { _, user ->
            if (user.balance >= count * price) {
                hasEnoughMoney = true
                user.copy(balance = user.balance - count * price)
            } else {
                user
            }
        }

        check(hasEnoughMoney) {
            "User with id = $id doesn't have enough money to but $count stocks for $price per stock"
        }
        val buyResult = try {
            client.buyStocks(company, count, price)
        } catch (e: Exception) {
            log.error("Error while buying stocks", e)
            false
        }

        return if (buyResult) {
            usersInMemoryDatabase.computeIfPresent(id) { _, user ->
                val curStocks = user.stocksAmountByCompanyName.toMutableMap()
                curStocks.compute(company) { _, curCount -> curCount?.plus(count) ?: count }
                user.copy(stocksAmountByCompanyName = curStocks.toMap())
            }
            true
        } else {
            usersInMemoryDatabase.computeIfPresent(id) { _, user ->
                user.copy(balance = user.balance + price * count)
            }
            false
        }
    }

    override suspend fun sellStocks(id: Int, company: String, count: Int): Int {
        checkUserExists(id)

        var hasEnoughStocks = false
        usersInMemoryDatabase.computeIfPresent(id) { _, user ->
            if (user.stocksAmountByCompanyName[company]?.let { it >= count } == true) {
                hasEnoughStocks = true
                val curStocks = user.stocksAmountByCompanyName.toMutableMap()
                curStocks.computeIfPresent(company) { _, curCount -> curCount - count }
                user.copy(stocksAmountByCompanyName = curStocks.toMap())
            } else {
                user
            }
        }
        check(hasEnoughStocks) {
            "User with id = $id doesn't have $count stocks of company $company"
        }

        val money = try {
            client.sellStocks(company, count)
        } catch (e: Exception) {
            usersInMemoryDatabase.computeIfPresent(id) { _, user ->
                val curStocks = user.stocksAmountByCompanyName.toMutableMap()
                curStocks.computeIfPresent(company) { _, curCount -> curCount + count }
                user.copy(stocksAmountByCompanyName = curStocks.toMap())
            }
            throw e
        }
        usersInMemoryDatabase.computeIfPresent(id) { _, user -> user.copy(balance = user.balance + money) }
        return money
    }
}
