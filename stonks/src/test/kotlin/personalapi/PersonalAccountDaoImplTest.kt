package personalapi

import utils.contailer.PausableFixedHostPortGenericContainer
import utils.http.StockMarketAdminHttpClient
import exchange.model.Stocks
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import personalapi.config.StockMarketClientConfig
import personalapi.dao.PersonalAccountDao
import personalapi.dao.PersonalAccountDaoImpl
import personalapi.http.KtorStockMarketHttpClient
import personalapi.model.CompanyStocks

class PersonalAccountDaoImplTest {
    private val stockMarketClientConfig = object : StockMarketClientConfig {
        override val host: String = "localhost"
        override val port: Int = 8841
        override val schema: String = "http"

    }

    private val stockMarketContainer: PausableFixedHostPortGenericContainer =
        PausableFixedHostPortGenericContainer("kokorin/exchange:latest")
            .withFixedExposedPort(stockMarketClientConfig.port, 8080)
            .withExposedPorts(8080)
    private val parser = Json(JsonConfiguration.Stable)
    private val stockMarketHttpClient = KtorStockMarketHttpClient(stockMarketClientConfig, parser)
    private val stockMarketAdminHttpClient = StockMarketAdminHttpClient(stockMarketClientConfig)
    private lateinit var dao: PersonalAccountDao

    @Before
    fun startContainer() {
        stockMarketContainer.start()
        dao = PersonalAccountDaoImpl(stockMarketHttpClient)
    }

    @After
    fun stopContainer() {
        stockMarketContainer.stop()
    }

    @Test
    fun testGetNonExistingUser() {
        assertEquals(null, dao.getUserBalance(0))
    }

    @Test
    fun testAddUser() {
        val id = dao.addUser("user")
        assertEquals(0, dao.getUserBalance(id))
    }

    @Test
    fun testAddMoneyToNonExistingUser() {
        assertFalse(dao.addMoneyToUser(0, 100))
    }

    @Test
    fun testAddMoney() {
        val id = dao.addUser("user")
        assertEquals(0, dao.getUserBalance(id))
        dao.addMoneyToUser(id, 100)
        assertEquals(100, dao.getUserBalance(id))
    }

    @Test
    fun testBuyStocks() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        assertTrue(dao.buyStocks(id, "company", 3, 30))
        assertEquals(dao.getUserBalance(id), 10)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 3, 30))
        )
    }

    @Test
    fun testBuyTooManyStocks() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 1000000)
        assertFalse(dao.buyStocks(id, "company", 20, 30))
        assertEquals(dao.getUserBalance(id), 1000000)
        assertTrue(
            dao.getUserStocks(id) == listOf<CompanyStocks>()
        )
    }

    @Test
    fun testBuyIncorrectStocks() = runBlocking {
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        assertFalse(dao.buyStocks(id, "company", 3, 30))
        assertEquals(dao.getUserBalance(id), 100)
    }

    @Test
    fun testBuyStocksAndChangePrice() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        assertTrue(dao.buyStocks(id, "company", 3, 30))
        assertEquals(dao.getUserBalance(id), 10)
        stockMarketAdminHttpClient.changePrice("company", 100)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 3, 100))
        )
        assertEquals(dao.getTotalUserMoney(id), 310)
    }

    @Test
    fun testFailedBuyAfterPriceChange() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        assertTrue(dao.buyStocks(id, "company", 2, 30))
        assertEquals(dao.getUserBalance(id), 40)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 2, 30))
        )
        stockMarketAdminHttpClient.changePrice("company", 20)
        assertFalse(dao.buyStocks(id, "company", 1, 30))
        assertEquals(dao.getUserBalance(id), 40)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 2, 20))
        )
    }

    @Test
    fun testFailedBuyAfterPriceChangeNotEnoughMoney() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        assertTrue(dao.buyStocks(id, "company", 2, 30))
        assertEquals(dao.getUserBalance(id), 40)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 2, 30))
        )
        stockMarketAdminHttpClient.changePrice("company", 100)
        try {
            dao.buyStocks(id, "company", 1, 100)
            fail("Shouldn't reach this code")
        } catch (e: Exception) {
            assertEquals(dao.getUserBalance(id), 40)
            assertTrue(
                dao.getUserStocks(id) == listOf(CompanyStocks("company", 2, 100))
            )
        }
    }

    @Test
    fun testBuyAndSellStocks() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        assertTrue(dao.buyStocks(id, "company", 3, 30))
        assertEquals(dao.getUserBalance(id), 10)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 3, 30))
        )
        assertEquals(dao.sellStocks(id, "company", 2), 60)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 1, 30))
        )
        assertEquals(dao.getUserBalance(id), 70)
    }

    @Test
    fun testBuyAndSellStocksAfterPriceChange() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        assertTrue(dao.buyStocks(id, "company", 3, 30))
        assertEquals(dao.getUserBalance(id), 10)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 3, 30))
        )
        stockMarketAdminHttpClient.changePrice("company", 100)
        assertEquals(dao.sellStocks(id, "company", 2), 200)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 1, 100))
        )
        assertEquals(dao.getUserBalance(id), 210)
    }

    @Test(expected = IllegalStateException::class)
    fun testSellIncorrectStocks() = runBlocking {
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        val id = dao.addUser("user")
        dao.sellStocks(id, "company", 1)
        Unit
    }

    @Test
    fun testBuyWithNetworkError() = runBlocking {
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        stockMarketContainer.pauseContainer()
        assertFalse(dao.buyStocks(id, "company", 3, 30))
        stockMarketContainer.resumeContainer()
        assertEquals(dao.getUserBalance(id), 100)
        assertTrue(
            dao.getUserStocks(id) == listOf<CompanyStocks>()
        )
    }

    @Test
    fun testSellWithNetworkError() = runBlocking {
        val id = dao.addUser("user")
        dao.addMoneyToUser(id, 100)
        stockMarketAdminHttpClient.addCompany("company", Stocks(10, 30))
        assertTrue(dao.buyStocks(id, "company", 3, 30))
        assertEquals(dao.getUserBalance(id), 10)
        assertTrue(
            dao.getUserStocks(id) == listOf(CompanyStocks("company", 3, 30))
        )
        stockMarketContainer.pauseContainer()
        try {
            dao.sellStocks(id, "company", 3)
            fail("Shouldn't reach this code")
        } catch (e: Exception) {
            stockMarketContainer.resumeContainer()
            assertEquals(dao.getUserBalance(id), 10)
            assertTrue(
                dao.getUserStocks(id) == listOf(CompanyStocks("company", 3, 30))
            )
        }
    }
}
