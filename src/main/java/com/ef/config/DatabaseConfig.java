package com.ef.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

public final class DatabaseConfig {

    private static final Logger LOG = Logger.getLogger(DatabaseConfig.class.getName());

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:h2:mem:logs";
    private static final String USER = "root";
    private static final String PASS = "password";

    private DatabaseConfig() {
        registerDriver();
    }

    public static PreparedStatement getPreparedStatement(String query) { // TODO move to db utils?
        Optional<Connection> conn = getConnection();
        if (conn.isPresent()) {
            try {
                return conn.get().prepareStatement(query);
            } catch (SQLException ex) {
                LOG.severe(String.format("Failed to prepare statement: %s. %s", query, ex));
                throw new RuntimeException("Failed to prepare statement.", ex); // TODO should fail, not just log
            }
        }
        throw new RuntimeException("Failed to prepare statement.");
    }

    private static Optional<Connection> getConnection() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); // TODO close resources
            LOG.info(String.format("Connecting to database: %s, username: %s.", DB_URL, USER));
            return Optional.ofNullable(conn);
        } catch (SQLException ex) {
            LOG.severe(String.format("Cannot connect to database: %s, username: %s. %s", DB_URL, USER, ex));
            return Optional.empty();
        }
    }

    private static void registerDriver() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            LOG.severe(String.format("Cannot find database driver: %s. %s", JDBC_DRIVER, ex));
        }
    }

}
