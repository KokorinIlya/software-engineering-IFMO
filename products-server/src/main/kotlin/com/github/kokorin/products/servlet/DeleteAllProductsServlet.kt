package com.github.kokorin.products.servlet

import com.github.kokorin.products.dao.ProductsWriteDao
import javax.servlet.http.HttpServletRequest

class DeleteAllProductsServlet(private val productsWriteDao: ProductsWriteDao) : ProductsProcessingServlet() {
    override fun processRequest(request: HttpServletRequest): String {
        productsWriteDao.deleteAllProducts()
        return "OK"
    }
}
