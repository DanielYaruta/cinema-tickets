package com.danielyaruta.cinema.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// SRP: единственная ответственность — создавать и предоставлять соединения с БД.
// Не содержит SQL-запросов, не знает о сущностях — только управление соединением.
public class ConnectionManager {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ConnectionManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConnectionManager() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
}
