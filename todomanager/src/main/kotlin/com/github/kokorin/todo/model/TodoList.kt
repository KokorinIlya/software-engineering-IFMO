package com.github.kokorin.todo.model

data class TodoList(
    val id: Long,
    val name: String,
    val description: String
) {
    init {
        require(id >= 0) { "Identifier should be non-null" }
    }
}
