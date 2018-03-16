package com.ef.model;

import java.time.LocalDateTime;

public class LogEntry {

    private LocalDateTime date;
    private String ip;
    private String requestMethod;
    private int responseCode;
    private String userAgent;

    public LogEntry() {
    }

    public LogEntry(LocalDateTime date, String ip, String requestMethod, int responseCode, String userAgent) {
        this.date = date;
        this.ip = ip;
        this.requestMethod = requestMethod;
        this.responseCode = responseCode;
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

    public int getResponseCode() {
        return responseCode;
    }

    public LogEntry setResponseCode(int responseCode) {
        this.responseCode = responseCode;
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
