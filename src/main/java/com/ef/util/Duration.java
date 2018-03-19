package com.ef.util;

import java.util.Arrays;
import java.util.Optional;

public enum Duration {

    HOURLY("hour"), DAILY("day");

    private String sqlIntervalName;

    Duration(String intervalName) {
        this.sqlIntervalName = intervalName;
    }

    public String getSqlIntervalName() {
        return sqlIntervalName;
    }

    public static Optional<Duration> valueOfIgnoreCase(String name) {
        return Arrays.stream(Duration.values())
                .filter(platform -> platform.name().equalsIgnoreCase(name)).findFirst();
    }

}
