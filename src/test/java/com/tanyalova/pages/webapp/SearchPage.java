package com.tanyalova.pages.webapp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;


public class SearchPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SearchPage.class);

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    public By getSearchHeroLocator() {
        return By.xpath("//section[@data-testid = 'search-page-rail-warm-start-recommended_rail']");
    }

    public WebElement getHouseImage() {
        return driver.findElement(getHouseLocator());
    }

    public By getHouseLocator() {
        return By.xpath("//a[contains(@aria-label,'House.')]");
    }

    public WebElement getSearchField() {
        return driver.findElement(getSearchFieldLocator());
    }

    public By getSearchFieldLocator() {
        return By.xpath("//input[@data-testid ='searchBar_field']");
    }

    public Instant waitForLoadingSearchPage() {
        LOGGER.info("waitForLoadingSearchPage START");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getSearchHeroLocator());
        waitForElementDisplayed(getSearchFieldLocator());
        LOGGER.info("waitForLoadingSearchPage END");
        return startTime;
    }

    public Instant typeAndSearchContent() {
        LOGGER.info("typeAndSearchContent START");
        type(getSearchField(), "house");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getHouseLocator());
        LOGGER.info("typeAndSearchContent END");
        return startTime;
    }

    public void clickOnHouseImage() {
        LOGGER.info("clickOnHouseImage START");
        click(getHouseImage(), true);
        LOGGER.info("clickOnHouseImage END");
    }
}
