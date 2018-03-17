package com.ef.model;

import java.time.LocalDateTime;

public class LogEntry {  // TODO move?

    private LocalDateTime date;
    private String ip;
    private String requestMethod;
    private int statusCode;
    private String userAgent;

    public LogEntry() {
    }

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

    public LogEntry setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public LogEntry setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public LogEntry setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LogEntry setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LogEntry setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }
}
