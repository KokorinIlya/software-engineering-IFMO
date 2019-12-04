CREATE TABLE IF NOT EXISTS Todo
(
    todo_id          INT PRIMARY KEY NOT NULL,
    todo_name        VARCHAR(100)    NOT NULL,
    todo_description VARCHAR(500)    NOT NULL,
    is_done          BOOLEAN         NOT NULL,
    list_id          INT             NOT NULL,
    FOREIGN KEY (list_id) REFERENCES TodoList (list_id) ON DELETE CASCADE
);
