package com.github.kokorin.products.servlet

import com.github.kokorin.products.dao.ProductsReadDao
import com.github.kokorin.products.response.ResponseBuilder
import javax.servlet.http.HttpServletRequest

class GetProductsServlet(private val productsReadDao: ProductsReadDao) : ProductsProcessingServlet() {
    override fun processRequest(request: HttpServletRequest): String {
        val responseBuilder = ResponseBuilder()
        productsReadDao.getAllProducts().forEach {
            responseBuilder.addResponseElement("${it.name}\t${it.price}")
        }
        return responseBuilder.buildAnswer()
    }
}
