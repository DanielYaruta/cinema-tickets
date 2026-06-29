package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.MovieDao;
import com.danielyaruta.cinema.model.Movie;

import java.util.List;
import java.util.Optional;

// SRP: содержит только бизнес-логику для фильмов — без SQL и без I/O.
//
// DIP: зависит от абстракции MovieDao (интерфейс), а не от конкретной реализации
// MovieDaoJdbc. Конкретный класс передаётся снаружи через конструктор.
public class MovieService {

    private final MovieDao movieDao;

    public MovieService(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    public Movie addMovie(String title, String genre, int durationMinutes) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be blank");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Movie title is too long (max 255 characters)");
        }
        if (genre != null && genre.length() > 100) {
            throw new IllegalArgumentException("Genre is too long (max 100 characters)");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number");
        }
        if (durationMinutes > 600) {
            throw new IllegalArgumentException("Duration seems unrealistic (max 600 minutes)");
        }
        return movieDao.save(new Movie(0, title.trim(), genre == null ? "" : genre.trim(), durationMinutes));
    }

    public Optional<Movie> getMovie(long id) {
        return movieDao.findById(id);
    }

    public List<Movie> getAllMovies() {
        return movieDao.findAll();
    }
}
