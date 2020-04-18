package exchange.managers

import exchange.dao.StockMarketDao
import exchange.model.BuyResult
import exchange.model.Stocks

class StockMarketManager(private val dao: StockMarketDao) {
    fun addCompany(companyName: String, stocksCount: Int, stocksPrice: Int): String {
        val result = dao.addCompany(companyName, Stocks(stocksCount, stocksPrice))
        if (result) {
            return "Stonks!"
        } else {
            throw IllegalArgumentException("Company already exists")
        }
    }

    fun addStocks(companyName: String, count: Int): String {
        require(count >= 0) { "Cannot add negative amount of stocks" }
        val result = dao.addStocks(companyName, count)
        if (result) {
            return "Stonks!"
        } else {
            throw IllegalArgumentException("Company doesn't exist")
        }
    }

    fun changePrice(companyName: String, newPrice: Int): String {
        require(newPrice > 0) { "Price must be positive" }
        val result = dao.changePrice(companyName, newPrice)
        if (result) {
            return "Stonks!"
        } else {
            throw IllegalArgumentException("Company doesn't exist")
        }
    }

    fun getStocks(companyName: String): Stocks? {
        return dao.getStocks(companyName)
    }

    fun buyStocks(company: String, count: Int, price: Int): String {
        require(count >= 0) { "Cannot buy negative amount of stocks" }
        when (dao.buyStocks(company, count, price)) {
            BuyResult.STOCKS ->
                return "Stonks!"
            BuyResult.COMPANY_NOT_EXISTS ->
                throw IllegalArgumentException("Company doesn't exist")
            BuyResult.PRICE_CHANGED_OR_NOT_ENOUGH_STOCKS ->
                throw IllegalArgumentException("Price was changed concurrently of not enough stocks")
        }
    }

    fun sellStocks(companyName: String, count: Int): String {
        require(count >= 0) { "Cannot sell negative amount of stocks" }
        return dao.sellStocks(companyName, count)?.toString() ?: throw IllegalArgumentException("Company doesn't exist")
    }
}
