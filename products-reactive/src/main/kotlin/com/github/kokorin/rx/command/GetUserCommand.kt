package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import rx.Observable

class GetUserCommand(private val id: Long) : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return dao
            .getUserById(id)
            .map { it.toString() }
            .singleOrDefault("User with id=$id not found")
    }
}
