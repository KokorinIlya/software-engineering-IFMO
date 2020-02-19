package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import com.github.kokorin.rx.model.Currency
import com.github.kokorin.rx.model.Product
import com.github.kokorin.rx.model.User
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import rx.Observable
import java.lang.IllegalArgumentException

interface Command {
    fun process(dao: ReactiveDao): Observable<String>

    companion object {
        private fun <T> HttpServerRequest<T>.getQueryParam(paramName: String): String {
            val paramsList = this.queryParameters[paramName]?.toList() ?: error("Parameter $paramName is required")
            return paramsList[0]
        }

        fun <T> makeCommand(request: HttpServerRequest<T>): Command {
            return try {
                when (val command = request.decodedPath.substring(1)) {
                    "add_user" -> {
                        val id = request.getQueryParam("id").toLong()
                        val currency = Currency.fromString(request.getQueryParam("currency"))
                        AddUserCommand(User(id, currency))
                    }
                    "get_user" -> {
                        val id = request.getQueryParam("id").toLong()
                        GetUserCommand(id)
                    }
                    "add_product" -> {
                        val id = request.getQueryParam("id").toLong()
                        val currency = Currency.fromString(request.getQueryParam("currency"))
                        val price = request.getQueryParam("price").toDouble()
                        val name = request.getQueryParam("name")
                        AddProductCommand(Product(id, name, price, currency))
                    }
                    "get_product" -> {
                        val id = request.getQueryParam("id").toLong()
                        GetProductCommand(id)
                    }
                    "delete_products" -> {
                        DeleteProductsCommand
                    }
                    "delete_users" -> {
                        DeleteUsersCommand
                    }
                    "list_for_user" -> {
                        val id = request.getQueryParam("id").toLong()
                        GetProductsForUserCommand(id)
                    }
                    else -> InvalidCommand(IllegalArgumentException("No such command $command"))
                }
            } catch (e: Exception) {
                InvalidCommand(e)
            }
        }
    }
}
