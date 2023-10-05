package com.tanyalova.influxdb;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.logging.log4j.core.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tanyalova.common.Constants.*;
import static com.tanyalova.common.Utils.sleep;

public class InfluxDbReporter {
    public static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbReporter.class);

    private enum Status {PASSED, FAILED, SKIPPED}

    private static ThreadLocal<Map<String, Long>> kpiStorage = ThreadLocal.withInitial(HashMap::new);
    private static ThreadLocal<Map<String, String>> screenshotStorage = ThreadLocal.withInitial(HashMap::new);
    private static ThreadLocal<String> deviceName = new ThreadLocal<>();
    private static ThreadLocal<String> methodName = new ThreadLocal<>();
    private static ThreadLocal<String> appName = new ThreadLocal<String>();
    private static ThreadLocal<String> videoStorage = new ThreadLocal<>();
    private static ThreadLocal<String> testRunId = new ThreadLocal<>();
    private static ThreadLocal<Integer> failedTestCounter = new ThreadLocal<>();
    private static ThreadLocal<LogEvent> eventStorage = new ThreadLocal<>();
    private static ThreadLocal<String> unixTimestamp = new ThreadLocal<>();
    private static final String LINE = "--------===========-----------";
    private static final String LINE2 = "[INFO] Utils - Sleep for 1 seconds";

    public static void setFailedTestCounter(int failedTestCounter) {
        InfluxDbReporter.failedTestCounter.set(failedTestCounter);
    }

    public static Integer getFailedTestCounter() {
        return failedTestCounter.get() == null ? 0 : failedTestCounter.get();
    }

    public static void setUnixTimestamp() {
        long unixTimestamp = Instant.now().getEpochSecond();
    }

    public static Long getUnixTimestamp() {
        return Long.valueOf(unixTimestamp.get());
    }

    public static void setTestRunId(String testId) {
        LOGGER.info("set test id: " + testId);
        InfluxDbReporter.testRunId.set(testId);
    }

    public static String getTestRunId() {
        if (InfluxDbReporter.testRunId.get() == null || InfluxDbReporter.testRunId.get().isEmpty()) {
            LOGGER.info("set id: " + InfluxDbReporter.testRunId.get());
            setTestRunId(UUID.randomUUID().toString());
        }
        return InfluxDbReporter.testRunId.get();
    }

    public static void setTestRunId(ThreadLocal<String> testRunId) {
        InfluxDbReporter.testRunId = testRunId;
    }

    public static void setVideoStorage(String videoLocation) {
        InfluxDbReporter.videoStorage.set(videoLocation);
    }

    public static String getVideoStorage() {
        return videoStorage.get();
    }

    public static Map<String, Long> getKpi() {
        return kpiStorage.get();
    }

    public static void setKpi(String methodName, Long kpi) {
        kpiStorage.get().put(methodName, kpi);
    }

    public static Map<String, String> getScreenshot() {
        return screenshotStorage.get();
    }

    public static String getScreenshotUrl(String methodName) {
        return screenshotStorage.get().get(methodName + "_screenshot");
    }

    public static void setScreenshot(String methodName, String screenshot) {
        screenshotStorage.get().put(methodName + "_screenshot", screenshot);
    }

    public static void setDeviceName(String deviceName) {
        InfluxDbReporter.deviceName.set(deviceName);
    }

    public static String getDeviceName() {
        return deviceName.get();
    }

    public static String getMethodName() {
        return methodName.get();
    }

    public static void setMethodName(String methodName) {
        InfluxDbReporter.methodName.set(methodName);
    }

    public static synchronized void postTestClassStatus(String className, int numberOfFailedTests) {
        LOGGER.info("postTestClassStatus, id: " + getTestRunId() + ", device: " + getDeviceName() + "method" + getMethodName());
        List<String> logs = readLogsFile();
        Point point = Point.measurement(getDeviceName() + className)
                .time(System.currentTimeMillis(), WritePrecision.MS)
                .addField(VIDEO_STORAGE, getVideoStorage())
                .addField("logs", logs != null ? String.join("\n", logs) : "")
                .addFields(new HashMap<>(getKpi()))
                .addFields(new HashMap<>(getScreenshot()))
                .addField(TEST_RUN_ID, getTestRunId())
                .addTag(CLASS_NAME, className)
                .addTag(DEVICE_NAME, getDeviceName())
                .addTag(TEST_RUN_ID, getTestRunId())
                .addTag(STATUS, numberOfFailedTests == 0 ? Status.PASSED.name() : Status.FAILED.name());
        InfluxDbApi.getInstance().post(point);
        LOGGER.info("postTestClassStatus: DONE");
    }

    private synchronized static void waitForLogsSaved(Path path) {
        String log = Thread.currentThread().getName() + "] [INFO] BasePage - Stop Video recording";
        int j = 0;
        try {
            while (!Files.lines(path).collect(Collectors.toList()).contains(log) && j++ < 10) {
                for (int i=0;i<50;i++) {
                    LOGGER.info(LINE);
                }
                sleep(1);
            }
        } catch (Exception ignored) {
        }
    }

    public static synchronized List<String> readLogsFile() {
        Set<String> methods = getScreenshot().keySet();
        List<String> logs = new LinkedList<>();
        Path path = Paths.get("target/all.log");
        waitForLogsSaved(path);
        try (Stream<String> stream = Files.lines(path)) {//BaseTestClass.baos.toString().lines()
            stream.filter(l -> l.contains(Thread.currentThread().getName()))
                    .filter(l -> !l.contains(LINE)  && !l.contains(LINE2))
                    .forEach(l -> {
                        String method = null;
                        if (l.contains("BasePage")) {
                            for (String m : methods) {
                                if (l.contains(m)) {
                                    method = m;
                                    break;
                                }
                            }
                        }
                        if (method != null) {
                            String url = getScreenshotUrl(method);
                            logs.add(l + " " + (url != null ? url : ""));
                        } else {
                            logs.add(l);
                        }
                    });
            return logs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
