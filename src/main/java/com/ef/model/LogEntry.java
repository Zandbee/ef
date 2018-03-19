package com.ef.model;

import java.time.LocalDateTime;

public class LogEntry {

    private LocalDateTime date;
    private String ip;
    private String requestMethod;
    private int statusCode;
    private String userAgent;

    public LogEntry(LocalDateTime date, String ip, String requestMethod, int statusCode, String userAgent) {
        this.date = date;
        this.ip = ip;
        this.requestMethod = requestMethod;
        this.statusCode = statusCode;
        this.userAgent = userAgent;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getIp() {
        return ip;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
