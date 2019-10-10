package com.github.kokorin.products.dao

import com.github.kokorin.products.config.SqlCommandsHolder
import com.github.kokorin.products.connection.ConnectionProvider
import com.github.kokorin.products.model.Product
import java.sql.SQLException

class ProductsDaoImpl(
    private val connectionProvider: ConnectionProvider,
    private val sqlCommandsHolder: SqlCommandsHolder
) : ProductsDao {
    override fun getCount(): Int {
        return getScalarResult(sqlCommandsHolder.productsCountCommand)
    }

    override fun getPricesSum(): Int {
        return getScalarResult(sqlCommandsHolder.pricesSumCommand)
    }

    override fun createProductsTable() {
        executeSimpleCommand(sqlCommandsHolder.createCommand)
    }

    override fun deleteAllProducts() {
        executeSimpleCommand(sqlCommandsHolder.deleteAllCommand)
    }

    override fun getMinPriceProduct(): Product? {
        return chooseFirst(sqlCommandsHolder.selectByMinPriceCommand)
    }

    override fun getMaxPriceProduct(): Product? {
        return chooseFirst(sqlCommandsHolder.selectByMaxPriceCommand)
    }

    override fun addProduct(product: Product) {
        val sql = sqlCommandsHolder.insertProductCommand
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, product.name)
                statement.setInt(2, product.price)
                statement.executeUpdate()
            }
        }
    }

    override fun getAllProducts(): List<Product> {
        val result = mutableListOf<Product>()
        val sql = sqlCommandsHolder.selectAllCommand
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(sql).use { resultSet ->
                    while (resultSet.next()) {
                        println("Getting")
                        val name = resultSet.getString("name")
                        val price = resultSet.getInt("price")
                        result.add(Product(name, price))
                    }
                }
            }
        }
        return result
    }

    private fun chooseFirst(sql: String): Product? {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, 1)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        val name = resultSet.getString("name")
                        val price = resultSet.getInt("price")
                        return Product(name, price)
                    }
                }
            }
        }
        return null
    }

    private fun getScalarResult(sql: String): Int {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(sql).use { resultSet ->
                    if (resultSet.next()) {
                        return resultSet.getInt(1)
                    }
                }
            }
        }
        throw SQLException("Single value expected as a result of sql query, but no value was given")
    }

    private fun executeSimpleCommand(sql: String) {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(sql)
            }
        }
    }
}
