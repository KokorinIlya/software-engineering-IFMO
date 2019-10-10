package com.github.kokorin.products.servlet

import com.github.kokorin.products.dao.ProductsWriteDao
import com.github.kokorin.products.model.Product
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import javax.servlet.http.HttpServletRequest

class AddProductsServletTest {
    @Test
    fun testAdd() {
        val request = mock(HttpServletRequest::class.java)
        val dao = mock(ProductsWriteDao::class.java)
        `when`(request.getParameter("name")).thenReturn("some good")
        `when`(request.getParameter("price")).thenReturn("2517")
        val product = Product("some good", 2517)
        doNothing().`when`(dao).addProduct(product)
        val servlet = AddProductServlet(dao)
        val result = servlet.processRequest(request)
        verify(dao, times(1)).addProduct(product)
        assertEquals(result, "OK")
    }
}
