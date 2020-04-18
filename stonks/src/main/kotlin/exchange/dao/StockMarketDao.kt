package exchange.dao

import exchange.model.BuyResult
import exchange.model.Stocks

interface StockMarketDao {
    fun addCompany(company: String, stocks: Stocks): Boolean
    fun addStocks(company: String, count: Int): Boolean
    fun changePrice(company: String, newPrice: Int): Boolean

    fun getStocks(company: String): Stocks?
    fun buyStocks(company: String, count: Int, price: Int): BuyResult
    fun sellStocks(company: String, count: Int): Int?
}
