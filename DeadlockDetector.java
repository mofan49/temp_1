import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;

public class DeadlockDetector {
    private static final Gauge deadlockCount = Gauge.build()
            .name("deadlock_count")
            .help("Number of deadlocks detected")
            .register();

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public void checkForDeadlocks() {
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            deadlockCount.inc(); // Increment deadlock count
            System.out.println("Deadlock detected! Thread IDs: " + java.util.Arrays.toString(deadlockedThreads));
        }
    }

    public static void main(String[] args) throws Exception {
        new HTTPServer(1234); // Start a Prometheus metrics HTTP server on port 1234
        DeadlockDetector detector = new DeadlockDetector();
        
        // Check for deadlocks every 5 seconds
        while (true) {
            detector.checkForDeadlocks();
            Thread.sleep(5000);
        }
    }
}

