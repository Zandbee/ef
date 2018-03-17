package com.ef.util;

import com.ef.db.Batch;
import com.ef.db.RequestLogRepository;
import com.ef.model.LogEntry;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class LogReader { // TODO move

    private static final Logger LOG = Logger.getLogger(LogReader.class.getName());
    private static final String DELIMITER = "\\|";
    private static final int BATCH_SIZE = 900;

    private LogReader() {
    }

    public static void readFileFromResources(String fileName) {
        Batch<LogEntry> batch = new Batch<>();
        try (Stream<String> lines = Files.lines( // TODO: substitute with a fileName or extract to const
                Paths.get(ClassLoader.getSystemResource("access.log").toURI()))) {
            lines.forEach(line -> {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 5) {
                    Optional<LocalDateTime> date = DataValidator.getValidDate(trim(parts[0]));
                    String ip = trim(parts[1]);
                    String requestMethod = trimWithQuotes(parts[2]);
                    Optional<Integer> statusCode = DataValidator.getInt(trim(parts[3]));
                    String userAgent = trimWithQuotes(parts[4]);
                    if (date.isPresent() && statusCode.isPresent() && DataValidator.isValidIp(ip)
                            && DataValidator.isValidStringAll(requestMethod, userAgent)) {
                        batch.add(new LogEntry(date.get(), ip, requestMethod, statusCode.get(), userAgent));
                        if (batch.size() >= BATCH_SIZE) {
                            RequestLogRepository.add(batch);
                            batch.clear();
                        }
                    }
                }
            });
            RequestLogRepository.add(batch);
        } catch (IOException | URISyntaxException ex) {
            LOG.severe(String.format("Cannot read file [%s]: %s", fileName, ex));
        } catch (SecurityException ex) { // TODO: need this?
            LOG.severe(String.format("Access denied to file [%s]: %s", fileName, ex));
        }
    }

    private static String trim(String string) {
        return string.trim();
    }

    private static String trimWithQuotes(String string) {
        return string.trim().replaceAll("^\"|\"$", "");
    }

}
