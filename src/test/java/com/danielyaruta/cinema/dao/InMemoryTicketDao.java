package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Ticket;
import com.danielyaruta.cinema.model.TicketStatus;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTicketDao implements TicketDao {

    private final Map<Long, Ticket> store = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public Ticket save(Ticket ticket) {
        ticket.setId(nextId++);
        store.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Ticket> findBySessionId(long sessionId) {
        return store.values().stream()
                .filter(t -> t.getSessionId() == sessionId)
                .sorted(Comparator.comparingInt(Ticket::getSeatNumber))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Ticket> findActiveBySessionIdAndSeatNumber(long sessionId, int seatNumber) {
        return store.values().stream()
                .filter(t -> t.getSessionId() == sessionId
                          && t.getSeatNumber() == seatNumber
                          && t.getStatus() != TicketStatus.CANCELLED)
                .findFirst();
    }

    @Override
    public void updateStatus(long id, TicketStatus status) {
        Optional.ofNullable(store.get(id)).ifPresent(t -> t.setStatus(status));
    }

    @Override
    public void delete(long id) {
        store.remove(id);
    }
}
