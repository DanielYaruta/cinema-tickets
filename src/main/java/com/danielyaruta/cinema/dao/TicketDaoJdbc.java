package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.db.ConnectionManager;
import com.danielyaruta.cinema.model.Ticket;
import com.danielyaruta.cinema.model.TicketStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// SRP: отвечает исключительно за SQL-операции с таблицей tickets.
// LSP: является полноценной заменой интерфейса TicketDao.
public class TicketDaoJdbc implements TicketDao {

    @Override
    public Ticket save(Ticket ticket) {
        String sql = "INSERT INTO tickets (session_id, seat_number, status, customer_name) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, ticket.getSessionId());
            stmt.setInt(2, ticket.getSeatNumber());
            stmt.setString(3, ticket.getStatus().name());
            stmt.setString(4, ticket.getCustomerName());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ticket.setId(rs.getLong("id"));
                }
            }
            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save ticket", e);
        }
    }

    @Override
    public Optional<Ticket> findById(long id) {
        String sql = "SELECT id, session_id, seat_number, status, customer_name FROM tickets WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ticket by id=" + id, e);
        }
    }

    @Override
    public List<Ticket> findAll() {
        String sql = "SELECT id, session_id, seat_number, status, customer_name FROM tickets ORDER BY id";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Ticket> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all tickets", e);
        }
    }

    @Override
    public List<Ticket> findBySessionId(long sessionId) {
        String sql = "SELECT id, session_id, seat_number, status, customer_name FROM tickets WHERE session_id = ? ORDER BY seat_number";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Ticket> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find tickets for session id=" + sessionId, e);
        }
    }

    @Override
    public Optional<Ticket> findActiveBySessionIdAndSeatNumber(long sessionId, int seatNumber) {
        // Возвращает билет только если он не отменён — используется для проверки занятости места
        String sql = """
                SELECT id, session_id, seat_number, status, customer_name
                FROM tickets
                WHERE session_id = ? AND seat_number = ? AND status <> 'CANCELLED'
                LIMIT 1
                """;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sessionId);
            stmt.setInt(2, seatNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ticket for session=" + sessionId + " seat=" + seatNumber, e);
        }
    }

    @Override
    public void updateStatus(long id, TicketStatus status) {
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update status for ticket id=" + id, e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM tickets WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ticket id=" + id, e);
        }
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getLong("id"),
                rs.getLong("session_id"),
                rs.getInt("seat_number"),
                TicketStatus.valueOf(rs.getString("status")),
                rs.getString("customer_name")
        );
    }
}
