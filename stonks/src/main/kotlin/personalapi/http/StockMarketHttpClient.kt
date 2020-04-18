package personalapi.http

interface StockMarketHttpClient : AutoCloseable {
    suspend fun getCompanyStockPrices(companies: List<String>): List<Int>
    suspend fun buyStocks(company: String, count: Int, price: Int): Boolean
    suspend fun sellStocks(company: String, count: Int): Int
}
