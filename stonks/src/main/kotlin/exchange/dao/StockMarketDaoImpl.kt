package exchange.dao

import exchange.model.BuyResult
import exchange.model.Stocks
import java.util.concurrent.ConcurrentHashMap

class StockMarketDaoImpl : StockMarketDao {
    private val inMemoryDatabase = ConcurrentHashMap<String, Stocks>()

    override fun addCompany(company: String, stocks: Stocks): Boolean {
        return inMemoryDatabase.putIfAbsent(company, stocks) == null
    }

    override fun addStocks(company: String, count: Int): Boolean {
        return inMemoryDatabase.computeIfPresent(company) { _, shares ->
            shares.copy(count = shares.count + count)
        } != null
    }

    override fun changePrice(company: String, newPrice: Int): Boolean {
        return inMemoryDatabase.computeIfPresent(company) { _, shares ->
            shares.copy(price = newPrice)
        } != null
    }

    override fun getStocks(company: String): Stocks? = inMemoryDatabase[company]

    override fun buyStocks(company: String, count: Int, price: Int): BuyResult {
        var result = false
        inMemoryDatabase.computeIfPresent(company) { _, stocks ->
            if (stocks.price == price && stocks.count >= count) {
                result = true
                stocks.copy(count = stocks.count - count)
            } else {
                stocks
            }
        } ?: return BuyResult.COMPANY_NOT_EXISTS
        return if (result) {
            BuyResult.STOCKS
        } else {
            BuyResult.PRICE_CHANGED_OR_NOT_ENOUGH_STOCKS
        }
    }

    override fun sellStocks(company: String, count: Int): Int? {
        val oldStocks = inMemoryDatabase.computeIfPresent(company) { _, stocks ->
            stocks.copy(count = stocks.count + count)
        }
        return oldStocks?.price?.let { it * count }
    }
}
