package com.github.kokorin.products.dao

import com.github.kokorin.products.config.SqlCommandsHolder
import com.github.kokorin.products.config.SqlCommandsHolderImpl
import com.github.kokorin.products.connection.ConnectionProvider
import com.github.kokorin.products.model.Product
import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Paths
import org.mockito.Mockito.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class ProductsDaoImplTest {
    private val sqlCommandsHolder: SqlCommandsHolder = SqlCommandsHolderImpl(Paths.get("src/main/sql"))

    @Test
    fun testCount() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(sqlCommandsHolder.productsCountCommand)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getInt(1)).thenReturn(14)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getCount()
        assertEquals(result, 14)
    }

    @Test
    fun testPricesSum() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(sqlCommandsHolder.pricesSumCommand)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getInt(1)).thenReturn(25)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getPricesSum()
        assertEquals(result, 25)
    }

    @Test
    fun testCreateTable() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        `when`(statement.executeUpdate(sqlCommandsHolder.createCommand)).thenReturn(0)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        dao.createProductsTable()
        verify(statement, times(1)).executeUpdate(sqlCommandsHolder.createCommand)
    }

    @Test
    fun testDelete() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        `when`(statement.executeUpdate(sqlCommandsHolder.deleteAllCommand)).thenReturn(0)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        dao.deleteAllProducts()
        verify(statement, times(1)).executeUpdate(sqlCommandsHolder.deleteAllCommand)
    }

    @Test
    fun testMin() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val preparedStatement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(sqlCommandsHolder.selectByMinPriceCommand)).thenReturn(preparedStatement)
        doNothing().`when`(preparedStatement).setInt(1, 1)
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getString("name")).thenReturn("some good")
        `when`(resultSet.getInt("price")).thenReturn(2517)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getMinPriceProduct()
        assertEquals(result, Product("some good", 2517))
    }

    @Test
    fun testMax() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val preparedStatement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(sqlCommandsHolder.selectByMaxPriceCommand)).thenReturn(preparedStatement)
        doNothing().`when`(preparedStatement).setInt(1, 1)
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getString("name")).thenReturn("some good")
        `when`(resultSet.getInt("price")).thenReturn(2517)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getMaxPriceProduct()
        assertEquals(result, Product("some good", 2517))
    }

    @Test
    fun testMinEmpty() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val preparedStatement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(sqlCommandsHolder.selectByMinPriceCommand)).thenReturn(preparedStatement)
        doNothing().`when`(preparedStatement).setInt(1, 1)
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(false)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getMinPriceProduct()
        assertEquals(result, null)
    }

    @Test
    fun testMaxEmpty() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val preparedStatement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(sqlCommandsHolder.selectByMaxPriceCommand)).thenReturn(preparedStatement)
        doNothing().`when`(preparedStatement).setInt(1, 1)
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(false)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getMaxPriceProduct()
        assertEquals(result, null)
    }

    @Test
    fun testAdd() {
        val product = Product("some good", 2517)
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val preparedStatement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(sqlCommandsHolder.insertProductCommand)).thenReturn(preparedStatement)
        doNothing().`when`(preparedStatement).setString(1, product.name)
        doNothing().`when`(preparedStatement).setInt(2, product.price)
        `when`(preparedStatement.executeUpdate()).thenReturn(0)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        dao.addProduct(product)
        verify(preparedStatement, times(1)).setString(1, product.name)
        verify(preparedStatement, times(1)).setInt(2, product.price)
    }

    @Test
    fun testGetAllProducts() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(sqlCommandsHolder.selectAllCommand)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)
        `when`(resultSet.getString("name"))
            .thenReturn("good 1")
            .thenReturn("good 2")
        `when`(resultSet.getInt("price"))
            .thenReturn(25)
            .thenReturn(17)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getAllProducts()
        assertEquals(result.toList(), listOf(Product("good 1", 25), Product("good 2", 17)))
    }

    @Test
    fun testGetEmptyProductsList() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(sqlCommandsHolder.selectAllCommand)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(false)
        val dao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
        val result = dao.getAllProducts()
        assertEquals(result.toList(), listOf<Product>())
    }
}
