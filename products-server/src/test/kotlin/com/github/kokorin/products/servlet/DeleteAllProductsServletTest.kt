package com.github.kokorin.products.servlet

import com.github.kokorin.products.dao.ProductsWriteDao
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import javax.servlet.http.HttpServletRequest

class DeleteAllProductsServletTest {
    @Test
    fun testDelete() {
        val request = mock(HttpServletRequest::class.java)
        val dao = mock(ProductsWriteDao::class.java)
        doNothing().`when`(dao).deleteAllProducts()
        val servlet = DeleteAllProductsServlet(dao)
        val result = servlet.processRequest(request)
        verify(dao, times(1)).deleteAllProducts()
        assertEquals(result, "OK")
    }
}
