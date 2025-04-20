package com.DS.DistributedSystems;

import java.sql.*;

public class MultiThreadDockerPostgreSQLStorage implements Storage {

    private static final String url = "jdbc:postgresql://localhost:5432/java_db";
    private static final String user = "postgres";
    private static final String password = "mysecretpassword";

    public MultiThreadDockerPostgreSQLStorage() {

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM pg_roles");

            while (rs.next()) {
                int id = rs.getInt("oid");
                String name = rs.getString("rolname");
                System.out.println("ID: " + id + ", Name: " + name);
            }

            stmt.execute("CREATE TABLE IF NOT EXISTS key_value (" +
                    "key VARCHAR(100) PRIMARY KEY, " +
                    "value VARCHAR(100) NOT NULL)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void put(String key, String value) {

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO key_value (key, value) VALUES (?, ?) " +
                             "ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value")) {

            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
//                System.out.println("Добавлено строк: " + rowsInserted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String get(String key) {

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT value FROM key_value WHERE key = ?")) {

            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("value") : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
