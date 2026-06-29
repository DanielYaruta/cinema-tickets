package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.db.ConnectionManager;
import com.danielyaruta.cinema.model.Hall;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// SRP: отвечает исключительно за SQL-операции с таблицей halls.
// LSP: является полноценной заменой интерфейса HallDao.
public class HallDaoJdbc implements HallDao {

    @Override
    public Hall save(Hall hall) {
        String sql = "INSERT INTO halls (name, capacity) VALUES (?, ?) RETURNING id";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hall.getName());
            stmt.setInt(2, hall.getCapacity());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hall.setId(rs.getLong("id"));
                }
            }
            return hall;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save hall", e);
        }
    }

    @Override
    public Optional<Hall> findById(long id) {
        String sql = "SELECT id, name, capacity FROM halls WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find hall by id=" + id, e);
        }
    }

    @Override
    public List<Hall> findAll() {
        String sql = "SELECT id, name, capacity FROM halls ORDER BY id";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Hall> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all halls", e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM halls WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete hall id=" + id, e);
        }
    }

    private Hall mapRow(ResultSet rs) throws SQLException {
        return new Hall(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("capacity")
        );
    }
}
