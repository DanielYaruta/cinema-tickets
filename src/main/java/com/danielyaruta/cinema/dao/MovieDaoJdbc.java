package com.danielyaruta.cinema.dao;

import com.danielyaruta.cinema.db.ConnectionManager;
import com.danielyaruta.cinema.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// SRP: отвечает исключительно за SQL-операции с таблицей movies.
// Не содержит бизнес-логики и не знает о CLI.
//
// LSP: MovieDaoJdbc полностью выполняет контракт MovieDao — её можно подставить
// везде, где ожидается MovieDao, без изменения поведения вызывающего кода.
public class MovieDaoJdbc implements MovieDao {

    @Override
    public Movie save(Movie movie) {
        String sql = "INSERT INTO movies (title, genre, duration_minutes) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDurationMinutes());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    movie.setId(rs.getLong("id"));
                }
            }
            return movie;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save movie", e);
        }
    }

    @Override
    public Optional<Movie> findById(long id) {
        String sql = "SELECT id, title, genre, duration_minutes FROM movies WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find movie by id=" + id, e);
        }
    }

    @Override
    public List<Movie> findAll() {
        String sql = "SELECT id, title, genre, duration_minutes FROM movies ORDER BY id";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Movie> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all movies", e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete movie id=" + id, e);
        }
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("duration_minutes")
        );
    }
}
