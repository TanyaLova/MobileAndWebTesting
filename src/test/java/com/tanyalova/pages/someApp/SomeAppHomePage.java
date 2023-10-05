package com.tanyalova.pages.someApp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class SomeAppHomePage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeAppHomePage.class);

    public SomeAppHomePage(WebDriver driver) {
        super(driver);
    }

    private static final String HOME_HERO_PROMO_IMAGE = "//androidx.recyclerview.widget.RecyclerView[@resource-id='com.:id/list'] " +
            "| //XCUIElementTypeOther[@name='Vertical scroll bar, 4 pages']";

    public By getHomeHeroPromoImageLocator() {
        return By.xpath(HOME_HERO_PROMO_IMAGE);
    }

    public WebElement getMenuButton() {
        return driver.findElement(By.xpath("//XCUIElementTypeButton[@name='HOME']"));
    }

    public Instant homeNavTime() {
        LOGGER.info("START: homeNavTime");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getHomeHeroPromoImageLocator());
        LOGGER.info("END: homeNavTime");
        return startTime;
    }

    public void clickMenuButton() {
        LOGGER.info("clickMenuButton START");
        if (isAndroidDriver()) {
            clickOnScreen(49, 177);
        }else click(29,68);
        LOGGER.info("clickMenuButton END");
    }

}