package com.ef.db;

import com.ef.model.LogEntry;
import com.ef.util.BlockReason;
import com.ef.util.Duration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public final class RequestLogRepository {

    private static final String TABLE = "request_log";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_METHOD = "request_method";
    private static final String COLUMN_STATUS = "status_code";
    private static final String COLUMN_USER_AGENT = "user_agent";

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String CREATE_DB_SQL = String.format(
            "create table if not exists %s (" +
                    "id int unsigned auto_increment not null," +
                    "%s timestamp not null," +
                    "%s varchar not null," +
                    "%s varchar not null," +
                    "%s smallint not null," +
                    "%s varchar not null)",
            TABLE, COLUMN_DATE, COLUMN_IP, COLUMN_METHOD, COLUMN_STATUS, COLUMN_USER_AGENT);

    private static final String INSERT_SQL = String.format(
            "insert into %s (%s, %s, %s, %s, %s) values (?, ?, ?, ?, ?)",
            TABLE, COLUMN_DATE, COLUMN_IP, COLUMN_METHOD, COLUMN_STATUS, COLUMN_USER_AGENT);

    private static final String SELECT_WITH_THRESHOLD_SQL = String.format(
            "select %s, count(*) from %s where %s between ? and timestampadd(?,1,?) group by %s having count(*) > ?",
            COLUMN_IP, TABLE, COLUMN_DATE, COLUMN_IP);

    private static final PreparedStatement createDbStatement;
    private static final PreparedStatement insertStatement;
    private static final PreparedStatement selectWithThresholdStatement;

    static {
        createDbStatement = DbHelper.getPreparedStatement(CREATE_DB_SQL);
        try {
            createDbStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO everywhere
        }

        insertStatement = DbHelper.getPreparedStatement(INSERT_SQL);

        selectWithThresholdStatement = DbHelper.getPreparedStatement(SELECT_WITH_THRESHOLD_SQL);
    }

    private RequestLogRepository() {

    }

    public static void add(LogEntry entry) {
        try {
            insertStatement.setTimestamp(1, Timestamp.valueOf(entry.getDate()));
            insertStatement.setString(2, entry.getIp());
            insertStatement.setString(3, entry.getRequestMethod());
            insertStatement.setInt(4, entry.getStatusCode());
            insertStatement.setString(5, entry.getUserAgent());
            insertStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> findIpThresholdExceeded(LocalDateTime date, Duration duration, int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException(String.format("Threshold = %d. Threshold must be greater than 0.", threshold));
        }
        String dateAsString = date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        String interval = duration.getSqlIntervalName();

        Set<String> result = new HashSet<>();
        try {
            selectWithThresholdStatement.setString(1, dateAsString);
            selectWithThresholdStatement.setString(2, interval);
            selectWithThresholdStatement.setString(3, dateAsString);
            selectWithThresholdStatement.setInt(4, threshold);
            /*
            DatabaseConfig.getPreparedStatement("select ip, count(*) from request_log
            where date between '2017-01-01 15:00:00' and timestampadd(hour,1,'2017-01-01 15:00:00')
            group by ip having count(*) > 200").executeQuery()
             */
            // TODO select + insert transaction
            ResultSet resultSet = selectWithThresholdStatement.executeQuery();
            while (resultSet.next()) {
                String ip = resultSet.getString(COLUMN_IP);
                result.add(ip);
                BlockedIpRepository.add(ip, BlockReason.TOO_MANY_REQUESTS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
