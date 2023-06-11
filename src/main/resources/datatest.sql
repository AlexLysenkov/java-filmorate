INSERT INTO PUBLIC.users (email, login, name, birthday)
VALUES('user1test@gmail.com', 'testLogin1', 'user1', '2003-12-31'),
      ('user2test@mail.ru', 'testLogin2', 'user2', '2000-11-08'),
      ('user3test@rambler.ru', 'testLogin3', 'user3', '1998-04-23'),
      ('user4test@test.ru', 'testLogin4', 'user4', '1995-07-08'),
      ('user5test@yandex.ru', 'testLogin5', 'user5', '1992-01-23'),
      ('user6test@test.ru', 'testLogin6', 'user6', '1993-04-14'),
      ('user7test@test.ru', 'testLogin7', 'user7', '2005-09-04');

INSERT INTO PUBLIC.friends (user1_id, user2_id, status)
VALUES(1, 2, 'FRIEND'),
      (2, 3, 'FRIEND'),
      (3, 5, 'FRIEND'),
      (1, 4, 'NOT_APPROVED'),
      (4, 7, 'DECLINED'),
      (7, 2, 'DECLINED');

INSERT INTO PUBLIC.mpa (name)
VALUES('G'),
      ('PG'),
      ('PG-13'),
      ('R'),
      ('NC-17');

INSERT INTO PUBLIC.genres (name)
VALUES('Комедия'),
      ('Драма'),
      ('Мультфильм'),
      ('Триллер'),
      ('Документальный'),
      ('Боевик');

INSERT INTO PUBLIC.films (name, description, release_date, duration, mpa_id)
VALUES('film1', 'film1Description', '2020-10-15', 245, 2),
      ('film2', 'film2Description', '2020-06-11', 546, 5),
      ('film3', 'film3Description', '2018-05-02', 624, 5);

INSERT INTO PUBLIC.genres (name)
VALUES('фантастика'),
      ('комедия'),
      ('боевик'),
      ('драма'),
      ('мультфильм');

INSERT INTO PUBLIC.film_genre (film_id, genre_id)
VALUES(1, 6),
      (1, 7),
      (2, 2),
      (2, 4),
      (2, 5);

SELECT * FROM film_genre;

INSERT INTO PUBLIC.likes (film_id, user_id)
VALUES(1, 2),
      (2, 3),
      (2, 5);