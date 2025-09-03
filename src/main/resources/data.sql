-- Вставка рейтингов MPA
MERGE INTO mpa_rating (id, name) KEY(id) VALUES (1, 'G');
MERGE INTO mpa_rating (id, name) KEY(id) VALUES (2, 'PG');
MERGE INTO mpa_rating (id, name) KEY(id) VALUES (3, 'PG-13');
MERGE INTO mpa_rating (id, name) KEY(id) VALUES (4, 'R');
MERGE INTO mpa_rating (id, name) KEY(id) VALUES (5, 'NC-17');

-- Вставка жанров
MERGE INTO genre (id, name) KEY(id) VALUES (1, 'Комедия');
MERGE INTO genre (id, name) KEY(id) VALUES (2, 'Драма');
MERGE INTO genre (id, name) KEY(id) VALUES (3, 'Мультфильм');
MERGE INTO genre (id, name) KEY(id) VALUES (4, 'Триллер');
MERGE INTO genre (id, name) KEY(id) VALUES (5, 'Документальный');
MERGE INTO genre (id, name) KEY(id) VALUES (6, 'Боевик');

-- Вставка тестового пользователя (id генерируется автоматически)
INSERT INTO users (email, login, name, birthday)
VALUES ('test@test.com', 'testuser', 'Test User', DATE '1990-01-01');

-- Вставка тестового фильма (id генерируется автоматически)
INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES ('Тестовый фильм', 'Описание тестового фильма', DATE '2000-01-01', 120, 1);

-- После вставки пользователей и фильмов можно добавить связи
-- Предположим, что id пользователя и фильма — 1 (в тестовой in-memory БД, при первой вставке они будут 1)
MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id)
    VALUES (1, 1);

MERGE INTO film_likes (film_id, user_id) KEY(film_id, user_id)
    VALUES (1, 1);

MERGE INTO friendship (user_id, friend_id) KEY(user_id, friend_id)
    VALUES (1, 1);
