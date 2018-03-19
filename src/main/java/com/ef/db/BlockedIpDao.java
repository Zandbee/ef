package com.ef.db;

import com.ef.util.BlockReason;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class BlockedIpDao {

    private static final String TABLE = "blocked_ip";
    private static final String COLUMN_DATE = "`date`";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_REASON = "reason";

    private static final String INSERT_SQL = String.format(
            "insert into %s (%s, %s, %s) values (now(), ?, ?);",
            TABLE, COLUMN_DATE, COLUMN_IP, COLUMN_REASON);

    public BlockedIpDao() {
    }

    public void add(List<String> ips, BlockReason reason) {
        DbUtil.execute((DbUtil.SqlExecution<Void>) () -> {
            try (Connection conn = DbUtil.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement insertBatchStatement =
                             DbUtil.prepareStatement(conn, INSERT_SQL)) {
                    for (String ip : ips) {
                        insertBatchStatement.setString(1, ip);
                        insertBatchStatement.setString(2, reason.toString());
                        insertBatchStatement.addBatch();
                    }
                    insertBatchStatement.executeBatch();
                    conn.commit();
                    return null;
                }
            }
        });
    }

}
