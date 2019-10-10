package com.github.kokorin.products.servlet

import com.github.kokorin.products.dao.ProductsReadDao
import com.github.kokorin.products.model.Product
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import javax.servlet.http.HttpServletRequest

class QueryServletTest {
    @Test
    fun testMin() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("min")
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getMinPriceProduct()).thenReturn(Product("some good", 2517))
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with min price: </h1> some good 2517\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testMinNull() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("min")
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getMinPriceProduct()).thenReturn(null)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with min price: </h1> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testMax() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("max")
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getMaxPriceProduct()).thenReturn(Product("some good", 2517))
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with max price: </h1> some good 2517\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testMaxNull() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("max")
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getMaxPriceProduct()).thenReturn(null)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            "  <h1>Product with max price: </h1> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testCount() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("count")
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getCount()).thenReturn(14)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   Number of products: 14\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testSum() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("sum")
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getPricesSum()).thenReturn(42)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   Summary price: 42\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testUnknownCommand() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("abacaba")
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getPricesSum()).thenReturn(42)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "  Unknown command: abacaba\n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }
}
