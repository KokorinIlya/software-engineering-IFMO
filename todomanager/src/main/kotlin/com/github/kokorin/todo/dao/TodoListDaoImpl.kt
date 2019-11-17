package com.github.kokorin.todo.dao

import com.github.kokorin.todo.model.Todo
import com.github.kokorin.todo.model.TodoInput
import com.github.kokorin.todo.model.TodoStatus
import com.github.kokorin.todo.sql.SqlHolder
import org.springframework.stereotype.Component
import java.sql.DriverManager

@Component
class TodoListDaoImpl(private val sqlHolder: SqlHolder) : TodoListDao {
    private val connectionString = "jdbc:sqlite:todo.db"

    init {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(sqlHolder.createTableIfExists)
            }
        }
    }

    override fun findTodoList(): List<Todo> {
        val result = mutableListOf<Todo>()
        DriverManager.getConnection(connectionString).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(sqlHolder.getAll).use { resultSet ->
                    while (resultSet.next()) {
                        val id = resultSet.getLong("todo_id")
                        val name = resultSet.getString("todo_name")
                        val description = resultSet.getString("todo_description")
                        val status = when (resultSet.getInt("todo_status")) {
                            0 -> TodoStatus.TODO
                            1 -> TodoStatus.DONE
                            else -> throw IllegalArgumentException("Illegal status")
                        }
                        result.add(
                            Todo(
                                id = id,
                                name = name,
                                description = description,
                                status = status
                            )
                        )
                    }
                }
            }
        }
        return result.toList()
    }

    override fun addTodo(entity: TodoInput) {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.prepareStatement(sqlHolder.insertNew).use { statement ->
                statement.setString(1, entity.name)
                statement.setString(2, entity.description)
                statement.setInt(3, 0)
                statement.executeUpdate()
            }
        }
    }

    override fun removeTodo(entityId: Long) {
        processSingle(entityId, sqlHolder.delete)
    }

    override fun markTodo(entityId: Long) {
        processSingle(entityId, sqlHolder.markAsDone)
    }

    private fun processSingle(todoId: Long, sql: String) {
        DriverManager.getConnection(connectionString).use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, todoId)
                statement.executeUpdate()
            }
        }
    }
}
