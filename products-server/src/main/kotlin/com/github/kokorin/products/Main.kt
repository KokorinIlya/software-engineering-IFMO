package com.github.kokorin.products

import com.github.kokorin.products.config.ApplicationConfig
import com.github.kokorin.products.config.ApplicationConfigImpl
import com.github.kokorin.products.config.SqlCommandsHolder
import com.github.kokorin.products.config.SqlCommandsHolderImpl
import com.github.kokorin.products.connection.ConnectionProvider
import com.github.kokorin.products.connection.ConnectionProviderImpl
import com.github.kokorin.products.dao.ProductsDaoImpl
import com.github.kokorin.products.servlet.AddProductServlet
import com.github.kokorin.products.servlet.DeleteAllProductsServlet
import com.github.kokorin.products.servlet.GetProductsServlet
import com.github.kokorin.products.servlet.QueryServlet
import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.io.File
import java.nio.file.Paths
import javax.servlet.http.HttpServlet

fun main() {
    val config: ApplicationConfig = ApplicationConfigImpl(
        ConfigFactory.parseFile(File("src/main/resources/application.conf"))
    )
    val sqlCommandsHolder: SqlCommandsHolder = SqlCommandsHolderImpl(Paths.get("src/main/sql"))

    val connectionProvider: ConnectionProvider = ConnectionProviderImpl(config.database)
    val productsDao = ProductsDaoImpl(connectionProvider, sqlCommandsHolder)
    productsDao.createProductsTable()

    val server = Server(config.port)

    val context = ServletContextHandler(ServletContextHandler.SESSIONS)
    context.contextPath = "/"
    server.handler = context

    val servlets = mapOf<String, HttpServlet> (
        Pair("/add-product", AddProductServlet(productsDao)),
        Pair("/get-products", GetProductsServlet(productsDao)),
        Pair("/query", QueryServlet(productsDao)),
        Pair("/clear", DeleteAllProductsServlet(productsDao))
    )
    servlets.forEach { (path, servlet) ->
        context.addServlet(ServletHolder(servlet), path)
    }

    server.start()
    server.join()
}
