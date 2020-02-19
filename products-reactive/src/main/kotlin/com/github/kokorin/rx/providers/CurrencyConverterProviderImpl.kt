package com.github.kokorin.rx.providers

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.github.kokorin.rx.currency.CurrencyConverter
import com.github.kokorin.rx.model.Currency
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

data class CurrencyConversionResponse(
    @Json(name = "USD_EUR")
    val usdToEur: Double,
    @Json(name = "USD_RUB")
    val usdToRub: Double
)

class CurrencyConverterProviderImpl(startValue: CurrencyConverter, apiKey: String) :
    Provider<CurrencyConverter> {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val curConverter = AtomicReference<CurrencyConverter>(startValue)
    private val scheduler = Executors.newScheduledThreadPool(1)

    constructor(apiKey: String) : this(getConversion(apiKey), apiKey)

    init {
        log.info("Initializing currency conversion: ${startValue.conversionMap}")
        val runnable = Runnable {
            try {
                val newConverter = getConversion(apiKey)
                log.info("New currency conversion is ${newConverter.conversionMap}")
                val oldConverter = get()
                val updateResult = curConverter.compareAndSet(oldConverter, newConverter)
                log.info("Currency update result is $updateResult")
            } catch (e: Exception) {
                log.error("Error while updating currency conversion", e)
            }
        }
        scheduler.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.HOURS)
    }

    override fun get(): CurrencyConverter = curConverter.get()

    companion object {
        private val parser = Klaxon()

        private fun getConversion(apiKey: String): CurrencyConverter {
            val url =
                "https://free.currconv.com/api/v7/convert?q=USD_EUR,USD_RUB&compact=ultra&apiKey=$apiKey"
            val stringResponse = HttpClients.createDefault().use { client ->
                client.execute(HttpGet(url)).use { response ->
                    EntityUtils.toString(response.entity)
                }
            }

            val jsonResponse = parser.parse<CurrencyConversionResponse>(stringResponse)
                ?: error("Cannot parse currency conversion from $stringResponse")
            val resultMap = HashMap<Pair<Currency, Currency>, Double>()
            resultMap[Pair(Currency.USD, Currency.EUR)] = jsonResponse.usdToEur
            resultMap[Pair(Currency.USD, Currency.RUB)] = jsonResponse.usdToRub

            resultMap[Pair(Currency.EUR, Currency.USD)] = 1.0 / jsonResponse.usdToEur
            resultMap[Pair(Currency.RUB, Currency.USD)] = 1.0 / jsonResponse.usdToRub

            resultMap[Pair(Currency.EUR, Currency.RUB)] = jsonResponse.usdToRub / jsonResponse.usdToEur
            resultMap[Pair(Currency.RUB, Currency.USD)] = jsonResponse.usdToEur / jsonResponse.usdToRub
            return CurrencyConverter(resultMap.toMap())
        }
    }
}
