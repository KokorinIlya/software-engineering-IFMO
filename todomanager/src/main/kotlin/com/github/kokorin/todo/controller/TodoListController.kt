package com.github.kokorin.todo.controller

import com.github.kokorin.todo.dao.TodoListDao
import com.github.kokorin.todo.model.TodoInput
import com.github.kokorin.todo.model.TodoListInput
import com.github.kokorin.todo.model.TodoListOutput
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
        model.addAttribute("all_todos", dao.getAllTodos())
        model.addAttribute("todo_list", TodoListOutput())
        model.addAttribute("todo", TodoOutput())
        return "all_todos"
    }

    @PostMapping("/add-todo")
    fun addTodo(@ModelAttribute("todo") todo: TodoInput): String {
        dao.addTodo(todo)
        return "redirect:/all-todos"
    }

    @PostMapping("/add-todo-list")
    fun addTodoList(@ModelAttribute("todo_list") todoList: TodoListInput): String {
        dao.addTodoList(todoList)
        return "redirect:/all-todos"
    }

    @PostMapping("/delete-todo-list")
    fun removeTodoList(@RequestParam("id") todoListId: Long): String {
        dao.removeTodoList(todoListId)
        return "redirect:/all-todos"
    }

    @PostMapping("/mark-todo-as-done")
    fun markTodo(@RequestParam("id") todoId: Long): String {
        dao.markTodoAsDone(todoId)
        return "redirect:/all-todos"
    }
}
