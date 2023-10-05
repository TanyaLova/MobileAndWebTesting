package com.tanyalova.influxdb;

import com.influxdb.LogLevel;
import com.influxdb.client.*;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxColumn;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.tanyalova.common.ConfigProperties;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.tanyalova.common.ConfigProperties.getEnv;
import static com.tanyalova.common.ConfigProperties.getEnvProperty;
import static com.tanyalova.common.Constants.TEST_RUN_ID;
import static com.tanyalova.common.Utils.sleep;

public class InfluxDbApi {

    public static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbApi.class);
    private static final String URL = getEnvProperty("url");
    private static final String BUCKET_NAME = getEnvProperty("bucket");
    private static final String ORG_NAME = getEnvProperty("org");
    private static final String TOKEN = getEnvProperty("token");
    private static final OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS);

    private static final InfluxDBClientOptions influxDBClientOptions = InfluxDBClientOptions.builder()
            .url(URL)
            .authenticateToken(TOKEN.toCharArray())
            .okHttpClient(builder)
            .bucket(BUCKET_NAME)
            .org(ORG_NAME)
            .build();
    private static final InfluxDBClient INFLUXDB = InfluxDBClientFactory.create(influxDBClientOptions);
    private static InfluxDbApi influxDbApi;
    private static final WriteApiBlocking WRITE_API = INFLUXDB.getWriteApiBlocking();
    private static final DeleteApi DELETE_API = INFLUXDB.getDeleteApi();
    private static final BucketsApi BUCKETS_API = INFLUXDB.getBucketsApi();
    private static final LogLevel LOG_LEVEL = INFLUXDB.getLogLevel();

    private InfluxDbApi() {
        LOGGER.info("influxDBClientOptions: url=" + URL + ", org=" + ORG_NAME + ", bucket=" + BUCKET_NAME);
    }

    public static InfluxDbApi getInstance() {
        if (influxDbApi == null) {
            influxDbApi = new InfluxDbApi();
        }
        return influxDbApi;
    }


    public synchronized void post(final Point point) {
        WRITE_API.writePoint(point);
    }

    public synchronized void postLog(final Point log) {
        WRITE_API.writePoint(log);
    }

    public synchronized void validatePost() {
        LOGGER.info("validatePost");
        sleep(5);
        Map<String, Object> params = new HashMap<>();
//        params.put("bucketParam", "bucket");
//        params.put("startParam", "2013-12-19T18:22:39Z");//Instant.now().minusSeconds(300).toString()

        String parametrizedQuery = "from(bucket: \"" + BUCKET_NAME + "\") |> range(start: time(v: \"2013-12-19T18:22:39Z\"))";

        List<FluxTable> query = INFLUXDB.getQueryApi().query(parametrizedQuery, ORG_NAME, params);
//        query.forEach(fluxTable -> {
//            fluxTable.getRecords().forEach(r -> LOGGER.info("Record: " + r.getMeasurement() + ": " + r.getField() + ": " + r.getValueByKey("_value") + ": " + r.getValueByKey(TEST_RUN_ID)));
//        });

        boolean found = false;
        for (FluxTable table : query) {
            if (table.getColumns().stream().map(FluxColumn::getLabel)
                    .collect(Collectors.toList()).contains(TEST_RUN_ID)) {
                for (FluxRecord record : table.getRecords()) {
                    if (InfluxDbReporter.getTestRunId().equalsIgnoreCase(String.valueOf(record.getValueByKey(TEST_RUN_ID)))) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                break;
            }
        }
        Assert.assertTrue(found, "Test Run Id not found: " + InfluxDbReporter.getTestRunId() + " Device not found:" + InfluxDbReporter.getDeviceName());
    }

    public static synchronized void deleteAll() {
        Bucket bucket = INFLUXDB.getBucketsApi().findBucketByName(BUCKET_NAME);
        Organization org = INFLUXDB.getOrganizationsApi().findOrganizations().stream()
                .filter(o -> o.getName().equalsIgnoreCase(ORG_NAME)).findAny().orElse(null);
        LOGGER.info("bucket: " + bucket);
        LOGGER.info("org: " + org);
        DELETE_API.delete(OffsetDateTime.now().minusYears(2),
                OffsetDateTime.now().plusYears(2), "", bucket, org);
    }

    public static synchronized void refreshBucket() {
        if (getEnv() != ConfigProperties.Env.LOCAL) {
            return;
        }
        Bucket bucket = INFLUXDB.getBucketsApi().findBucketByName(BUCKET_NAME);
        Organization org = INFLUXDB.getOrganizationsApi().findOrganizations().stream()
                .filter(o -> o.getName().equalsIgnoreCase(ORG_NAME)).findAny().orElse(null);
        if (bucket != null) {
            LOGGER.info("delete bucket: " + bucket.getId());
            BUCKETS_API.deleteBucket(bucket.getId());
        }
        LOGGER.info("create bucket: " + BUCKET_NAME + " in org " + ORG_NAME);
        BUCKETS_API.createBucket(BUCKET_NAME, org.getId());
    }
}
