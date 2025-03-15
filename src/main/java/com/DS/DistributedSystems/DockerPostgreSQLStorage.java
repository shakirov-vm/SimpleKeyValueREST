package com.DS.DistributedSystems;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DockerPostgreSQLStorage implements Storage {

    public DockerPostgreSQLStorage() {
        String url = "jdbc:postgresql://localhost:5432/java_db";
        String user = "postgres";
        String password = "mysecretpassword";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pg_roles")) {

            while (rs.next()) {
                int id = rs.getInt("oid");
                String name = rs.getString("rolname");
                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void put(String key, String value) {
//        storage.put(key, value);
    }
    public String get(String key) {
        return "";
//        return storage.get(key);
    }
}
