package com.ef.util;

import com.ef.model.LogEntry;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LogReader implements Closeable {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DELIMITER = "\\|";
    private static final int BATCH_SIZE = 900;

    private BufferedReader reader;

    public LogReader(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public List<LogEntry> readNext(Integer size) throws IOException {
        if (size == null || size <= 0) {
            size = BATCH_SIZE;
        }
        List<LogEntry> batch = new ArrayList<>();
        String line;
        while (batch.size() < size && (line = reader.readLine()) != null) {
            String[] parts = line.split(DELIMITER);
            if (parts.length == 5) {
                Optional<LocalDateTime> date = DataUtil.getValidDate(trim(parts[0]), DATE_PATTERN);
                String ip = trim(parts[1]);
                String requestMethod = trimWithQuotes(parts[2]);
                Optional<Integer> statusCode = DataUtil.getInt(trim(parts[3]));
                String userAgent = trimWithQuotes(parts[4]);
                if (date.isPresent() && statusCode.isPresent() && DataUtil.isValidIp(ip)
                        && DataUtil.isNoneEmpty(requestMethod, userAgent)) {
                    batch.add(new LogEntry(date.get(), ip, requestMethod, statusCode.get(), userAgent));
                }
            }
        }
        if (batch.isEmpty()) {
            return null;
        } else {
            return batch;
        }
    }

    private static String trim(String string) {
        return string.trim();
    }

    private static String trimWithQuotes(String string) {
        return string.trim().replaceAll("^\"|\"$", "");
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
