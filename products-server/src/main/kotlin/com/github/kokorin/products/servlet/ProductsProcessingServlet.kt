package com.github.kokorin.products.servlet

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class ProductsProcessingServlet : HttpServlet() {
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val actionResult = processRequest(request)
        response.contentType = "text/html"
        response.status = HttpServletResponse.SC_OK
        response.writer.println(actionResult)
    }

    abstract fun processRequest(request: HttpServletRequest): String
}
