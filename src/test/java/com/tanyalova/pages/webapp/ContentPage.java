package com.tanyalova.pages.webapp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;


public class ContentPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(ContentPage.class);

    public ContentPage(WebDriver driver) {
        super(driver);
    }

    public By getHouseHeroLocator() {
        return By.xpath("//img[@alt= 'House']");
    }

    public WebElement getRestartButton() {
        return driver.findElement(By.xpath("//a[@data-testid='restart_button']"));
    }

    public WebElement getPlayButton() {
        return driver.findElement(By.xpath("//a[@data-testid='play_button']"));
    }

    public Instant waitLoadingContentPage() {
        LOGGER.info("waitLoadingContentPage START");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getHouseHeroLocator());
        LOGGER.info("waitLoadingContentPage END");
        return startTime;
    }

    public void clickRestart() {
        LOGGER.info("ClickRestart START");
        if (getRestartButton().isDisplayed()) {
            click(getRestartButton(), false);
        } else {
            click(getPlayButton());
        }
        LOGGER.info("ClickRestart END");
    }

    public Instant waitVideoInitialPlayer() {
        LOGGER.info("waitVideoInitialPlayer START");
        Instant startTime = Instant.now();
        waitForElementNotDisplayed(getHouseHeroLocator());
        LOGGER.info("waitVideoInitialPlayer END");
        return startTime;
    }

    public Instant waitExitFromVideo() {
        LOGGER.info("waitExitFromVideo START");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getHouseHeroLocator());
        LOGGER.info("waitExitFromVideo END");
        return startTime;
    }
}
