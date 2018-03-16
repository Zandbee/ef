package com.ef.util;

public enum Duration {

    HOURLY("hour"), DAILY("day");

    private String sqlIntervalName;

    Duration(String intervalName) {
        this.sqlIntervalName = intervalName;
    }

    public String getSqlIntervalName() {
        return sqlIntervalName;
    }
}
