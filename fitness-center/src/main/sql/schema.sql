CREATE TYPE Entity AS ENUM ('USER');

CREATE TYPE GateEventType AS ENUM ('ENTER', 'EXIT');

CREATE TABLE MaxIds
(
    entity Entity NOT NULL PRIMARY KEY,
    max_id INT    NOT NULL
);

INSERT INTO MaxIds (entity, max_id)
VALUES ('USER', 0);

CREATE TABLE Events
(
    user_id       INT NOT NULL,
    user_event_id INT NOT NULL,
    PRIMARY KEY (user_id, user_event_id)
);

CREATE TABLE SubscriptionRenewalsEvents
(
    user_id       INT       NOT NULL,
    user_event_id INT       NOT NULL,
    end_date      TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, user_event_id),
    FOREIGN KEY (user_id, user_event_id) REFERENCES Events (user_id, user_event_id)
);

CREATE TABLE GateEvents
(
    user_id         INT           NOT NULL,
    user_event_id   INT           NOT NULL,
    gate_event_type GateEventType NOT NULL,
    event_timestamp TIMESTAMP     NOT NULL,
    PRIMARY KEY (user_id, user_event_id),
    FOREIGN KEY (user_id, user_event_id) REFERENCES Events (user_id, user_event_id)
);
