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

    public static PreparedStatement getPreparedStatement(String query) {
        Optional<Connection> conn = DatabaseConfig.getConnection();
        if (conn.isPresent()) {
            try {
                return conn.get().prepareStatement(query);
            } catch (SQLException ex) {
                LOG.severe(String.format("Failed to prepare statement: %s. %s", query, ex));
                throw new RuntimeException("Failed to prepare statement.", ex);
            }
        }
        throw new RuntimeException("Failed to prepare statement.");
    }

}
