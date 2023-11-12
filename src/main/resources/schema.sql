CREATE TABLE Groups
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) UNIQUE
);

CREATE TABLE Users
(
    chat_id    BIGINT PRIMARY KEY,
    name       VARCHAR(255),
    surname    VARCHAR(255),
    group_name VARCHAR(50) REFERENCES Groups (name)
);

CREATE TABLE Disciplines
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) UNIQUE
);

CREATE TABLE Tests
(
    test_id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    test_name       VARCHAR(100),
    discipline_name VARCHAR(100) REFERENCES Disciplines (name)
);

CREATE TABLE Discipline_Groups
(
    discipline_group_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    discipline_id       BIGINT REFERENCES Disciplines (id),
    group_name    VARCHAR(50) REFERENCES Groups (name)
);

CREATE TABLE Questions
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    question_text  TEXT NOT NULL,
    correct_answer TEXT NOT NULL
);

CREATE TABLE Test_Questions
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    test_id     BIGINT REFERENCES tests (test_id),
    question_id BIGINT REFERENCES questions (id)
);
CREATE TABLE Results
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT REFERENCES Users (chat_id),
    test_id     BIGINT REFERENCES Tests (test_id),
    question_id BIGINT REFERENCES Questions (id),
    answer      TEXT NOT NULL
);



