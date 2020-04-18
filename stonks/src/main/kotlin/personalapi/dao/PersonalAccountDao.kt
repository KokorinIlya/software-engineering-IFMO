package personalapi.dao

import personalapi.model.CompanyStocks

interface PersonalAccountDao {
    fun addUser(name: String): Int
    fun addMoneyToUser(id: Int, count: Int): Boolean
    fun getUserBalance(id: Int): Int?

    suspend fun getUserStocks(id: Int): List<CompanyStocks>?
    suspend fun getTotalUserMoney(id: Int): Int?
    suspend fun buyStocks(id: Int, company: String, count: Int, price: Int): Boolean
    suspend fun sellStocks(id: Int, company: String, count: Int): Int
}
