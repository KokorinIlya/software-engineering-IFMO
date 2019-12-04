package com.github.kokorin.todo.dao

import com.github.kokorin.todo.model.Todo
import com.github.kokorin.todo.model.TodoList

interface TodoListDao {
    fun getAllTodos(): List<Pair<TodoList, List<Todo>>>

    fun addTodo(name: String, description: String, listId: Int)

    fun addTodoList(todoListName: String, todoListDescription: String)

    fun removeTodoList(todoListId: Int)

    fun markTodoAsDone(todoId: Int)
}
