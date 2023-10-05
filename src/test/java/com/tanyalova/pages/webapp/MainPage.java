package com.tanyalova.pages.webapp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;


public class MainPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(MainPage.class);

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public WebElement getSignInButton() {
        return driver.findElement(By.xpath("//div[contains(@class,'d-sm-flex')]//a[@href='/login']"));
    }

    public By getMainPageHero() {
        return By.xpath("//div[contains(@class,'init aos-animate')]");
    }

    public Instant waitForLoadingMainPage() {
        LOGGER.info("waitForLoadingMainPage START");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getMainPageHero());
        LOGGER.info("waitForLoadingMainPage END");
        return startTime;
    }

    public void clickOnSignInButton() {
        LOGGER.info("ClickOnSignInButton START");
        click(getSignInButton(), true);
        LOGGER.info("ClickOnSignInButton END");
    }
}
