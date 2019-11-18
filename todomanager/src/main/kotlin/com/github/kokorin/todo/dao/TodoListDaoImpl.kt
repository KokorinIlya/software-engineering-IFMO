package com.github.kokorin.todo.dao

import com.github.kokorin.todo.model.*
import com.github.kokorin.todo.sql.SqlHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.sql.DriverManager

@Component
class TodoListDaoImpl(private val sqlHolder: SqlHolder) : TodoListDao {
    private val connectionString = "jdbc:sqlite:todo"
    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(sqlHolder.createTodoListTable)
                statement.executeUpdate(sqlHolder.createTodoTable)
            }
        }
    }

    override fun getAllTodos(): Map<TodoList, List<Todo>> {
        val aggregator = mutableMapOf<TodoList, MutableList<Todo>>()
        DriverManager.getConnection(connectionString).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(sqlHolder.getAll).use { resultSet ->
                    while (resultSet.next()) {
                        val listId = resultSet.getLong("list_id")
                        val listName = resultSet.getString("list_name")
                        val listDescription = resultSet.getString("list_description")

                        val todoList = TodoList(
                            id = listId,
                            name = listName,
                            description = listDescription
                        )

                        if (resultSet.getObject("todo_id") != null) {
                            val todoId = resultSet.getLong("todo_id")
                            val todoName = resultSet.getString("todo_name")
                            val todoDescription = resultSet.getString("todo_description")
                            val todoStatus = when (resultSet.getInt("todo_status")) {
                                0 -> TodoStatus.TODO
                                else -> TodoStatus.DONE
                            }

                            val todo = Todo(
                                id = todoId,
                                name = todoName,
                                description = todoDescription,
                                status = todoStatus
                            )
                            if (!aggregator.containsKey(todoList)) {
                                aggregator[todoList] = mutableListOf(todo)
                            } else {
                                aggregator[todoList]!!.add(todo)
                            }
                        } else {
                            aggregator[todoList] = mutableListOf()
                        }
                    }
                }
            }
        }
        val result = aggregator.toMap().mapValues {
            it.value.toList()
        }
        logger.info(result.toString())
        return result
    }

    override fun addTodo(todo: TodoInput) {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.prepareStatement(sqlHolder.insertNewTodo).use { statement ->
                statement.setString(1, todo.name)
                statement.setString(2, todo.description)
                statement.setInt(3, 0)
                statement.setLong(4, todo.listId)
                statement.executeUpdate()
            }
        }
    }

    override fun removeTodoList(todoListId: Long) {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.prepareStatement(sqlHolder.deleteTodoList).use { statement ->
                statement.setLong(1, todoListId)
                statement.executeUpdate()
            }
        }
    }

    override fun markTodoAsDone(todoId: Long) {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.prepareStatement(sqlHolder.markAsDone).use { statement ->
                statement.setLong(1, todoId)
                statement.executeUpdate()
            }
        }
    }

    override fun addTodoList(todoList: TodoListInput) {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.prepareStatement(sqlHolder.insertNewTodoList).use { statement ->
                statement.setString(1, todoList.name)
                statement.setString(2, todoList.description)
                statement.executeUpdate()
            }
        }
    }
}
