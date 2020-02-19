package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import rx.Observable

class GetProductsForUserCommand(private val id: Long) : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return dao.getProductsForUser(id).map { it.toString() }
    }
}
