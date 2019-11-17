package com.github.kokorin.todo.controller

import com.github.kokorin.todo.dao.TodoListDao
import com.github.kokorin.todo.model.TodoInput
import com.github.kokorin.todo.model.TodoOutput
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class TodoListController(private val dao: TodoListDao) {

    @GetMapping("/all-todos")
    fun getTodoList(model: Model): String {
        model.addAttribute("all_todos", dao.findTodoList())
        model.addAttribute("todo", TodoOutput())
        return "all_todos"
    }

    @PostMapping("/add-todo")
    fun addTodo(@ModelAttribute("todo") todo: TodoInput): String {
        dao.addTodo(todo)
        return "redirect:/all-todos"
    }

    @PostMapping("/delete-todo")
    fun removeTodoList(@RequestParam("id") todoId: Long): String {
        dao.removeTodo(todoId)
        return "redirect:/all-todos"
    }

    @PostMapping("/mark-todo-as-done")
    fun markTodo(@RequestParam("id") id: Long): String {
        dao.markTodo(id)
        return "redirect:/all-todos"
    }
}
