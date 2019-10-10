package com.github.kokorin.products.servlet

import com.github.kokorin.products.commands.Command
import com.github.kokorin.products.dao.ProductsReadDao
import javax.servlet.http.HttpServletRequest

class QueryServlet(private val productsReadDao: ProductsReadDao) : ProductsProcessingServlet() {
    override fun processRequest(request: HttpServletRequest): String {
        val command = Command.makeCommand(request.getParameter("command"))
        return command.query(productsReadDao)
    }
}
