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
    private static final PreparedStatement insertBatchStatement;

    static {
        createDbStatement = DbHelper.prepareStatement(CREATE_DB_SQL);
        try {
            createDbStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO everywhere
        }

        insertBatchStatement = DbHelper.prepareBatchStatement(INSERT_SQL);
    }

    public static void add(Batch<String> ips, BlockReason reason) {
        try {
            ips.getEntries().forEach(ip -> {
                try {
                    insertBatchStatement.setString(1, ip);
                    insertBatchStatement.setString(2, reason.toString());
                    insertBatchStatement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            insertBatchStatement.executeBatch();
            insertBatchStatement.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
