package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Session;

import java.util.*;

public class InMemorySessionDao implements SessionDao {

    private final Map<Long, Session> store = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public Session save(Session session) {
        session.setId(nextId++);
        store.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<Session> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Session> findAll() {
        List<Session> list = new ArrayList<>(store.values());
        list.sort(Comparator.comparing(Session::getStartTime));
        return list;
    }

    @Override
    public void delete(long id) {
        store.remove(id);
    }
}
