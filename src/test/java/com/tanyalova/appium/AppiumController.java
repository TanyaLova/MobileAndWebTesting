package com.tanyalova.appium;


import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static com.tanyalova.common.Utils.sleep;
import static com.tanyalova.influxdb.InfluxDbReporter.getDeviceName;


public class AppiumController {
    public static final Logger LOGGER = LoggerFactory.getLogger(AppiumController.class);

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static AppiumDriverLocalService appiumDriverLocalService = null;
    private static boolean isAppiumServerStarted = false;
    private static Process processAppiumServer;
    private static Process processWebDriverAgent;
    private static final String RUN_WEB_DRIVER_AGENT = "xcodebuild build-for-testing test-without-building -project /Users/name/.appium/node_modules/appium-xcuitest-driver/node_modules/appium-webdriveragent/WebDriverAgent.xcodeproj -scheme WebDriverAgentRunner -destination 'id=%s' -allowProvisioningUpdates";
    public static final String IOS_DEVICE_ID = "00008110-000C58E40201801E";

    public enum OS {
        ANDROID,
        IOS,
        CHROME
    }

    public static AppiumController instance = new AppiumController();

    public void startAppiumServer() {
        if (isAppiumServerStarted) {
            return;
        }
        appiumDriverLocalService = AppiumDriverLocalService
                .buildDefaultService();
        appiumDriverLocalService.start();
    }


    public synchronized void start(OS executionOS) {
        if (getDriver() != null) {
            return;
        }
        DesiredCapabilities capabilities = new DesiredCapabilities();
        switch (executionOS) {
            case ANDROID:
                capabilities.setCapability("platformName", "Android");
                capabilities.setCapability("automationName", "UiAutomator2");
                capabilities.setCapability("platformVersion", "13.0");
                capabilities.setCapability("deviceName", "Samsung S22");
                capabilities.setCapability("udid", "R5CRC31XS9R");
                //capabilities.setCapability("appPackage", "com.hbo.hbonow");
                //capabilities.setCapability("appActivity", "com.hbo.hbonow.MainActivity");
                driver.set(new AndroidDriver(createURL(), capabilities));
                //driver.set(new AndroidDriver(createAppiumServiceURL(), capabilities));
                break;
            case IOS:
                capabilities.setCapability("platformName", "iOS");
                capabilities.setCapability("platformVersion", "16.5");
                capabilities.setCapability("deviceName", "iPhone (8)");
                capabilities.setCapability("automationName", "XCUITest");
                capabilities.setCapability("appium:udid", IOS_DEVICE_ID);
                capabilities.setCapability("appium:xcodeOrgId", "F7NQ2AZ57X");//"6X5W2YMR37"
                capabilities.setCapability("appium:xcodeSigningId", "Apple Development");
                capabilities.setCapability("appium:clearSystemFiles", "true");
                capabilities.setCapability("appium:noReset", "true");
//                capabilities.setCapability("appium:bundleid", "com.hbo.hbonow");
                capabilities.setCapability("shouldTerminateApp", "true");
                //capabilities.setCapability("app", app.getAbsolutePath());
                driver.set(new IOSDriver(createURL(), capabilities));
                //driver.set(new IOSDriver(createAppiumServiceURL(), capabilities));
                break;
            case CHROME:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setCapability("se:recordVideo", true);
//                chromeOptions.setCapability("browserName", "chrome");
//                chromeOptions.setCapability("platformName", "MacOS");
//                chromeOptions.setCapability("appium:automationName", "Chrome");
                sleep(5);
                boolean isDriverSet = setRemoteWebDriver("http://localhost:4444", chromeOptions);
                if (!isDriverSet) {
                    Assert.fail("Couldn't set Remote Web Driver! See logs");
                }
                getDriver().get("https://www.google.com");
        }
        driver.get().manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }

    private boolean setRemoteWebDriver(String url, ChromeOptions chromeOptions) {
        try {
            driver.set(new RemoteWebDriver(new URL(url), chromeOptions));
        } catch (Exception e) {
            sleep(5);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private URL createURL() {
        try {
            return new URL("http://127.0.0.1:4723/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public synchronized void afterClass() {
        LOGGER.info("afterClass: " + getDeviceName());
        if (isAppiumServerStarted) {
            LOGGER.info("Destroy Appium Server!");
            processAppiumServer.destroyForcibly();
            processWebDriverAgent.destroyForcibly();
        }
    }

    public synchronized void stopDriver() {
        if (getDriver() != null) {
            LOGGER.info("WebDriver quit start");
            getDriver().quit();
            driver.set(null);
        }
    }

    public synchronized WebDriver getDriver() {
        return driver.get();
    }

    public synchronized void terminateApp(String appPackage) {
        LOGGER.info("terminateApp start: " + appPackage);
        if (getDriver() instanceof AndroidDriver) {
            ((AndroidDriver) getDriver()).terminateApp(appPackage);
            ((AndroidDriver) getDriver()).terminateApp("com.android.chrome");
            ((AndroidDriver) getDriver()).terminateApp("com.android.vending");
        } else if (getDriver() instanceof IOSDriver) {
            ((IOSDriver) getDriver()).terminateApp(appPackage);
            ((IOSDriver) getDriver()).terminateApp("com.apple.AppStore");
        } else {
        }
    }

    public boolean runWebDriverAgent(String iosDeviceId) {
        LOGGER.info("Start webDriverAgent on device with id=" + iosDeviceId);
        String command = String.format(RUN_WEB_DRIVER_AGENT, iosDeviceId);
        boolean isRunning = false;
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash");
            builder.command("sh", "-c", command);
            builder.redirectErrorStream(true);
            processWebDriverAgent = builder.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processWebDriverAgent.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    LOGGER.info(line);
                    if (line.contains("TEST BUILD SUCCEEDED")) {
                        isRunning = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            LOGGER.info(e.toString());
            e.printStackTrace();
        }
        return isRunning;
    }
}