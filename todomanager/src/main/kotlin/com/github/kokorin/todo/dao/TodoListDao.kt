package com.github.kokorin.todo.dao

import com.github.kokorin.todo.model.Todo
import com.github.kokorin.todo.model.TodoList

interface TodoListDao {
    fun getAllTodos(): List<Pair<TodoList, List<Todo>>>

    fun addTodo(name: String, description: String, listId: Long)

    fun addTodoList(todoListName: String, todoListDescription: String)

    fun removeTodoList(todoListId: Long)

    fun markTodoAsDone(todoId: Long)
}
