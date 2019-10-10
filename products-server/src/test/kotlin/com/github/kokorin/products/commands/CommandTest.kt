package com.github.kokorin.products.commands

import com.github.kokorin.products.dao.ProductsReadDao
import com.github.kokorin.products.model.Product
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.jsoup.Jsoup

class CommandTest {
    @Test
    fun testMax() {
        val command = Command.makeCommand("max")
        val productsReadDao = mock(ProductsReadDao::class.java)
        `when`(productsReadDao.getMaxPriceProduct()).thenReturn(Product("some good", 2517))
        val result = Jsoup.parse(command.query(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with max price: </h1> some good 2517\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testMaxNull() {
        val command = Command.makeCommand("max")
        val productsReadDao = mock(ProductsReadDao::class.java)
        `when`(productsReadDao.getMaxPriceProduct()).thenReturn(null)
        val result = Jsoup.parse(command.query(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with max price: </h1> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testMin() {
        val command = Command.makeCommand("min")
        val productsReadDao = mock(ProductsReadDao::class.java)
        `when`(productsReadDao.getMinPriceProduct()).thenReturn(Product("some good", 2517))
        val result = Jsoup.parse(command.query(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with min price: </h1> some good 2517\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testMinNull() {
        val command = Command.makeCommand("min")
        val productsReadDao = mock(ProductsReadDao::class.java)
        `when`(productsReadDao.getMinPriceProduct()).thenReturn(null)
        val result = Jsoup.parse(command.query(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with min price: </h1> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testSum() {
        val command = Command.makeCommand("sum")
        val productsReadDao = mock(ProductsReadDao::class.java)
        `when`(productsReadDao.getPricesSum()).thenReturn(14)
        val result = Jsoup.parse(command.query(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   Summary price: 14\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testCount() {
        val command = Command.makeCommand("count")
        val productsReadDao = mock(ProductsReadDao::class.java)
        `when`(productsReadDao.getCount()).thenReturn(14)
        val result = Jsoup.parse(command.query(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   Number of products: 14\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun unknownCommand() {
        val command = Command.makeCommand("abacaba")
        val productsReadDao = mock(ProductsReadDao::class.java)
        val result = command.query(productsReadDao)
        val expectedAnswer = "Unknown command: abacaba"
        assertEquals(result, expectedAnswer)
    }
}
