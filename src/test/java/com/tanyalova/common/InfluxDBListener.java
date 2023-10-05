package com.tanyalova.common;

import org.apache.logging.log4j.core.LogEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import static com.tanyalova.influxdb.InfluxDbReporter.*;


public class InfluxDBListener extends LogEventListener implements ITestListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBListener.class);


    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getDescription();
        if (name == null || name.isEmpty()) {
            name = result.getMethod().getMethodName();
        }
        setMethodName(name);
    }


    public void onTestSuccess(ITestResult iTestResult) {
        //this.postTestMethodStatus(iTestResult, Status.PASSED);
    }

    public void onTestFailure(ITestResult iTestResult) {
        setFailedTestCounter(getFailedTestCounter() + 1);

    }

    public void onTestSkipped(ITestResult iTestResult) {
        //this.postTestMethodStatus(iTestResult, Status.SKIPPED);
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
    }

    public void onStart(ITestContext iTestContext) {
    }

    public void onFinish(ITestContext iTestContext) {
    }

}
