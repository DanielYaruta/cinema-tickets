package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.db.ConnectionManager;
import com.danielyaruta.cinema.model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// SRP: отвечает исключительно за SQL-операции с таблицей sessions.
// LSP: является полноценной заменой интерфейса SessionDao.
public class SessionDaoJdbc implements SessionDao {

    @Override
    public Session save(Session session) {
        String sql = "INSERT INTO sessions (movie_id, hall_id, start_time, price) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, session.getMovieId());
            stmt.setLong(2, session.getHallId());
            stmt.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            stmt.setBigDecimal(4, session.getPrice());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    session.setId(rs.getLong("id"));
                }
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save session", e);
        }
    }

    @Override
    public Optional<Session> findById(long id) {
        String sql = "SELECT id, movie_id, hall_id, start_time, price FROM sessions WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find session by id=" + id, e);
        }
    }

    @Override
    public List<Session> findAll() {
        String sql = "SELECT id, movie_id, hall_id, start_time, price FROM sessions ORDER BY start_time";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Session> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all sessions", e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM sessions WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete session id=" + id, e);
        }
    }

    private Session mapRow(ResultSet rs) throws SQLException {
        return new Session(
                rs.getLong("id"),
                rs.getLong("movie_id"),
                rs.getLong("hall_id"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getBigDecimal("price")
        );
    }
}
