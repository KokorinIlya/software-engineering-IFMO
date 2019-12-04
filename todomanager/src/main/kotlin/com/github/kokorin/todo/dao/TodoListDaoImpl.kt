package com.github.kokorin.todo.dao

import com.github.kokorin.todo.connection.ConnectionProvider
import com.github.kokorin.todo.model.*
import com.github.kokorin.todo.sql.SqlHolder
import org.springframework.stereotype.Component


@Component
class TodoListDaoImpl(
    private val sqlHolder: SqlHolder,
    private val connectionProvider: ConnectionProvider
) : TodoListDao {
    init {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(sqlHolder.createTodoListTable)
                statement.executeUpdate(sqlHolder.createTodoTable)
                statement.executeUpdate(sqlHolder.createNextIdsTable)
                statement.executeUpdate(sqlHolder.fillNextIdsTable)
            }
        }
    }

    override fun getAllTodos(): List<Pair<TodoList, List<Todo>>> {
        val aggregator = mutableMapOf<TodoList, MutableList<Todo>>()
        connectionProvider.getConnection().use { connection ->
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
                            val todoStatus = if (resultSet.getBoolean("is_done")) {
                                TodoStatus.DONE
                            } else {
                                TodoStatus.TODO
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
        return aggregator.toMap().mapValues {
            it.value.toList().sortedBy { todo -> todo.id }
        }.toList().sortedBy { it.first.id }
    }

    override fun addTodo(name: String, description: String, listId: Long) {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(sqlHolder.insertNewTodo).use { statement ->
                statement.setString(1, name)
                statement.setString(2, description)
                statement.setInt(3, listId.toInt())
                statement.executeQuery()
            }
        }
    }

    override fun removeTodoList(todoListId: Long) {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(sqlHolder.deleteTodoList).use { statement ->
                statement.setLong(1, todoListId)
                statement.executeUpdate()
            }
        }
    }

    override fun markTodoAsDone(todoId: Long) {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(sqlHolder.markAsDone).use { statement ->
                statement.setLong(1, todoId)
                statement.executeUpdate()
            }
        }
    }

    override fun addTodoList(todoListName: String, todoListDescription: String) {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(sqlHolder.insertNewTodoList).use { statement ->
                statement.setString(1, todoListName)
                statement.setString(2, todoListDescription)
                statement.executeQuery()
            }
        }
    }
}
