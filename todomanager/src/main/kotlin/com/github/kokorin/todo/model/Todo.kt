package com.github.kokorin.todo.model

data class Todo(
    val id: Long,
    val name: String,
    val description: String,
    val status: TodoStatus
) {
    init {
        require(id >= 0) { "Identifier should be non-null" }
    }
}
