package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import com.github.kokorin.rx.model.User
import rx.Observable

class AddUserCommand(private val user: User) : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return dao.addUser(user).map { it.toString() }
    }
}
