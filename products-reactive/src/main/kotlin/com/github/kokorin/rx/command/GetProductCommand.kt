package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import rx.Observable

class GetProductCommand(private val id: Long) : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return dao
            .getProductById(id)
            .map { it.toString() }
            .singleOrDefault("Product with id=$id not found")
    }
}
