import com.ef.model.LogEntry;
import com.ef.util.LogReader;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AppTest {

    private static final String LOG_ENTRY_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    @Test
    public void shouldReadAllLines() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("test.log")) {
            List<LogEntry> batch;
            int step = 0;
            while ((batch = new LogReader(is).readNext(8)) != null) {
                if (step == 2) {
                    assertEquals("192.168.54.139", batch.get(0).getIp());
                    assertEquals("192.168.110.220", batch.get(7).getIp());
                }
                if (step < 3) {
                    assertEquals(8, batch.size());
                }
                if (step == 3) {
                    assertEquals(6, batch.size());
                    assertEquals("192.168.179.79", batch.get(5).getIp());
                }
                if (step > 3) {
                    throw new IllegalStateException("Redundant iteration.");
                }
                step++;
            }
        }
    }

    @Test
    public void shouldParseLogEntry() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("test.log")) {
            LogEntry entry = new LogReader(is).readNext(3).get(2);
            assertEquals("192.168.169.194", entry.getIp());
            assertEquals(LocalDateTime.parse("2017-01-01 00:00:23.003", DateTimeFormatter.ofPattern(LOG_ENTRY_DATE_PATTERN)),
                    entry.getDate());
            assertEquals("GET / HTTP/1.1", entry.getRequestMethod());
            assertEquals(200, entry.getStatusCode());
            assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393", entry.getUserAgent());
        }
    }

}
