INSERT INTO NextIds(table_name, next_id)
VALUES ('TodoList', 1),
       ('Todo', 1)
ON CONFLICT DO NOTHING;
