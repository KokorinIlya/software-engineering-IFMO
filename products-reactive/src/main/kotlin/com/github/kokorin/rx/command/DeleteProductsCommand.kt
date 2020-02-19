package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import rx.Observable

object DeleteProductsCommand : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return dao.deleteAllProducts().map { it.toString() }
    }
}
