package com.github.kokorin.products.servlet

import com.github.kokorin.products.dao.ProductsReadDao
import com.github.kokorin.products.model.Product
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import javax.servlet.http.HttpServletRequest

class GetProductsServletTest {
    @Test
    fun testGet() {
        val request = mock(HttpServletRequest::class.java)
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getAllProducts())
            .thenReturn(listOf(Product("good 1", 2517), Product("good 2", 1337)))
        val servlet = GetProductsServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   good 1 2517\n" +
            "  <br> good 2 1337\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testEmptyGet() {
        val request = mock(HttpServletRequest::class.java)
        val dao = mock(ProductsReadDao::class.java)
        `when`(dao.getAllProducts()).thenReturn(listOf())
        val servlet = GetProductsServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(result, expectedResult)
    }
}
