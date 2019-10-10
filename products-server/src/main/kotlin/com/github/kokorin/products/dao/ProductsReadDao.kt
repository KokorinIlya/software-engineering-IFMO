package com.github.kokorin.products.dao

import com.github.kokorin.products.model.Product

interface ProductsReadDao {
    fun getAllProducts(): List<Product>

    fun getMinPriceProduct(): Product?

    fun getMaxPriceProduct(): Product?

    fun getCount(): Int

    fun getPricesSum(): Int
}
