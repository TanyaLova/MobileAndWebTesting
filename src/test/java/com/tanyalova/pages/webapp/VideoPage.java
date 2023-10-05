package com.tanyalova.pages.webapp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static com.tanyalova.common.Utils.sleep;


public class VideoPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(VideoPage.class);

    public VideoPage(WebDriver driver) {
        super(driver);
    }

    public WebElement getRatingLabel() {
        return driver.findElement(By.xpath("//span[@data-testid='player-ux-ratings-label']"));
    }

    private WebElement getBackButton() {
        return driver.findElement(By.xpath("//div[@data-testid='playback_controls' and contains(@style, 'visible')]" +
                "//button[@data-testid='player-ux-back-button']"));
    }

    public Instant waitVideoBufferingPage() {
        LOGGER.info("waitVideoBufferingPage START");
        Instant startTime = Instant.now();
        waitForTextToBe(By.xpath("//span[@data-testid='player-ux-ratings-label']"), "RATED");
        LOGGER.info("waitVideoBufferingPage END");
        return startTime;
    }

    public void clickExitFromVideo() {
        LOGGER.info("clickExitFromVideo START");
        sleep(3);
        actions.moveByOffset(500, 500).click().build().perform();
        click(getBackButton(), true);
        LOGGER.info("clickExitFromVideo END");
    }
}
