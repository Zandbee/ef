package com.ef.util;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.logging.Logger;

public final class DataValidator {

    private static final Logger LOG = Logger.getLogger(DataValidator.class.getName());
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final InetAddressValidator IP_VALIDATOR = InetAddressValidator.getInstance();

    private DataValidator() {
    }

    public static Optional<LocalDateTime> getValidDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        try {
            return Optional.of(LocalDateTime.parse(dateString, formatter));
        } catch (DateTimeParseException ex) {
            LOG.warning(String.format("Cannot parse date: %s - %s", dateString, ex));
            return Optional.empty();
        }
    }

    public static boolean isValidIp(String ipString) {
        return IP_VALIDATOR.isValid(ipString);
    }

    public static Optional<Integer> getInt(String intString) {
        try {
            return Optional.of(Integer.parseInt(intString));
        } catch (NumberFormatException ex) {
            LOG.warning(String.format("Cannot parse int: %s - %s", intString, ex));
            return Optional.empty();
        }
    }

    public static boolean isValidString(String string) {
        return string != null && !string.isEmpty();
    }

    public static boolean isValidStringAll(String... strings) {
        for (String string : strings) {
            if (!isValidString(string)) return false;
        }
        return true;
    }
}
