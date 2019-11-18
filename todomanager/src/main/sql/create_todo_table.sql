CREATE TABLE IF NOT EXISTS Todo
(
    todo_id          INTEGER PRIMARY KEY,
    todo_name        TEXT,
    todo_description TEXT,
    todo_status      INTEGER,
    list_id          INTEGER,
    FOREIGN KEY (list_id) REFERENCES TodoList (list_id) ON DELETE CASCADE
);
