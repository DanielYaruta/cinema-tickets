package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Hall;

import java.util.*;

public class InMemoryHallDao implements HallDao {

    private final Map<Long, Hall> store = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public Hall save(Hall hall) {
        hall.setId(nextId++);
        store.put(hall.getId(), hall);
        return hall;
    }

    @Override
    public Optional<Hall> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Hall> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(long id) {
        store.remove(id);
    }
}
