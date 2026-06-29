package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.InMemoryHallDao;
import com.danielyaruta.cinema.dao.InMemoryMovieDao;
import com.danielyaruta.cinema.dao.InMemorySessionDao;
import com.danielyaruta.cinema.model.Hall;
import com.danielyaruta.cinema.model.Movie;
import com.danielyaruta.cinema.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    private SessionService sessionService;
    private long movieId;
    private long hallId;

    @BeforeEach
    void setUp() {
        InMemoryMovieDao   movieDao   = new InMemoryMovieDao();
        InMemoryHallDao    hallDao    = new InMemoryHallDao();
        InMemorySessionDao sessionDao = new InMemorySessionDao();

        sessionService = new SessionService(sessionDao, movieDao, hallDao);

        Movie movie = movieDao.save(new Movie(0, "Inception", "Sci-Fi", 148));
        Hall  hall  = hallDao.save(new Hall(0, "Hall A", 100));
        movieId = movie.getId();
        hallId  = hall.getId();
    }

    // --- Успешные сценарии ---

    @Test
    void addSession_validData_returnsSessionWithGeneratedId() {
        LocalDateTime start = LocalDateTime.of(2025, 9, 1, 18, 0);
        Session session = sessionService.addSession(movieId, hallId, start, new BigDecimal("350.00"));

        assertTrue(session.getId() > 0);
        assertEquals(movieId, session.getMovieId());
        assertEquals(hallId, session.getHallId());
        assertEquals(start, session.getStartTime());
        assertEquals(new BigDecimal("350.00"), session.getPrice());
    }

    @Test
    void addSession_zeroPrice_isAllowed() {
        Session session = sessionService.addSession(movieId, hallId,
                LocalDateTime.now().plusDays(1), BigDecimal.ZERO);
        assertNotNull(session);
    }

    @Test
    void getAllSessions_sortedByStartTime() {
        LocalDateTime later  = LocalDateTime.of(2025, 9, 1, 20, 0);
        LocalDateTime earlier = LocalDateTime.of(2025, 9, 1, 10, 0);
        sessionService.addSession(movieId, hallId, later,   new BigDecimal("200"));
        sessionService.addSession(movieId, hallId, earlier, new BigDecimal("200"));

        List<Session> sessions = sessionService.getAllSessions();
        assertEquals(2, sessions.size());
        assertTrue(sessions.get(0).getStartTime().isBefore(sessions.get(1).getStartTime()));
    }

    // --- Валидация: несуществующие ссылки ---

    @Test
    void addSession_nonExistingMovie_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sessionService.addSession(999L, hallId,
                        LocalDateTime.now(), new BigDecimal("100")));
        assertTrue(ex.getMessage().contains("Movie not found"));
    }

    @Test
    void addSession_nonExistingHall_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sessionService.addSession(movieId, 999L,
                        LocalDateTime.now(), new BigDecimal("100")));
        assertTrue(ex.getMessage().contains("Hall not found"));
    }

    // --- Валидация: цена и время ---

    @Test
    void addSession_negativePrice_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sessionService.addSession(movieId, hallId,
                        LocalDateTime.now(), new BigDecimal("-1")));
        assertTrue(ex.getMessage().toLowerCase().contains("price"));
    }

    @Test
    void addSession_nullStartTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> sessionService.addSession(movieId, hallId, null, new BigDecimal("100")));
    }
}
