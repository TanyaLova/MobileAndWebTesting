package com.tanyalova.pages.someApp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class SomeAppMenuPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeAppMenuPage.class);

    public SomeAppMenuPage(WebDriver driver) {
        super(driver);
    }

    private static final String WATCH_LIVE_BUTTON ="//android.widget.FrameLayout[@resource-id='com.:id/live_btn'] " +
            "| //XCUIElementTypeButton[@name='WATCH LIVE']";

    public By getWatchLiveButtonLocator() {
        return By.xpath(WATCH_LIVE_BUTTON);
    }
    public WebElement getWatchLiveButton(){
        return driver.findElement(By.xpath(WATCH_LIVE_BUTTON));
    }

    public WebElement getSettingsButton(){
        return driver.findElement(By.xpath("//android.widget.FrameLayout[@resource-id='com.:id/settings_btn'] " +
                "| //XCUIElementTypeButton[@name='SETTINGS']"));
    }

    public Instant waitMenuNavTime(){
        LOGGER.info("START: waitMenuNavTime");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getWatchLiveButtonLocator());
        LOGGER.info("END: waitMenuNavTime");
        return startTime;
    }
    public void clickWatchLiveButton() {
        LOGGER.info("clickWatchLiveButton START");
        click(getWatchLiveButton());
        LOGGER.info("clickWatchLiveButton END");
    }
    public void clickSettingsButton() {
        LOGGER.info("clickSettingsButton START");
        click(getSettingsButton());
        LOGGER.info("clickSettingsButton END");
    }

}