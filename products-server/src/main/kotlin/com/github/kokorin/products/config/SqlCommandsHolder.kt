package com.github.kokorin.products.config

import com.github.kokorin.products.utils.readFileAsString
import java.nio.file.Path

interface SqlCommandsHolder {
    val createCommand: String

    val deleteAllCommand: String

    val insertProductCommand: String

    val selectAllCommand: String

    val selectByMinPriceCommand: String

    val selectByMaxPriceCommand: String

    val productsCountCommand: String

    val pricesSumCommand: String
}

class SqlCommandsHolderImpl(pathToSql: Path) : SqlCommandsHolder {
    override val createCommand: String = readFileAsString(pathToSql.resolve("create-database.sql"))

    override val deleteAllCommand: String = readFileAsString(pathToSql.resolve("delete-all.sql"))

    override val insertProductCommand: String = readFileAsString(pathToSql.resolve("insert-product.sql"))

    override val selectAllCommand: String = readFileAsString(pathToSql.resolve("select-all.sql"))

    override val selectByMinPriceCommand: String = readFileAsString(pathToSql.resolve("select-by-min-price.sql"))

    override val selectByMaxPriceCommand: String = readFileAsString(pathToSql.resolve("select-by-max-price.sql"))

    override val productsCountCommand: String = readFileAsString(pathToSql.resolve("get-products-count.sql"))

    override val pricesSumCommand: String = readFileAsString(pathToSql.resolve("get-prices-sum.sql"))
}
