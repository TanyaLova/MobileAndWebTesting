package com.tanyalova.pages.someApp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class SomeAppVideoPlayerPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeAppVideoPlayerPage.class);

    public SomeAppVideoPlayerPage(WebDriver driver) {
        super(driver);
    }

    private static final String BACK_BUTTON = " " +
            "| //XCUIElementTypeApplication[@name='SomeApp']/XCUIElementTypeWindow[2]/XCUIElementTypeOther[2]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther[2]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeButton[1]";

    public By getPausePlayButtonLocator() {
        return By.id("com.:id/play_btn");
    }

    public WebElement getBackButton() {
        return driver.findElement(By.xpath(BACK_BUTTON));
    }

    public By getLoadingImageLocator() {
        return By.xpath("//android.widget.FrameLayout[@resource-id='com.:id/load_animation'] | //XCUIElementTypeApplication[@name='SomeApp']/XCUIElementTypeWindow[1]/XCUIElementTypeOther[2]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther[2]/XCUIElementTypeOther[2]/XCUIElementTypeImage");
    }

    public By getTextLiveLocator() {
        return By.xpath("//android.widget.TextView[@resource-id='com.:id/time_txt'] " +
                "| //XCUIElementTypeStaticText[@name='LIVE']");
    }

    public Instant videoBufferingTime() {
        LOGGER.info("START: videoBufferingTime");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getTextLiveLocator());
        waitForElementNotDisplayed(getLoadingImageLocator());
        LOGGER.info("END: videoBufferingTime");
        return startTime;
    }

    public void clickBackButtonInVideo() {
        LOGGER.info("clickBackButtonInVideo START");
        if (isAndroidDriver()) {
            clickBackButton();
        } else {
            //click(getBackButton());
            clickOnScreen(39, 32);
        }
        LOGGER.info("clickBackButtonInVideo END");
    }

}