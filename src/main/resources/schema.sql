CREATE TABLE IF NOT EXISTS users (
    user_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(100) NOT NULL UNIQUE,
    login    VARCHAR(100) NOT NULL UNIQUE,
    name     VARCHAR(100),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
    film_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(1000),
    release_date DATE,
    duration     INTEGER,
    mpa_id       BIGINT REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends (
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    user_id     BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    film_id     BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    is_positive BOOLEAN NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    useful      INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS directors (
    director_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors (
    film_id     BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    director_id BIGINT NOT NULL REFERENCES directors(director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS events (
    event_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    operation  VARCHAR(50) NOT NULL,
    entity_id  BIGINT,
    timestamp  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS review_likes (
    review_id BIGINT NOT NULL REFERENCES reviews(review_id) ON DELETE CASCADE,
    user_id   BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    is_positive BOOLEAN NOT NULL,
    PRIMARY KEY (review_id, user_id)
);

