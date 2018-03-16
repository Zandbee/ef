package com.ef.db;

import com.ef.util.LogReader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Config {

    private static final Logger LOG = Logger.getLogger(Config.class.getName());

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/";
    private static final String USER = "username";
    private static final String PASS = "password";

    public void connect() {
        try {
            Class.forName(JDBC_DRIVER);

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);) {

            } catch (SQLException e) {
                LOG.severe(String.format(""));
            }
        } catch (ClassNotFoundException e) {
            LOG.severe(String.format("Cannot find database driver: %s.", JDBC_DRIVER));
        }
    }

}
