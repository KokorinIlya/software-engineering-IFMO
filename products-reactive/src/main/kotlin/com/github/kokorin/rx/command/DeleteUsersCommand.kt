package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import rx.Observable

object DeleteUsersCommand : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return dao.deleteAllUsers().map { it.toString() }
    }
}
