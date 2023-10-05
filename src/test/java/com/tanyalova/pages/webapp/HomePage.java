package com.tanyalova.pages.webapp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;


public class HomePage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public By getHomeHero() {
        return By.xpath("//div[contains(@class,'StyledImmersiveAndTakeoverHero')]");
    }

    public WebElement getSearchButton() {
        return driver.findElement(By.xpath("//a[@data-testid = 'search_button']"));
    }


    public Instant waitForLoadingHomePage() {
        LOGGER.info("waitForLoadingHomePage START");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getHomeHero());
        LOGGER.info("waitForLoadingHomePage END");
        return startTime;
    }

    public void clickSearchButton() {
        LOGGER.info("clickSearchButton START");
        click(getSearchButton(), true);
        LOGGER.info("clickSearchButton END");
    }
}
