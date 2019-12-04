CREATE OR REPLACE FUNCTION insert_new_todo_list(list_name_arg VARCHAR(100),
                                                list_description_arg VARCHAR(500)) RETURNS VOID AS
$$
DECLARE
    next_todo_list_id INT;
BEGIN
    next_todo_list_id := (SELECT NextIds.next_id
                          FROM NextIds
                          WHERE NextIds.table_name = 'TodoList');

    INSERT INTO TodoList(list_id, list_name, list_description)
    VALUES (next_todo_list_id, list_name_arg, list_description_arg);

    UPDATE NextIds
    SET next_id = next_id + 1
    WHERE NextIds.table_name = 'TodoList';
END;
$$
    LANGUAGE plpgsql;
