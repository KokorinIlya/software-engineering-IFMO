package com.github.kokorin.rx.providers

import com.github.kokorin.rx.currency.CurrencyConverter
import com.github.kokorin.rx.model.Currency

// TODO: async callback + atomic reference
object StaticCurrencyConverterProvider : Provider<CurrencyConverter> {
    private val conversionMap = mapOf<Pair<Currency, Currency>, Double>(
        Pair(Pair(Currency.RUB, Currency.RUB), 1.0),
        Pair(Pair(Currency.USD, Currency.USD), 1.0),
        Pair(Pair(Currency.EUR, Currency.EUR), 1.0),

        Pair(Pair(Currency.RUB, Currency.USD), 0.0157125),
        Pair(Pair(Currency.RUB, Currency.EUR), 0.0145695),

        Pair(Pair(Currency.USD, Currency.RUB), 63.6436),
        Pair(Pair(Currency.USD, Currency.EUR), 0.927241),

        Pair(Pair(Currency.EUR, Currency.RUB), 68.6366),
        Pair(Pair(Currency.EUR, Currency.USD), 1.07847)
    )

    private val converter = CurrencyConverter(conversionMap)

    override fun get(): CurrencyConverter {
        return converter
    }
}
