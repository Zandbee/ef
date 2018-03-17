package com.ef.db;

import com.ef.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

public final class DbHelper {

    private static final Logger LOG = Logger.getLogger(DbHelper.class.getName());

    private DbHelper() {
    }

    /**
     * @see #prepareBatchStatement
     */
    public static PreparedStatement prepareStatement(String query) {
        Optional<Connection> conn = DatabaseConfig.getConnection();
        if (conn.isPresent()) {
            return getPreparedStatement(conn.get(), query);
        } else {
            throw new RuntimeException("Failed to get DB connection.");
        }
    }

    /**
     * @return prepared statement with a connection's auto-commit mode
     * set to false
     */
    public static PreparedStatement prepareBatchStatement(String query) {
        Optional<Connection> conn = DatabaseConfig.getConnection();
        if (conn.isPresent()) {
            Connection connection = conn.get(); // TODO close connections, statements
            try {
                connection.setAutoCommit(false);
                return getPreparedStatement(connection, query);
            } catch (SQLException ex) {
                LOG.severe(String.format("Failed to prepare statement: %s. %s", query, ex));
                throw new RuntimeException("Failed to prepare statement.", ex);
            }
        } else {
            throw new RuntimeException("Failed to get DB connection.");
        }
    }

    private static PreparedStatement getPreparedStatement(Connection conn, String query) {
        try {
            return conn.prepareStatement(query);
        } catch (SQLException ex) {
            LOG.severe(String.format("Failed to prepare statement: %s. %s", query, ex));
            throw new RuntimeException("Failed to prepare statement.", ex);
        }
    }

}
