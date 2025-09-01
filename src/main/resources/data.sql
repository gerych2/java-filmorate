-- Вставка рейтингов MPA
MERGE INTO mpa (mpa_id, name) VALUES (1, 'G');
MERGE INTO mpa (mpa_id, name) VALUES (2, 'PG');
MERGE INTO mpa (mpa_id, name) VALUES (3, 'PG-13');
MERGE INTO mpa (mpa_id, name) VALUES (4, 'R');
MERGE INTO mpa (mpa_id, name) VALUES (5, 'NC-17');

-- Вставка жанров
MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) VALUES (6, 'Боевик');

-- Вставка тестового пользователя
MERGE INTO users (user_id, email, login, name, birthday) VALUES (1, 'test@test.com', 'testuser', 'Test User', '1990-01-01');
