package com.github.kokorin.todo.sql

import com.github.kokorin.todo.utils.readFileAsString
import org.springframework.stereotype.Component

interface SqlHolder {
    val createTodoListTable: String
    val createTodoTable: String
    val getAll: String
    val insertNewTodoList: String
    val insertNewTodo: String
    val deleteTodoList: String
    val markAsDone: String
}

@Component
class SqlHolderImpl(pathToSql: PathToSql) : SqlHolder {
    private val path = pathToSql.path
    override val createTodoListTable = readFileAsString(path.resolve("create_todo_list_table.sql"))
    override val insertNewTodoList = readFileAsString(path.resolve("insert_todo_list.sql"))
    override val createTodoTable = readFileAsString(path.resolve("create_todo_table.sql"))
    override val getAll = readFileAsString(path.resolve("get_all_todo.sql"))
    override val insertNewTodo = readFileAsString(path.resolve("insert_todo.sql"))
    override val deleteTodoList = readFileAsString(path.resolve("delete_todo_list.sql"))
    override val markAsDone = readFileAsString(path.resolve("mark_as_done.sql"))
}
