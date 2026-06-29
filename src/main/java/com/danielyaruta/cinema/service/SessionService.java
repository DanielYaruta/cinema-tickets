package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.HallDao;
import com.danielyaruta.cinema.dao.MovieDao;
import com.danielyaruta.cinema.dao.SessionDao;
import com.danielyaruta.cinema.model.Session;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// SRP: содержит только бизнес-логику для сеансов — проверка существования фильма и
// зала перед созданием сеанса, без SQL и без I/O.
//
// DIP: зависит от трёх интерфейсов (SessionDao, MovieDao, HallDao).
// Конкретные реализации передаются снаружи через конструктор.
public class SessionService {

    private final SessionDao sessionDao;
    private final MovieDao movieDao;
    private final HallDao hallDao;

    public SessionService(SessionDao sessionDao, MovieDao movieDao, HallDao hallDao) {
        this.sessionDao = sessionDao;
        this.movieDao = movieDao;
        this.hallDao = hallDao;
    }

    public Session addSession(long movieId, long hallId, LocalDateTime startTime, BigDecimal price) {
        movieDao.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found: id=" + movieId));
        hallDao.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Hall not found: id=" + hallId));

        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be zero or positive");
        }

        return sessionDao.save(new Session(0, movieId, hallId, startTime, price));
    }

    public Optional<Session> getSession(long id) {
        return sessionDao.findById(id);
    }

    public List<Session> getAllSessions() {
        return sessionDao.findAll();
    }
}
