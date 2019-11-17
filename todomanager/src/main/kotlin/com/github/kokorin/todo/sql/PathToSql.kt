package com.github.kokorin.todo.sql

import org.springframework.stereotype.Component
import java.nio.file.Path
import java.nio.file.Paths

interface PathToSql {
    val path: Path
}

@Component
object PathToSqlImpl : PathToSql {
    override val path: Path = Paths.get("src/main/sql")
}
