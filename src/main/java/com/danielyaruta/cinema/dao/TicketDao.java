package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.model.Ticket;
import com.danielyaruta.cinema.model.TicketStatus;

import java.util.List;
import java.util.Optional;

// ISP: Ticket-специфичный контракт — включает методы поиска по сеансу и обновления
// статуса, которых нет в других DAO-интерфейсах.
public interface TicketDao {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(long id);
    List<Ticket> findAll();
    List<Ticket> findBySessionId(long sessionId);
    // Возвращает активный (не CANCELLED) билет на данное место в сеансе
    Optional<Ticket> findActiveBySessionIdAndSeatNumber(long sessionId, int seatNumber);
    void updateStatus(long id, TicketStatus status);
    void delete(long id);
}
