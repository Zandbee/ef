package com.ef.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class LogReader {

    private static final Logger LOG = Logger.getLogger(LogReader.class.getName());
    private static final String DELIMITER = "|";

    private LogReader() {
    }

    public static void readFileFromResources(String fileName) {
        try (Stream<String> lines = Files.lines( // TODO: substitute with a fileName
                Paths.get(ClassLoader.getSystemResource("access.log").toURI()))) {
            lines.forEach(line -> {
                LOG.info(line);
                String[] parts = line.split(DELIMITER);
                if (parts.length == 5) {
                    Optional<LocalDateTime> date = DataValidator.getValidDate(trim(parts[0]));
                    String ip = trim(parts[1]);
                    String requestMethod = trimWithQuotes(parts[2]);
                    Optional<Integer> statusCode = DataValidator.getInt(trim(parts[3]));
                    String userAgent = trimWithQuotes(parts[4]);
                    if (date.isPresent() && statusCode.isPresent() && DataValidator.isValidStringAll(ip, requestMethod, userAgent)) {

                    }
                }
            });
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
