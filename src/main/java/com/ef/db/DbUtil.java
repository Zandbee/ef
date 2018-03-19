package com.ef.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Database parameters must be provided via system properties:
 * -DdbUrl, -DdbUser, -DdbPassword.
 */
public final class DbUtil {

    private static final Logger LOG = Logger.getLogger(DbUtil.class.getName());

    private static final String ARG_DB_URL = "dbUrl";
    private static final String ARG_USER = "dbUser";
    private static final String ARG_PASSWORD = "dbPassword";

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private DbUtil() {
        registerDriver();
    }

    public static Connection getConnection() {
        String dbUrl = System.getProperty(ARG_DB_URL);
        String user = System.getProperty(ARG_USER);
        String pswd = System.getProperty(ARG_PASSWORD);
        try {
            return DriverManager.getConnection(dbUrl, user, pswd);
        } catch (SQLException ex) {
            throw new RuntimeException(
                    "MySQL connection params must be provided as system properties: -DdbUrl, -DdbUser, -DdbPassword", ex);
        }
    }

    public static PreparedStatement prepareStatement(Connection conn, String query) {
        try {
            return conn.prepareStatement(query);
        } catch (SQLException ex) {
            throw new RuntimeException(
                    String.format("Failed to prepare statement: %s.", query), ex);
        }
    }

    public static <R> R execute(SqlExecution<R> f) {
        try {
            return f.executeSql();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public interface SqlExecution<R> {
        R executeSql() throws SQLException;
    }

    private static void registerDriver() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            LOG.severe(String.format("Cannot find database driver: %s. %s", JDBC_DRIVER, ex));
        }
    }

}
