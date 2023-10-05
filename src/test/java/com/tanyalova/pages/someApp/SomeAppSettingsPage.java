package com.tanyalova.pages.someApp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static com.tanyalova.common.Utils.sleep;

public class SomeAppSettingsPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeAppSettingsPage.class);

    public SomeAppSettingsPage(WebDriver driver) {
        super(driver);
    }

    private static final String SETTINGS_HERO = "//android.widget.LinearLayout[@resource-id='com.:id/settings_nav'] " +
            "| //XCUIElementTypeStaticText[@name='DO NOT SELL OR SHARE MY PERSONAL INFO']";

    public By getSettingsHeroLocator() {
        return By.xpath(SETTINGS_HERO);
    }

    public WebElement getSignOutButton() {
        return driver.findElement(By.xpath("//android.widget.TextView[@resource-id='com.:id/auth_btn'] " +
                "| //XCUIElementTypeButton[@name='SIGN OUT']"));
    }

    public WebElement getSignInButtonInSettings() {
        return driver.findElement(By.xpath("//android.widget.TextView[@resource-id='com.:id/auth_btn'] " +
                "| //XCUIElementTypeButton[@name='SIGN IN']"));
    }

    public void settingsPageIsDisplayed(){
        LOGGER.info("START: settingsPageIsDisplayed");
        waitForElementDisplayed(getSettingsHeroLocator());
        LOGGER.info("END: settingsPageIsDisplayed");
    }
    public Instant settingsPageNavTime() {
        LOGGER.info("START: settingsPageNavTime");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getSettingsHeroLocator());
        LOGGER.info("END: settingsPageNavTime");
        return startTime;
    }

    public void clickOnSignOutAndSignIn() {
        LOGGER.info("clickOnSignOutAndSignIn START");
        try {
            if (isDisplayed(getSignOutButton())) {
                click(getSignOutButton());
                sleep(1);
                click(getSignInButtonInSettings());
            }
        }catch (Exception ignored){
            click(getSignInButtonInSettings());
        }
        LOGGER.info("clickOnSignOutAndSignIn END");
    }

}