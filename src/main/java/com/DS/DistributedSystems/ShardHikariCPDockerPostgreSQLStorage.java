package com.DS.DistributedSystems;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class ShardHikariCPDockerPostgreSQLStorage implements Storage {

    private static final HikariDataSource shard1DataSource;
    private static final HikariDataSource shard2DataSource;

    static {

        HikariConfig shard1Config = new HikariConfig();
        shard1Config.setJdbcUrl("jdbc:postgresql://localhost:5432/java_db1");
        shard1Config.setUsername("postgres");
        shard1Config.setPassword("mysecretpassword");
        shard1Config.setMaximumPoolSize(20);
        shard1DataSource = new HikariDataSource(shard1Config);

        HikariConfig shard2Config = new HikariConfig();
        shard2Config.setJdbcUrl("jdbc:postgresql://localhost:5433/java_db2");
        shard2Config.setUsername("postgres");
        shard2Config.setPassword("mysecretpassword");
        shard2Config.setMaximumPoolSize(20);
        shard2DataSource = new HikariDataSource(shard2Config);

        String ddl = "CREATE TABLE IF NOT EXISTS key_value (" +
                "key VARCHAR(100) PRIMARY KEY, " +
                "value VARCHAR(100) NOT NULL)";

        executeDdl(shard1DataSource, ddl);
        executeDdl(shard2DataSource, ddl);
    }
    private static void executeDdl(HikariDataSource dataSource, String ddl) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private HikariDataSource getShardDataSource(String key) {
        int hash = key.hashCode();
        System.out.println("hash % 2 is " + (hash % 2));
        return (hash % 2 == 0) ? shard1DataSource : shard2DataSource;
    }

    @Override
    public void put(String key, String value) {
        HikariDataSource dataSource = getShardDataSource(key);

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
        HikariDataSource dataSource = getShardDataSource(key);

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
