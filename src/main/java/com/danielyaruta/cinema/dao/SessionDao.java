package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Session;

import java.util.List;
import java.util.Optional;

// ISP: интерфейс отвечает только за операции с Session.
public interface SessionDao {
    Session save(Session session);
    Optional<Session> findById(long id);
    List<Session> findAll();
    void delete(long id);
}
