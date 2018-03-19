package com.ef;

import com.ef.db.RequestLogDao;
import com.ef.model.LogEntry;
import com.ef.util.DataUtil;
import com.ef.util.Duration;
import com.ef.util.LogReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class Parser {

    private static final Logger LOG = Logger.getLogger(Parser.class.getName());

    private static final String ARG_START_DATE = "--startDate";
    private static final String ARG_DURATION = "--duration";
    private static final String ARG_THRESHOLD = "--threshold";
    private static final String ARG_PATH = "--accesslog";
    private static final int ARGS_NUM = 3;

    private static final String DATE_PATTERN = "yyyy-MM-dd.HH:mm:ss";

    public static void main(String[] args) throws Exception {
        LOG.info("START");
        RequestLogDao requestLogDao = new RequestLogDao();

        Arguments arguments = readArgs(args);
        // If the --accesslog arg is provided, the file is read and loaded to the db.
        // Otherwise, only select query is executed on the data already present in the db.
        String filePath = arguments.getPath();
        if (DataUtil.isNotEmpty(filePath)) {
            try (InputStream is = new FileInputStream(filePath)) {
                LOG.info("Loading data...");
                LogReader logReader = new LogReader(is);
                List<LogEntry> logEntries;
                while ((logEntries = logReader.readNext(null)) != null) {
                    requestLogDao.add(logEntries);
                }
            }
        }

        Set<String> ips = requestLogDao.findIpThresholdExceeded(
                arguments.getDate(), arguments.getDuration(), arguments.getThreshold());
        LOG.info("IPs that exceeded the threshold: " + String.join(", ", ips));
        LOG.info("DONE");
    }

    private static Arguments readArgs(String[] args) {
        if (args != null && args.length >= ARGS_NUM) {
            Map<String, String> params = new HashMap<>(ARGS_NUM);
            for (String arg : args) {
                String[] parts = arg.split("=");
                if (parts.length == 2) {
                    params.put(parts[0], parts[1]);
                }
            }
            String dateString = params.get(ARG_START_DATE);
            String durationString = params.get(ARG_DURATION);
            String thresholdString = params.get(ARG_THRESHOLD);
            String pathString = params.get(ARG_PATH);
            if (dateString == null || durationString == null || thresholdString == null) {
                throw new IllegalArgumentException("Invalid input arguments.");
            }
            Optional<LocalDateTime> dateOpt = DataUtil.getValidDate(dateString, DATE_PATTERN);
            Optional<Duration> durationOpt = Duration.valueOfIgnoreCase(durationString);
            Optional<Integer> thresholdOpt = DataUtil.getInt(thresholdString);

            if (!dateOpt.isPresent()) {
                throw new IllegalArgumentException("Cannot parse start date arg. Expected 'yyyy-MM-dd.HH:mm:ss' format.");
            }
            if (!durationOpt.isPresent()) {
                throw new IllegalArgumentException("Cannot parse duration arg. Allowable values: daily, hourly.");
            }
            if (!thresholdOpt.isPresent()) {
                throw new IllegalArgumentException("Cannot parse threshold arg. Must be a valid integer.");
            }

            return new Arguments(dateOpt.get(), durationOpt.get(), thresholdOpt.get(), pathString);
        } else {
            throw new IllegalArgumentException("Missing input arguments.");
        }
    }

    private static class Arguments {

        private LocalDateTime date;
        private Duration duration;
        private int threshold;
        private String path;

        Arguments(LocalDateTime date, Duration duration, int threshold, String path) {
            this.date = date;
            this.duration = duration;
            this.threshold = threshold;
            this.path = path;
        }

        LocalDateTime getDate() {
            return date;
        }

        Duration getDuration() {
            return duration;
        }

        int getThreshold() {
            return threshold;
        }

        String getPath() {
            return path;
        }
    }

}
