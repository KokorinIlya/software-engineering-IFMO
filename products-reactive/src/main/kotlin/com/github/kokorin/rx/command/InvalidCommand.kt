package com.github.kokorin.rx.command

import com.github.kokorin.rx.dao.ReactiveDao
import rx.Observable

class InvalidCommand(private val reason: Throwable) : Command {
    override fun process(dao: ReactiveDao): Observable<String> {
        return Observable.just("Invalid command: $reason")
    }
}
