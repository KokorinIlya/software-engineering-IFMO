package com.github.kokorin.todo.dao

import com.github.kokorin.todo.model.Todo
import com.github.kokorin.todo.model.TodoInput
import com.github.kokorin.todo.model.TodoList
import com.github.kokorin.todo.model.TodoListInput

interface TodoListDao {
    fun getAllTodos(): List<Pair<TodoList, List<Todo>>>

    fun addTodo(todo: TodoInput)

    fun addTodoList(todoList: TodoListInput)

    fun removeTodoList(todoListId: Long)

    fun markTodoAsDone(todoId: Long)
}
