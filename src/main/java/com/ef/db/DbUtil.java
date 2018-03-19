package com.ef.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Database parameters should be provided via system properties:
 * -DdbUrl, -DdbUser, -DdbPassword. Otherwise, the defaults are used.
 */
public final class DbUtil {

    private static final Logger LOG = Logger.getLogger(DbUtil.class.getName());

    private static final String ARG_DB_URL = "dbUrl";
    private static final String ARG_USER = "dbUser";
    private static final String ARG_PASSWORD = "dbPassword";

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/access_log?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private DbUtil() {
        registerDriver();
    }

    public static Connection getConnection() {
        String dbUrl = System.getProperty(ARG_DB_URL, DB_URL);
        String user = System.getProperty(ARG_USER, DB_USER);
        String pswd = System.getProperty(ARG_PASSWORD, DB_PASSWORD);
        try {
            return DriverManager.getConnection(dbUrl, user, pswd);
        } catch (SQLException ex) {
            throw new RuntimeException(
                    "MySQL connection should be provided as system properties: -DdbUrl, -DdbUser, -DdbPassword", ex);
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
