CREATE SCHEMA IF NOT EXISTS app_schema;

DROP TABLE IF EXISTS app_schema.books;

create table app_schema.books
(
id bigint not null primary key,
name varchar(255),
author varchar(255),
count_page int,
category_id bigint
);

DROP TABLE IF EXISTS app_schema.categories;

INSERT INTO books(id, name, author, count_page, category_id) VALUES(1, 'testName_1', 'testAuthor_1', 100, 1);
--INSERT INTO books(id, name, author, count_page, category_id) VALUES(2, 'testName_2', 'testAuthor_2', 100, 2);
--INSERT INTO books(id, name, author, count_page, category_id) VALUES(3, 'testName_3', 'testAuthor_3', 100, 3);

--DROP TABLE IF EXISTS app_schema.categories;
--
create table app_schema.categories
(
id bigint not null primary key,
name varchar(255)
);

INSERT INTO categories(id, name) VALUES(1, 'testCategoryName_1');
--
--INSERT INTO categories(id, name, books) VALUES(1, 'testCategoryName_1', '[{"id": 1, "name": "testName_1", "author": "testAuthor_1", "count_page": 100, "category_id": 1}]');
--INSERT INTO categories(id, name) VALUES(2, 'testCategoryName_2');