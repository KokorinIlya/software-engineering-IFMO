package com.github.kokorin.todo.dao

import com.github.kokorin.todo.model.Todo
import com.github.kokorin.todo.model.TodoInput
import java.util.*

interface TodoListDao {
    fun findTodoList(): List<Todo>

    fun addTodo(entity: TodoInput)

    fun removeTodo(entityId: Long)

    fun markTodo(entityId: Long)
}
