package com.github.kokorin.rx.currency

import com.github.kokorin.rx.model.Currency

class CurrencyConverter(val conversionMap: Map<Pair<Currency, Currency>, Double>) {
    fun convert(amount: Double, from: Currency, to: Currency) =
        amount * (conversionMap[Pair(from, to)] ?: error("Conversion from $from to $to not supported"))
}
