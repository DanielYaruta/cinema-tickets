package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Movie;

import java.util.*;

// LSP в действии: InMemoryMovieDao полностью реализует MovieDao и подставляется
// вместо MovieDaoJdbc в тестах — без БД, без изменений в сервисах.
public class InMemoryMovieDao implements MovieDao {

    private final Map<Long, Movie> store = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public Movie save(Movie movie) {
        movie.setId(nextId++);
        store.put(movie.getId(), movie);
        return movie;
    }

    @Override
    public Optional<Movie> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(long id) {
        store.remove(id);
    }
}
