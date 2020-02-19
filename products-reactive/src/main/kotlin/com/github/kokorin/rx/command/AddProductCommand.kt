package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import com.github.kokorin.rx.model.Product
import rx.Observable

class AddProductCommand(private val product: Product) : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return dao.addProduct(product)
            .map { it.toString() }
    }
}
