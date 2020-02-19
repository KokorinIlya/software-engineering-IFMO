package com.github.kokorin.rx.providers

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.github.kokorin.rx.config.CurrencyConversionConfig
import com.github.kokorin.rx.currency.CurrencyConverter
import com.github.kokorin.rx.http.HTTPClient
import com.github.kokorin.rx.model.Currency
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

class CurrencyConverterProviderImpl(
    startValue: CurrencyConverter,
    config: CurrencyConversionConfig,
    private val httpClientsProvider: Provider<HTTPClient>
) : Provider<CurrencyConverter> {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val curConverter = AtomicReference<CurrencyConverter>(startValue)
    private val scheduler = Executors.newScheduledThreadPool(1)

    constructor(config: CurrencyConversionConfig, httpClientsProvider: Provider<HTTPClient>) : this(
        getConversion(config, httpClientsProvider),
        config,
        httpClientsProvider
    )

    init {
        log.info("Initializing currency conversion: ${startValue.conversionMap}")
        val runnable = Runnable {
            try {
                val newConverter = getConversion(config, httpClientsProvider)
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

        private fun getConversion(
            config: CurrencyConversionConfig,
            httpClientsProvider: Provider<HTTPClient>
        ): CurrencyConverter {
            val url =
                "${config.schema}://${config.host}/${config.path}?q=USD_EUR,USD_RUB&compact=ultra&apiKey=${config.key}"
            val stringResponse = httpClientsProvider.get().use { it.getResponse(url) }
            val jsonResponse = parser.parse<CurrencyConversionResponse>(stringResponse)
                ?: error("Cannot parse currency conversion from $stringResponse")
            val resultMap = HashMap<Pair<Currency, Currency>, Double>()
            resultMap[Pair(Currency.USD, Currency.USD)] = 1.0
            resultMap[Pair(Currency.RUB, Currency.RUB)] = 1.0
            resultMap[Pair(Currency.EUR, Currency.EUR)] = 1.0

            resultMap[Pair(Currency.USD, Currency.EUR)] = jsonResponse.usdToEur
            resultMap[Pair(Currency.USD, Currency.RUB)] = jsonResponse.usdToRub

            resultMap[Pair(Currency.EUR, Currency.USD)] = 1.0 / jsonResponse.usdToEur
            resultMap[Pair(Currency.RUB, Currency.USD)] = 1.0 / jsonResponse.usdToRub

            resultMap[Pair(Currency.EUR, Currency.RUB)] = jsonResponse.usdToRub / jsonResponse.usdToEur
            resultMap[Pair(Currency.RUB, Currency.EUR)] = jsonResponse.usdToEur / jsonResponse.usdToRub

            return CurrencyConverter(resultMap.toMap())
        }
    }
}
