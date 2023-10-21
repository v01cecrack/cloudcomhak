CREATE TABLE Groups (
    name VARCHAR(50) PRIMARY KEY
);

CREATE TABLE Users (
    chat_id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    group_name VARCHAR(50) REFERENCES Groups(name)
);

CREATE TABLE Tests (
    test_id BIGINT PRIMARY KEY,
    test_name VARCHAR(100)
);

CREATE TABLE TestGroups (
test_id INT REFERENCES TESTS(test_id),
group_name VARCHAR(50) REFERENCES Groups(name)
);


CREATE TABLE Users (
                       chat_id BIGINT PRIMARY KEY,
                       name VARCHAR(255),
                       surname VARCHAR(255),
                       group_name VARCHAR(50)
);

