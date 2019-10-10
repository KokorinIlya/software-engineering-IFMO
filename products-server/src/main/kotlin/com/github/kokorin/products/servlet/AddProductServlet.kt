package com.github.kokorin.products.servlet

import com.github.kokorin.products.dao.ProductsWriteDao
import com.github.kokorin.products.model.Product
import javax.servlet.http.HttpServletRequest

class AddProductServlet(private val productsWriteDao: ProductsWriteDao) : ProductsProcessingServlet() {
    override fun processRequest(request: HttpServletRequest): String {
        val name = request.getParameter("name")
        val price = Integer.parseInt(request.getParameter("price"))
        productsWriteDao.addProduct(Product(name, price))
        return "OK"
    }
}
