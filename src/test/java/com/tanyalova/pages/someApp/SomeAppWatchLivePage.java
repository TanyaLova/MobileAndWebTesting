package com.tanyalova.pages.someApp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class SomeAppWatchLivePage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeAppWatchLivePage.class);

    public SomeAppWatchLivePage(WebDriver driver) {
        super(driver);
    }

    private static final String WATCH_LIVE_IMAGE = "//android.widget.ScrollView | //XCUIElementTypeScrollView";

    public By getWatchLiveImageLocator() {
        return By.xpath(WATCH_LIVE_IMAGE);
    }

    public WebElement getWatchLiveImage() {
        return driver.findElement(By.xpath(WATCH_LIVE_IMAGE));
    }

    public WebElement getIsPlayingNow() {
        return driver.findElement(By.xpath("//android.widget.FrameLayout[@resource-id='com.:id/now']"));
    }

    public Instant watchLiveNavTime() {
        LOGGER.info("START: watchLiveNavTime");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getWatchLiveImageLocator());
        LOGGER.info("END: watchLiveNavTime");
        return startTime;
    }

    public void clickIsPlayingNow() {
        LOGGER.info("clickIsPlayingNow START");
        if (isAndroidDriver()) {
            click(getIsPlayingNow());
        } else {
            click(176, 176);
        }
        LOGGER.info("clickIsPlayingNow END");
    }

    public void watchLiveIsDisplayed() {
        LOGGER.info("START: watchLiveIsDisplayed");
        waitForElementDisplayed(getWatchLiveImageLocator());
        LOGGER.info("END: watchLiveIsDisplayed");
    }

}