package com.ef.db;

import com.ef.model.LogEntry;
import com.ef.util.BlockReason;
import com.ef.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestLogDao {

    private static final String TABLE = "request_log";
    private static final String COLUMN_DATE = "`date`";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_METHOD = "request_method";
    private static final String COLUMN_STATUS = "status_code";
    private static final String COLUMN_USER_AGENT = "user_agent";

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final int BATCH_SIZE = 900;

    private static final String INSERT_SQL = String.format(
            "insert into %s (%s, %s, %s, %s, %s) values (?, ?, ?, ?, ?);",
            TABLE, COLUMN_DATE, COLUMN_IP, COLUMN_METHOD, COLUMN_STATUS, COLUMN_USER_AGENT);

    public RequestLogDao() {
    }

    public void add(List<LogEntry> entries) {
        DbUtil.execute((DbUtil.SqlExecution<Void>) () -> {
            try (Connection conn = DbUtil.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement insertBatchStatement = DbUtil.prepareStatement(conn, INSERT_SQL)) {
                    for (LogEntry entry : entries) {
                        insertBatchStatement.setTimestamp(1, Timestamp.valueOf(entry.getDate()), Calendar.getInstance());
                        insertBatchStatement.setString(2, entry.getIp());
                        insertBatchStatement.setString(3, entry.getRequestMethod());
                        insertBatchStatement.setInt(4, entry.getStatusCode());
                        insertBatchStatement.setString(5, entry.getUserAgent());
                        insertBatchStatement.addBatch();
                    }
                    insertBatchStatement.executeBatch();
                    conn.commit();
                    return null;
                }
            }
        });
    }

    public Set<String> findIpThresholdExceeded(LocalDateTime date, Duration duration, int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException(String.format("Threshold = %d. Threshold must be greater than 0.", threshold));
        }
        String dateAsString = date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        String interval = duration.getSqlIntervalName();
        final String selectWithThresholdSql = String.format(
                "select %s, count(*) from %s where %s between ? and timestampadd(%s, 1, ?) group by %s having count(*) > ?;",
                COLUMN_IP, TABLE, COLUMN_DATE, interval, COLUMN_IP);
        Set<String> result = new HashSet<>();
        List<String> batch = new ArrayList<>();
        BlockedIpDao blockedIpDao = new BlockedIpDao();
        return DbUtil.execute(() -> {
            try (Connection conn = DbUtil.getConnection();
                 PreparedStatement selectWithThresholdStatement = DbUtil.prepareStatement(conn, selectWithThresholdSql)) {
                selectWithThresholdStatement.setString(1, dateAsString);
                selectWithThresholdStatement.setString(2, dateAsString);
                selectWithThresholdStatement.setInt(3, threshold);
                ResultSet resultSet = selectWithThresholdStatement.executeQuery();
                while (resultSet.next()) {
                    String ip = resultSet.getString(COLUMN_IP);
                    result.add(ip);
                    batch.add(ip);
                    if (batch.size() >= BATCH_SIZE) {
                        blockedIpDao.add(batch, BlockReason.TOO_MANY_REQUESTS);
                        batch.clear();
                    }
                }
                blockedIpDao.add(batch, BlockReason.TOO_MANY_REQUESTS);
                return result;
            }
        });
    }

}
