package com.ef;

import com.ef.config.DatabaseConfig;
import com.ef.db.RequestLogRepository;
import com.ef.util.DataValidator;
import com.ef.util.Duration;
import com.ef.util.LogReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class Parser {

    private static final Logger LOG = Logger.getLogger(Parser.class.getName());

    public static void main(String[] args) {

        //new RequestLogRepository();
        //DatabaseConfig.getPreparedStatement();
        LogReader.readFileFromResources("");
        RequestLogRepository.findIpThresholdExceeded(
                LocalDateTime.parse("2017-01-01.15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss")),
                Duration.HOURLY, 200).forEach(ip -> LOG.info("hourly: " + ip));

        RequestLogRepository.findIpThresholdExceeded(
                LocalDateTime.parse("2017-01-01.00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss")),
                Duration.DAILY, 500).forEach(ip -> LOG.info("daily: " + ip));
    }
}
