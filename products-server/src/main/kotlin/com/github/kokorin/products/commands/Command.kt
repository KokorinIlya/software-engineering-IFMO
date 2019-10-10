package com.github.kokorin.products.commands

import com.github.kokorin.products.dao.ProductsReadDao
import com.github.kokorin.products.response.ResponseBuilder

sealed class Command {
    abstract fun query(productsReadDao: ProductsReadDao): String

    companion object {
        fun makeCommand(command: String): Command {
            return when (command) {
                "max" -> MaxCommand
                "min" -> MinCommand
                "sum" -> SumCommand
                "count" -> CountCommand
                else -> UnknownCommand(command)
            }
        }

        internal object MaxCommand : Command() {
            override fun query(productsReadDao: ProductsReadDao): String {
                val responseBuilder = ResponseBuilder("<h1>Product with max price: </h1>")
                val maxPriceProduct = productsReadDao.getMaxPriceProduct()
                maxPriceProduct?.let {
                    responseBuilder.addResponseElement("${it.name}\t${it.price}")
                }
                return responseBuilder.buildAnswer()
            }
        }

        internal object MinCommand : Command() {
            override fun query(productsReadDao: ProductsReadDao): String {
                val responseBuilder = ResponseBuilder("<h1>Product with min price: </h1>")
                val maxPriceProduct = productsReadDao.getMinPriceProduct()
                maxPriceProduct?.let {
                    responseBuilder.addResponseElement("${it.name}\t${it.price}")
                }
                return responseBuilder.buildAnswer()
            }
        }

        internal object SumCommand : Command() {
            override fun query(productsReadDao: ProductsReadDao): String {
                val responseBuilder = ResponseBuilder("Summary price: ")
                val sumPrices = productsReadDao.getPricesSum()
                responseBuilder.addResponseElement(sumPrices.toString())
                return responseBuilder.buildAnswer()
            }
        }

        internal object CountCommand : Command() {
            override fun query(productsReadDao: ProductsReadDao): String {
                val responseBuilder = ResponseBuilder("Number of products: ")
                val count = productsReadDao.getCount()
                responseBuilder.addResponseElement(count.toString())
                return responseBuilder.buildAnswer()
            }
        }

        internal class UnknownCommand(private val command: String) : Command() {
            override fun query(productsReadDao: ProductsReadDao): String {
                return "Unknown command: $command"
            }
        }
    }
}
