DELETE
FROM film_genre;
DELETE
FROM likes;
DELETE
FROM films;
DELETE
FROM genres;
DELETE
FROM users;
DELETE
FROM mpa;

INSERT INTO mpa (mpa_id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO users (email, login, name, birthday)
VALUES ('alex@example.com', 'ss', 'ss ss', '1997-01-01'),
       ('lexa@example.com', 'll', 'll ll', '1998-02-02'),
       ('xaxa@example.com', 'xx', 'xx xx', '1999-03-03');

INSERT INTO genres (genre_id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Боевик'),
       (4, 'Триллер'),
       (5, 'Хоррор');

INSERT INTO films (film_id, name, description, duration, release_date, mpa_id)
VALUES (1, 'Inception', 'bbbbbbb', 148, '2000-02-01', 4),
       (2, 'Titanic', 'aaaaaaa', 195, '2000-03-01', 3),
       (3, 'Slovopacana', 'sssss', 136, '2000-04-01', 5);

INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 1),
       (1, 3),
       (2, 2),
       (3, 3),
       (3, 4);

INSERT INTO likes (film_id, user_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 3);