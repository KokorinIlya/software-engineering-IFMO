package com.github.kokorin.todo.model

data class TodoOutput(
    var id: Long = -1,
    var name: String = "",
    var description: String = "",
    var status: TodoStatus = TodoStatus.TODO,
    var listId: Int = -1
)
