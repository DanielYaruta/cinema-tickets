package com.danielyaruta.cinema.service;

import com.danielyaruta.cinema.dao.InMemoryHallDao;
import com.danielyaruta.cinema.dao.InMemorySessionDao;
import com.danielyaruta.cinema.dao.InMemoryTicketDao;
import com.danielyaruta.cinema.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {

    private TicketService ticketService;
    private long sessionId;

    @BeforeEach
    void setUp() {
        InMemoryHallDao    hallDao    = new InMemoryHallDao();
        InMemorySessionDao sessionDao = new InMemorySessionDao();
        InMemoryTicketDao  ticketDao  = new InMemoryTicketDao();

        ticketService = new TicketService(ticketDao, sessionDao, hallDao);

        Hall hall = hallDao.save(new Hall(0, "Hall A", 50));
        Session session = new Session(0, 1L, hall.getId(),
                LocalDateTime.now().plusDays(1), new BigDecimal("300"));
        session = sessionDao.save(session);
        sessionId = session.getId();
    }

    // --- Бронирование: успешные сценарии ---

    @Test
    void bookTicket_validData_returnsTicketWithStatusBooked() {
        Ticket ticket = ticketService.bookTicket(sessionId, 1, "Ivan Petrov");

        assertTrue(ticket.getId() > 0);
        assertEquals(sessionId, ticket.getSessionId());
        assertEquals(1, ticket.getSeatNumber());
        assertEquals(TicketStatus.BOOKED, ticket.getStatus());
        assertEquals("Ivan Petrov", ticket.getCustomerName());
    }

    @Test
    void bookTicket_firstAndLastSeat_bothSucceed() {
        assertDoesNotThrow(() -> ticketService.bookTicket(sessionId, 1, "First"));
        assertDoesNotThrow(() -> ticketService.bookTicket(sessionId, 50, "Last"));
    }

    @Test
    void bookTicket_customerNameTrimmed() {
        Ticket ticket = ticketService.bookTicket(sessionId, 5, "  Anna  ");
        assertEquals("Anna", ticket.getCustomerName());
    }

    // --- Бронирование: валидация ---

    @Test
    void bookTicket_nonExistingSession_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(999L, 1, "Customer"));
        assertTrue(ex.getMessage().contains("Session not found"));
    }

    @Test
    void bookTicket_seatZero_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(sessionId, 0, "Customer"));
        assertTrue(ex.getMessage().contains("between 1"));
    }

    @Test
    void bookTicket_seatExceedsCapacity_throwsIllegalArgumentException() {
        // Hall capacity = 50
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(sessionId, 51, "Customer"));
        assertTrue(ex.getMessage().contains("50"));
    }

    @Test
    void bookTicket_negativeSeat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(sessionId, -1, "Customer"));
    }

    @Test
    void bookTicket_blankCustomerName_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(sessionId, 1, "   "));
        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    void bookTicket_customerNameTooLong_throwsIllegalArgumentException() {
        String longName = "A".repeat(256);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.bookTicket(sessionId, 1, longName));
        assertTrue(ex.getMessage().contains("255"));
    }

    // --- Двойное бронирование ---

    @Test
    void bookTicket_alreadyTakenSeat_throwsIllegalStateException() {
        ticketService.bookTicket(sessionId, 10, "First Customer");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> ticketService.bookTicket(sessionId, 10, "Second Customer"));
        assertTrue(ex.getMessage().contains("already taken"));
    }

    @Test
    void bookTicket_afterCancellation_seatBecomesAvailableAgain() {
        Ticket first = ticketService.bookTicket(sessionId, 10, "First Customer");
        ticketService.cancelTicket(first.getId());

        // Должно пройти без ошибок
        Ticket second = ticketService.bookTicket(sessionId, 10, "New Customer");
        assertEquals(TicketStatus.BOOKED, second.getStatus());
    }

    // --- Оплата ---

    @Test
    void payTicket_bookedTicket_statusBecomePaid() {
        Ticket ticket = ticketService.bookTicket(sessionId, 1, "Customer");
        ticketService.payTicket(ticket.getId());

        Ticket updated = ticketService.getTicket(ticket.getId()).orElseThrow();
        assertEquals(TicketStatus.PAID, updated.getStatus());
    }

    @Test
    void payTicket_alreadyPaid_throwsIllegalStateException() {
        Ticket ticket = ticketService.bookTicket(sessionId, 1, "Customer");
        ticketService.payTicket(ticket.getId());

        assertThrows(IllegalStateException.class,
                () -> ticketService.payTicket(ticket.getId()));
    }

    @Test
    void payTicket_cancelledTicket_throwsIllegalStateException() {
        Ticket ticket = ticketService.bookTicket(sessionId, 1, "Customer");
        ticketService.cancelTicket(ticket.getId());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> ticketService.payTicket(ticket.getId()));
        assertTrue(ex.getMessage().contains("cancelled"));
    }

    @Test
    void payTicket_nonExistingTicket_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ticketService.payTicket(999L));
    }

    // --- Отмена ---

    @Test
    void cancelTicket_bookedTicket_statusBecomeCancelled() {
        Ticket ticket = ticketService.bookTicket(sessionId, 1, "Customer");
        ticketService.cancelTicket(ticket.getId());

        Ticket updated = ticketService.getTicket(ticket.getId()).orElseThrow();
        assertEquals(TicketStatus.CANCELLED, updated.getStatus());
    }

    @Test
    void cancelTicket_alreadyCancelled_throwsIllegalStateException() {
        Ticket ticket = ticketService.bookTicket(sessionId, 1, "Customer");
        ticketService.cancelTicket(ticket.getId());

        assertThrows(IllegalStateException.class,
                () -> ticketService.cancelTicket(ticket.getId()));
    }

    @Test
    void cancelTicket_nonExistingTicket_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ticketService.cancelTicket(999L));
    }

    // --- Список билетов ---

    @Test
    void getTicketsBySession_returnsOnlyTicketsForThatSession() {
        ticketService.bookTicket(sessionId, 1, "A");
        ticketService.bookTicket(sessionId, 2, "B");

        List<Ticket> tickets = ticketService.getTicketsBySession(sessionId);
        assertEquals(2, tickets.size());
        assertTrue(tickets.stream().allMatch(t -> t.getSessionId() == sessionId));
    }

    @Test
    void getTicketsBySession_nonExistingSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ticketService.getTicketsBySession(999L));
    }

    @Test
    void getTicketsBySession_sortedBySeatNumber() {
        ticketService.bookTicket(sessionId, 5, "C");
        ticketService.bookTicket(sessionId, 1, "A");
        ticketService.bookTicket(sessionId, 3, "B");

        List<Ticket> tickets = ticketService.getTicketsBySession(sessionId);
        assertEquals(List.of(1, 3, 5),
                tickets.stream().map(Ticket::getSeatNumber).toList());
    }
}
