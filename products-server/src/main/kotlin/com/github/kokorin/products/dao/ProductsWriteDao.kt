package com.github.kokorin.products.dao

import com.github.kokorin.products.model.Product

interface ProductsWriteDao {
    fun deleteAllProducts()

    fun addProduct(product: Product)

    fun createProductsTable()
}
