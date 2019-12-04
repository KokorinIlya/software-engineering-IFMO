SELECT TodoList.list_id,
       TodoList.list_name,
       TodoList.list_description,
       Todo.todo_id,
       Todo.todo_name,
       Todo.todo_description,
       Todo.is_done
FROM TodoList
         LEFT OUTER JOIN Todo ON TodoList.list_id = Todo.list_id;