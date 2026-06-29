package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Hall;

import java.util.List;
import java.util.Optional;

// ISP: интерфейс отвечает только за операции с Hall.
public interface HallDao {
    Hall save(Hall hall);
    Optional<Hall> findById(long id);
    List<Hall> findAll();
    void delete(long id);
}
