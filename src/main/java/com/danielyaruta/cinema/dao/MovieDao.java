package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Movie;

import java.util.List;
import java.util.Optional;

// ISP: отдельный интерфейс только для операций с Movie.
// Нет универсального «жирного» DAO<T> — каждая сущность имеет собственный контракт.
public interface MovieDao {
    Movie save(Movie movie);
    Optional<Movie> findById(long id);
    List<Movie> findAll();
    void delete(long id);
}
