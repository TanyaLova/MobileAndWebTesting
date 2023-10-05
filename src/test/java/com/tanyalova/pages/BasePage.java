package com.tanyalova.pages;

import com.tanyalova.appium.AppiumController;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.functions.ExpectedCondition;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.remote.HideKeyboardStrategy;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import static com.tanyalova.common.Utils.calculateTime;
import static com.tanyalova.influxdb.InfluxDbReporter.*;

public class BasePage {

    public static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;


    public BasePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        actions = new Actions(driver);
    }

    private WebElement randomElement;
    private static final String RANDOM_ELEMENT = "//*[@clickable='true'] | //*[@clickable='true']";

    public WebElement getRandomElement() {
        return getWebElement(RANDOM_ELEMENT);
    }

    public void clickViaActions() {
        clickViaActions(randomElement);
    }

    public void typeViaActions(String text) {
        LOGGER.info("START: typeViaActions" + text);
        actions.sendKeys(text).perform();
        LOGGER.info("END: typeViaActions" + text);
    }

    public void click(int x, int y) {
        LOGGER.debug("START: Click on element: " + x + ", " + y);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence click = new Sequence(finger, 0);
        click.addAction(finger.createPointerMove(Duration.ofSeconds(1), PointerInput.Origin.viewport(), x, y));
        click.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
        if (driver instanceof AppiumDriver) {
            ((AppiumDriver) driver).perform(Arrays.asList(click));
        }
        LOGGER.debug("END: Click on element: " + x + ", " + y);
    }

    public void clickViaActions(WebElement element) {
        LOGGER.debug("START: Click on element: " + element);
        if (driver instanceof AppiumDriver) {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence click = new Sequence(finger, 0);
            Point point = element.getLocation();
            click.addAction(finger.createPointerMove(Duration.ofSeconds(1), PointerInput.Origin.viewport(), point.x, point.y));
            click.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));

            ((AppiumDriver) driver).perform(Arrays.asList(click));
        } else {
            actions.click(element).perform();
        }
        LOGGER.debug("END: Click on element: " + element);
    }

    public void click(WebElement element) {
        LOGGER.debug("START: Click on element: " + element);
        click(element, true);
        LOGGER.debug("END: Click on element: " + element);
    }

    public void click(WebElement element, boolean waitForElement) {
        LOGGER.debug("START: Click on element: " + element);
        if (waitForElement) {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } else {
            element.click();
        }
        LOGGER.debug("END: Click on element: " + element);
    }

    public boolean isDisplayed(WebElement element) {
        LOGGER.debug("START: isElementDisplayed: " + element);
        try {
            return element.isDisplayed();
        } catch (Exception ignored) {
            LOGGER.debug("END: isElementDisplayed: " + element);
            return false;
        }
    }

    public boolean isDisplayed(By element) {
        LOGGER.debug("START: isElementDisplayed: " + element);
        try {
            return driver.findElement(element).isDisplayed();
        } catch (Exception ignored) {
            LOGGER.debug("END: isElementDisplayed: " + element);
            return false;
        }
    }

    public void waitForTextToBe(By locator, String text) {
        LOGGER.debug("START: waitForTextToBe: " + locator);
        wait.until(ExpectedConditions.textToBe(locator, text));
        LOGGER.debug("END: waitForTextToBe: " + locator);
    }

    public void waitForElement(By locator) {
        LOGGER.debug("START: waitForElement: " + locator);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        LOGGER.debug("END: waitForElement: " + locator);
    }
    public void waitForElementVisible(By locator) {
        LOGGER.debug("START: waitForElementInvisible: " + locator);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        LOGGER.debug("END: waitForElementInvisible: " + locator);
    }

    public WebElement waitForElementTextNotContained(WebElement element, String textPart) {
        if (element != null) {
            try {
                ExpectedCondition<Boolean> textCondition = driver -> {
                    String text = "";
                    try {
                        text = element.getText();
                    } catch (Exception ignored) {
                    }
                    return !text.contains(textPart);
                };
                new WebDriverWait(this.driver, Duration.ofSeconds(10)).until(textCondition);
            } catch (Exception ignored) {
            }
        }
        return element;
    }
    public WebElement waitForElementDisplayedShortTime(By element) {
        if (element != null) {
            new WebDriverWait(this.driver, Duration.ofSeconds(10))
                    .until(driver -> isDisplayed(element));
        }
        return driver.findElement(element);
    }

    public WebElement waitForElementDisplayed(By element) {
        if (element != null) {
            new WebDriverWait(this.driver, Duration.ofSeconds(60))
                    .until(driver -> isDisplayed(element));
        }
        return driver.findElement(element);
    }

    public WebElement waitForElementDisplayed(WebElement element) {
        if (element != null) {
            new WebDriverWait(this.driver, Duration.ofSeconds(20))
                    .until(driver -> isDisplayed(element));
        }
        return element;
    }

    public void waitForElementNotDisplayed(By element) {
        LOGGER.debug("waitForElementNotDisplayed START");
        if (element != null) {
            new WebDriverWait(this.driver, Duration.ofSeconds(62))
                    .until(driver -> !isDisplayed(element));
        }
        LOGGER.debug("waitForElementNotDisplayed END");
    }

    public void waitForElementNotDisplayed(WebElement element) {
        LOGGER.debug("waitForElementNotDisplayed START");
        if (element != null) {
            new WebDriverWait(this.driver, Duration.ofSeconds(15))
                    .until(driver -> !isDisplayed(element));
        }
        LOGGER.debug("waitForElementNotDisplayed START");
    }

    public WebElement getWebElement(String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    public WebElement getWebElement(By by) {
        LOGGER.debug("START: getWebElement By: " + by);
        WebElement element = driver.findElement(by);
        LOGGER.debug("END: getWebElement By: " + by);
        return element;
    }

    public WebElement getWebElementByName(String name) {
        LOGGER.debug("START: getWebElementByName, name: " + name);
        WebElement element = driver.findElement(By.name(name));
        LOGGER.debug("END: getWebElementByName, name: " + name);
        return element;
    }

    public WebElement getWebElementByAccessibility(String accessibilityId) {
        LOGGER.debug("START: getWebElementByAccessibility, accessibilityId: " + accessibilityId);
        WebElement element = driver.findElement(getLocatorByAccessibilityId(accessibilityId));
        LOGGER.debug("END: getWebElementByAccessibility, accessibilityId: " + accessibilityId);
        return element;
    }

    public By getLocatorByAccessibilityId(String accessibilityId) {
        return AppiumBy.ByAccessibilityId.accessibilityId(accessibilityId);
    }

    public void type(WebElement element, String text) {
        LOGGER.debug("START: type: " + text + " into element: " + element);
        element.sendKeys(text);
        LOGGER.debug("END: type: " + text + " into element: " + element);
    }

    public void sendTextViaActions(WebElement element, String text) {
        actions.sendKeys(element, text).perform();
    }

    public void hideKeyboard() {
        LOGGER.debug("START: hideKeyboard");
        if (isAndroidDriver()) {
            ((AndroidDriver) driver).hideKeyboard();
        } else {
            ((IOSDriver) driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "search");
        }
        LOGGER.debug("END: hideKeyboard");
    }
    public void hideKeyboardIOS(){
        ((IOSDriver)driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY,"return");
    }

    public void tap() {
        if (isAndroidDriver()) {
            click(getRandomElement(), false);
        } else {
            click(220, 220);
        }
    }
    public void clickOnScreen(int x, int y){
        TouchAction tc = new TouchAction((PerformsTouchActions) driver);
        tc.tap(PointOption.point(x, y)).perform();
    }

    public void clickBackButton() {
        ((AndroidDriver) driver).pressKey(new KeyEvent().withKey(AndroidKey.BACK));
    }
    public void clickEnterButton(){
        ((AndroidDriver) driver).pressKey(new KeyEvent().withKey(AndroidKey.ENTER));
    }

    public void clickBack() {
        driver.navigate().back();
    }

    public File screenshot() {
        String platform = getPlatform().toString();
        String imagesLocation = "target/surefire-reports/screenshot/" + platform + "/";
        new File(imagesLocation).mkdirs();
        String filename = platform + "-" + UUID.randomUUID() + "-" + Instant.now().toString().replace(":", "-");
        String path = imagesLocation + filename + ".png";

        File targetFile = null;
        try {
            File srcFile = driver instanceof AppiumDriver
                    ? ((AppiumDriver) driver).getScreenshotAs(OutputType.FILE)
                    : ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            targetFile = new File(path);
            FileUtils.copyFile(srcFile, targetFile, true);
            LOGGER.debug("The screenshot is copied to: " + targetFile.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("Error capturing screen shot of " + path + " test failure. Message: " + e.getMessage());
            e.printStackTrace();
        }
        return targetFile;
    }

    public void logKpi(String message, Instant startTime) {
        logKpi(message, startTime, true);
    }

    public void logKpi(String message, Instant startTime, boolean takeScreenshot) {
        long kpi = calculateTime(startTime);
        File file = null;
        if (takeScreenshot) {
            file = screenshot();
        }
        LOGGER.info(message + ": " + kpi);
        setKpi(getMethodName(), kpi);
        setScreenshot(getMethodName(), file == null ? null : getUrl(file));
        //TODO setScreenshot(getMethodName(), new String(readFileToBytes(file), StandardCharsets.UTF_8));
        //decodeVideo(path);
    }

    public boolean isAndroidDriver() {
        return driver instanceof AndroidDriver;
    }

    public boolean isIOSDriver() {
        return driver instanceof IOSDriver;
    }

    public boolean isWebDriver() {
        return driver instanceof ChromeDriver;
    }

    public AppiumController.OS getPlatform() {
        if (isAndroidDriver()) {
            return AppiumController.OS.ANDROID;
        } else if (isIOSDriver()) {
            return AppiumController.OS.IOS;
        } else {
            return AppiumController.OS.CHROME;
        }
    }

    public synchronized void startRecordingScreen() {
        LOGGER.info("Start Video recording");
        if (isAndroidDriver()) {
            ((AndroidDriver) driver).startRecordingScreen();
        } else if (isIOSDriver()) {
            ((CanRecordScreen) (driver)).startRecordingScreen(new IOSStartScreenRecordingOptions()
                    .withFps(25)
                    .withVideoScale("320:-2")
                    .withVideoType("h264") // require ffmpeg
                    .withTimeLimit(Duration.ofMinutes(10)));
        } else {
            //DO NOTHING for Web becauce it's done by docker compose
        }
    }

    public synchronized String stopRecordingScreen() {
        LOGGER.info("Stop Video recording");
        if (isAndroidDriver()) {
            return ((AndroidDriver) driver).stopRecordingScreen();
        } else if (isIOSDriver()) {
            return ((IOSDriver) driver).stopRecordingScreen();
        } else {
            return null;
        }
    }

    public synchronized void decodeVideo(String encodedVideoString) {
        if (encodedVideoString == null) {
            return;
        }
        String platform = getPlatform().toString();
        String videoLocation = "target/surefire-reports/video/" + platform + "/";
        new File(videoLocation).mkdirs();
        String filename = platform + "-" + getTestRunId() + "-" + Instant.now().toString().replace(":", "-");
        String path = videoLocation + filename + ".mp4";
        byte[] decode = Base64.getDecoder().decode(encodedVideoString);
        File file = new File(path);
        try {
            FileUtils.writeByteArrayToFile(file, decode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String pathVideo = getUrl(file);
        LOGGER.info("Decode video: " + pathVideo);
        setVideoStorage(pathVideo);
    }

    private String getUrl(File file) {
        String userHome = System.getProperty("user.home");
        return "http://localhost:8000" + file.getAbsolutePath().replace(userHome, "");
    }

    public void runScript() {
        LOGGER.info("START runScript");
        String scriptPath = "src/test/resources/runCommands.sh";
        ProcessBuilder pb = new ProcessBuilder("/bin/bash");
        try {
            Process p;
            pb.command("sh","-c",scriptPath);
            p = pb.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    LOGGER.info("Line: " + line);
                    LOGGER.info("Output: " + bufferedReader.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized File saveEncodedVideo(String video) {
        String platform = getPlatform().toString();
        String videoLocation = "target/surefire-reports/video/" + platform + "/encoded/";
        new File(videoLocation).mkdirs();
        String filename = platform + "-" + UUID.randomUUID() + "-" + Instant.now().toString().replace(":", "-");
        String path = videoLocation + filename + ".txt";
        File file = new File(path);
        try {
            FileUtils.writeByteArrayToFile(file, video.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}