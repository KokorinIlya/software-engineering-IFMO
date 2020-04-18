package personalapi.managers

import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import personalapi.dao.PersonalAccountDao
import personalapi.model.CompanyStocks

class PersonalAccountManager(private val dao: PersonalAccountDao, private val parser: Json) {
    fun addUser(userName: String): String {
        val newId = dao.addUser(userName)
        return "Stonks! User with id $newId added"
    }

    fun addMoney(id: Int, count: Int): String {
        require(count > 0) { "Count must be positive" }
        val result = dao.addMoneyToUser(id, count)
        if (!result) {
            throw IllegalArgumentException("User doesn't exist")
        } else {
            return "Stonks!"
        }
    }

    suspend fun getStocksPrices(id: Int): String {
        val result = dao.getUserStocks(id) ?: throw IllegalArgumentException("User doesn't exist")
        return parser.stringify(CompanyStocks.serializer().list, result)
    }

    fun getMoneyAmount(id: Int): String {
        return dao.getUserBalance(id)?.toString() ?: throw IllegalArgumentException("User doesn't exist")
    }

    suspend fun getStocksPriceSum(id: Int): String {
        return dao.getTotalUserMoney(id)?.toString() ?: throw IllegalArgumentException("User doesn't exist")
    }

    suspend fun buyStocks(id: Int, company: String, count: Int, price: Int): String {
        require(count > 0) { "Cannot buy negative amount of stocks" }
        require(price > 0) { "Price must be positive" }
        return if (dao.buyStocks(id, company, count, price)) {
            "Stonks!"
        } else {
            "Not stonks :("
        }
    }

    suspend fun sellStocks(id: Int, company: String, count: Int): String {
        require(count > 0) { "Cannot sell negative amount of stocks" }
        return dao.sellStocks(id, company, count).toString()
    }
}
