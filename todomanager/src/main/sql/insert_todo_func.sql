CREATE OR REPLACE FUNCTION insert_new_todo(todo_name_arg VARCHAR(100),
                                           todo_description_arg VARCHAR(500),
                                           list_id_arg INT) RETURNS VOID AS
$$
DECLARE
    next_todo_id INT;
BEGIN
    next_todo_id := (SELECT NextIds.next_id
                     FROM NextIds
                     WHERE NextIds.table_name = 'Todo');

    INSERT INTO Todo(todo_id, todo_name, todo_description, is_done, list_id)
    VALUES (next_todo_id, todo_name_arg, todo_description_arg, FALSE, list_id_arg);

    UPDATE NextIds
    SET next_id = next_id + 1
    WHERE NextIds.table_name = 'Todo';
END;
$$
    LANGUAGE plpgsql;

