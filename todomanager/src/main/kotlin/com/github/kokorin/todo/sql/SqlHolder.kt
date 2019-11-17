package com.github.kokorin.todo.sql

import com.github.kokorin.todo.utils.readFileAsString
import org.springframework.stereotype.Component
import java.nio.file.Path

interface SqlHolder {
    val createTableIfExists: String
    val getAll: String
    val insertNew: String
    val delete: String
    val markAsDone: String
}

@Component
class SqlHolderImpl(pathToSql: PathToSql) : SqlHolder {
    private val path = pathToSql.path
    override val createTableIfExists = readFileAsString(path.resolve("create_table.sql"))
    override val getAll = readFileAsString(path.resolve("get_all_todo.sql"))
    override val insertNew = readFileAsString(path.resolve("insert_todo.sql"))
    override val delete = readFileAsString(path.resolve("delete_todo.sql"))
    override val markAsDone = readFileAsString(path.resolve("mark_as_done.sql"))
}
