package com.danielyaruta.cinema.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// SRP: единственная ответственность — инициализация схемы БД при старте приложения.
// Не знает ни о бизнес-логике, ни о CLI.
public class SchemaInitializer {

    public void initialize() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS movies (
                        id               BIGSERIAL PRIMARY KEY,
                        title            VARCHAR(255) NOT NULL,
                        genre            VARCHAR(100),
                        duration_minutes INT NOT NULL
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS halls (
                        id       BIGSERIAL PRIMARY KEY,
                        name     VARCHAR(100) NOT NULL,
                        capacity INT NOT NULL
                    )
                    """);

            // FK на movies и halls — referential integrity на уровне БД
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS sessions (
                        id         BIGSERIAL PRIMARY KEY,
                        movie_id   BIGINT         NOT NULL REFERENCES movies(id),
                        hall_id    BIGINT         NOT NULL REFERENCES halls(id),
                        start_time TIMESTAMP      NOT NULL,
                        price      NUMERIC(10, 2) NOT NULL
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS tickets (
                        id            BIGSERIAL PRIMARY KEY,
                        session_id    BIGINT       NOT NULL REFERENCES sessions(id),
                        seat_number   INT          NOT NULL,
                        status        VARCHAR(20)  NOT NULL DEFAULT 'BOOKED',
                        customer_name VARCHAR(255) NOT NULL
                    )
                    """);

            System.out.println("Schema initialized successfully.");

        } catch (SQLException e) {
            throw new RuntimeException("Schema initialization failed", e);
        }
    }
}
