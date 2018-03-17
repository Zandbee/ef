package com.ef.db;

import com.ef.util.BlockReason;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockedIpRepository {

    private static final String TABLE = "blocked_ip";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_REASON = "reason";

    private static final String CREATE_DB_SQL = String.format(
            "create table if not exists %s (" +
                    "id int unsigned auto_increment not null," +
                    "%s varchar not null," +
                    "%s timestamp not null," +
                    "%s varchar not null)",
            TABLE, COLUMN_IP, COLUMN_DATE, COLUMN_REASON);

    private static final String INSERT_SQL = String.format(
            "insert into %s (%s, %s, %s) values (now(), ?, ?)",
            TABLE, COLUMN_DATE, COLUMN_IP, COLUMN_REASON);

    private static final PreparedStatement createDbStatement;
    private static final PreparedStatement insertStatement;

    static {
        createDbStatement = DbHelper.getPreparedStatement(CREATE_DB_SQL);
        try {
            createDbStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO everywhere
        }

        insertStatement = DbHelper.getPreparedStatement(INSERT_SQL);
    }

    // TODO add batch
    public static void add(String ip, BlockReason reason) {
        try {
            insertStatement.setString(1, ip);
            insertStatement.setString(2, reason.toString());
            insertStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
