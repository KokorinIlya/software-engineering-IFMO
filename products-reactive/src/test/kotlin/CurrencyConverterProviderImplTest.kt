import com.github.kokorin.rx.config.CurrencyConversionConfig
import com.github.kokorin.rx.http.HTTPClient
import com.github.kokorin.rx.model.Currency
import com.github.kokorin.rx.providers.CurrencyConverterProviderImpl
import com.github.kokorin.rx.providers.Provider
import com.sun.org.apache.xalan.internal.lib.ExsltMath.abs
import org.junit.Test
import org.junit.Assert.*

class CurrencyConverterProviderImplTest {
    @Test
    fun testGettingConversion() {
        val config = object : CurrencyConversionConfig {
            override val host: String = "host"
            override val path: String = "path/to/service"
            override val schema: String = "https"
            override val key: String = "api_key"
        }
        val httpClientsProvider = object : Provider<HTTPClient> {
            override fun get(): HTTPClient {
                return object : HTTPClient {
                    override fun getResponse(url: String): String {
                        return if (url == "https://host/path/to/service?q=USD_EUR,USD_RUB&compact=ultra&apiKey=api_key") {
                            "{\"USD_EUR\": 0.925265, \"USD_RUB\": 63.5442}"
                        } else {
                            "ERROR"
                        }
                    }

                    override fun close() {
                    }

                }
            }

        }
        val provider = CurrencyConverterProviderImpl(config, httpClientsProvider)
        val conversionMap = provider.get().conversionMap
        val expectedMap = mapOf(
            Pair(Pair(Currency.RUB, Currency.RUB), 1.0),
            Pair(Pair(Currency.USD, Currency.USD), 1.0),
            Pair(Pair(Currency.EUR, Currency.EUR), 1.0),

            Pair(Pair(Currency.RUB, Currency.USD), 0.01573707749),
            Pair(Pair(Currency.RUB, Currency.EUR), 0.0145695),

            Pair(Pair(Currency.USD, Currency.RUB), 63.5442),
            Pair(Pair(Currency.USD, Currency.EUR), 0.925265),

            Pair(Pair(Currency.EUR, Currency.RUB), 68.67675746948171),
            Pair(Pair(Currency.EUR, Currency.USD), 1.0807714546643394)
        )
        assertTrue(conversionMap.keys == expectedMap.keys)
        val eps = 1e-5
        for (key in conversionMap.keys) {
            assertTrue(abs(conversionMap[key]!! - expectedMap[key]!!) < eps)
        }
    }
}
