package com.github.kokorin.todo.model

data class TodoInput(var name: String = "имя", var description: String = "описание", var listId: Long = -1)
