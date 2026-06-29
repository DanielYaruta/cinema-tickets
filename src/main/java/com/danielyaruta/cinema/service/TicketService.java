package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.HallDao;
import com.danielyaruta.cinema.dao.SessionDao;
import com.danielyaruta.cinema.dao.TicketDao;
import com.danielyaruta.cinema.model.Hall;
import com.danielyaruta.cinema.model.Session;
import com.danielyaruta.cinema.model.Ticket;
import com.danielyaruta.cinema.model.TicketStatus;

import java.util.List;
import java.util.Optional;

// SRP: содержит бизнес-логику управления билетами — проверку свободного места,
// валидацию номера места, изменение статусов. Без SQL и без I/O.
//
// DIP: зависит от интерфейсов TicketDao, SessionDao, HallDao.
// Конкретные реализации передаются через конструктор.
public class TicketService {

    private final TicketDao ticketDao;
    private final SessionDao sessionDao;
    private final HallDao hallDao;

    public TicketService(TicketDao ticketDao, SessionDao sessionDao, HallDao hallDao) {
        this.ticketDao = ticketDao;
        this.sessionDao = sessionDao;
        this.hallDao = hallDao;
    }

    public Ticket bookTicket(long sessionId, int seatNumber, String customerName) {
        Session session = sessionDao.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: id=" + sessionId));

        Hall hall = hallDao.findById(session.getHallId())
                .orElseThrow(() -> new IllegalStateException("Hall not found for session id=" + sessionId));

        if (seatNumber < 1 || seatNumber > hall.getCapacity()) {
            throw new IllegalArgumentException(
                    "Seat number must be between 1 and " + hall.getCapacity() + " (hall capacity)");
        }

        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be blank");
        }
        if (customerName.length() > 255) {
            throw new IllegalArgumentException("Customer name is too long (max 255 characters)");
        }

        // Проверяем, что место ещё не занято (нет активного билета)
        ticketDao.findActiveBySessionIdAndSeatNumber(sessionId, seatNumber)
                .ifPresent(t -> {
                    throw new IllegalStateException(
                            "Seat " + seatNumber + " is already taken (ticket id=" + t.getId() + ")");
                });

        Ticket ticket = new Ticket(0, sessionId, seatNumber, TicketStatus.BOOKED, customerName.trim());
        return ticketDao.save(ticket);
    }

    public void payTicket(long ticketId) {
        Ticket ticket = ticketDao.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: id=" + ticketId));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled ticket");
        }
        if (ticket.getStatus() == TicketStatus.PAID) {
            throw new IllegalStateException("Ticket is already paid");
        }

        ticketDao.updateStatus(ticketId, TicketStatus.PAID);
    }

    public void cancelTicket(long ticketId) {
        Ticket ticket = ticketDao.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: id=" + ticketId));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("Ticket is already cancelled");
        }

        ticketDao.updateStatus(ticketId, TicketStatus.CANCELLED);
    }

    public List<Ticket> getTicketsBySession(long sessionId) {
        sessionDao.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: id=" + sessionId));
        return ticketDao.findBySessionId(sessionId);
    }

    public Optional<Ticket> getTicket(long ticketId) {
        return ticketDao.findById(ticketId);
    }
}
