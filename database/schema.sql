CREATE TABLE IF NOT EXISTS friendship (
    user_id INT REFERENCES users(user_id),
    friend_id INT REFERENCES users(user_id),
    status BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id)
    );

CREATE TABLE IF NOT EXISTS mpa_rating (
    rating_id INT PRIMARY KEY,
    VARCHAR(10) NOT NULL
    );

CREATE TABLE IF NOT EXISTS films (
    film_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration INT NOT NULL CHECK (duration > 0),
    rating_id INT REFERENCES mpa_rating(rating_id)
    );

CREATE TABLE IF NOT EXISTS genre (
    genre_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT REFERENCES films(film_id),
    genre_id INT REFERENCES genre(genre_id),
    PRIMARY KEY (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INT REFERENCES films(film_id),
    user_id INT REFERENCES users(user_id),
    PRIMARY KEY (film_id, user_id)
    );
