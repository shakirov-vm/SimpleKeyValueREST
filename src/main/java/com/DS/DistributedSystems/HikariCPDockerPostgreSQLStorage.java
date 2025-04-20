package com.DS.DistributedSystems;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class HikariCPDockerPostgreSQLStorage implements Storage {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/java_db");
        config.setUsername("postgres");
        config.setPassword("mysecretpassword");
        config.setMaximumPoolSize(20);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void put(String key, String value) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO key_value (key, value) VALUES (?, ?) " +
                             "ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value")) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String key) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT value FROM key_value WHERE key = ?")) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("value") : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
