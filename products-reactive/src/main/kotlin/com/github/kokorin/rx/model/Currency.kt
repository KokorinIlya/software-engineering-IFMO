package com.github.kokorin.rx.model

enum class Currency {
    RUB, USD, EUR;

    companion object {
        fun fromString(currencyName: String): Currency {
            val name = currencyName.toUpperCase()
            val supportedValues = values().map { it.name }
            if (name in supportedValues) {
                return valueOf(name)
            } else {
                throw IllegalArgumentException(
                    "Currency $currencyName not supported, supported currencies are $supportedValues"
                )
            }
        }
    }
}
