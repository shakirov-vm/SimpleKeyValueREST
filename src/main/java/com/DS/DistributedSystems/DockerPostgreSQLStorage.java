package com.DS.DistributedSystems;

import java.sql.*;

public class DockerPostgreSQLStorage implements Storage {

    private Connection conn;

    public DockerPostgreSQLStorage() {
        String url = "jdbc:postgresql://localhost:5432/java_db";
        String user = "postgres";
        String password = "mysecretpassword";

        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
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

        try {
            String sql = "INSERT INTO key_value (key, value) VALUES (?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, key);
                pstmt.setString(2, value);

                int rowsInserted = pstmt.executeUpdate();
//                System.out.println("Добавлено строк: " + rowsInserted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String get(String key) {

        String sql = "SELECT value FROM key_value WHERE key = ?";

        try {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, key);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("value");
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
