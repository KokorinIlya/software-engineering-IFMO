package com.github.kokorin.todo.model

data class TodoListOutput(
    var id: Long = -1,
    var name: String = "",
    var description: String = ""
)
