CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE NOT NULL
    );

CREATE TABLE IF NOT EXISTS friendship (
    user_id INT REFERENCES users(id),
    friend_id INT REFERENCES users(id),
    status BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id)
    );

CREATE TABLE IF NOT EXISTS mpa_rating (
    id INT PRIMARY KEY,
    name VARCHAR(10) NOT NULL
    );

CREATE TABLE IF NOT EXISTS films (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration INT NOT NULL CHECK (duration > 0),
    rating_id INT REFERENCES mpa_rating(id)
    );

CREATE TABLE IF NOT EXISTS genre (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT REFERENCES films(id),
    genre_id INT REFERENCES genre(id),
    PRIMARY KEY (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INT REFERENCES films(id),
    user_id INT REFERENCES users(id),
    PRIMARY KEY (film_id, user_id)
    );
