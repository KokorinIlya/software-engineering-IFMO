package com.github.kokorin.todo.controller

import com.github.kokorin.todo.dao.TodoListDao
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
        model.addAttribute("all_todos", dao.getAllTodos())
        return "all_todos"
    }

    @PostMapping("/add-todo")
    fun addTodo(
        @RequestParam("new_todo_name") todoName: String,
        @RequestParam("new_todo_description") todoDescription: String,
        @RequestParam("new_todo_listId") todoListId: Int
    ): String {
        dao.addTodo(todoName, todoDescription, todoListId)
        return "redirect:/all-todos"
    }

    @PostMapping("/add-todo-list")
    fun addTodoList(
        @ModelAttribute("name") todoListName: String,
        @ModelAttribute("description") todoListDescription: String
    ): String {
        dao.addTodoList(todoListName, todoListDescription)
        return "redirect:/all-todos"
    }

    @PostMapping("/delete-todo-list")
    fun removeTodoList(@RequestParam("id") todoListId: Int): String {
        dao.removeTodoList(todoListId)
        return "redirect:/all-todos"
    }

    @PostMapping("/mark-todo-as-done")
    fun markTodo(@RequestParam("id") todoId: Int): String {
        dao.markTodoAsDone(todoId)
        return "redirect:/all-todos"
    }
}
