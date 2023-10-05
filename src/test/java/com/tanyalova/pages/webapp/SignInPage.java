package com.tanyalova.pages.webapp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import static com.tanyalova.common.Constants.LOGIN;
import static com.tanyalova.common.Constants.PASSWORD;


public class SignInPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SignInPage.class);

    public SignInPage(WebDriver driver) {
        super(driver);
    }

    public WebElement getShadowElement() {
        try {
            return driver.findElement(By.xpath("//gi-login-username-and-mvpd"));
        } catch (StaleElementReferenceException e) {
            LOGGER.debug("getShadowElement StaleElementReferenceException error: " + e);
            return driver.findElement(By.xpath("//gi-login-username-and-mvpd"));
        }
    }

    private WebElement getShadowRootElement() {
        try {
            return getShadowElement().getShadowRoot().findElement(By.cssSelector("gi-login-username")).getShadowRoot()
                    .findElement(By.cssSelector("gi-track-analytics-events"));
        } catch (StaleElementReferenceException e) {
            LOGGER.debug("getShadowRootElement StaleElementReferenceException error: " + e);
            return getShadowElement().getShadowRoot().findElement(By.cssSelector("gi-login-username")).getShadowRoot()
                    .findElement(By.cssSelector("gi-track-analytics-events"));
        }
    }

    public boolean isDisplayedShadow(By element) {
        LOGGER.debug("START: isElementDisplayed: " + element);
        try {
            return getShadowRootElement().findElement(element).isDisplayed();
        } catch (Exception e) {
            LOGGER.debug("END: isElementDisplayed: " + element + ". Error: " + e);
            return false;
        }
    }

    public WebElement waitForElementDisplayedShadow(By element) {
        LOGGER.debug("START: waitForElementDisplayedShadow: " + element);
        if (element != null) {
            new WebDriverWait(this.driver, Duration.ofSeconds(20))
                    .until(driver -> isDisplayedShadow(element));
        }
        WebElement e = getShadowRootElement().findElement(element);
        LOGGER.debug("END: waitForElementDisplayedShadow: " + element);
        return e;
    }

    public By getLoginInputLocator() {
        return By.cssSelector("input#login-username-input");
    }

    public WebElement getLoginInput() {
        return getShadowRootElement().findElement(getLoginInputLocator());
    }

    public WebElement getPasswordInput() {
        return getShadowRootElement().findElement(By.cssSelector("input#login-password-input"));
    }

    public WebElement getSignInButton() {
        return getShadowRootElement().findElement(
                By.cssSelector("button[data-testid='gisdk.gi-login-username.signIn_button']"));
    }

    public WebElement getProfile() {
        return driver.findElement(By.xpath("//button[contains(@aria-label,'Profile')]"));
    }

    public By getProfileLocator() {
        return By.xpath("//button[contains(@aria-label,'Profile')]");
    }

    public Instant waitForLoadingSignInPage() {
        LOGGER.info("waitForLoadingSignInPage START");
        Instant startTime = Instant.now();
        waitForElementDisplayedShadow(getLoginInputLocator());
        LOGGER.info("waitForLoadingSignInPage END");
        return startTime;
    }

    public Instant waitForSignInComplete() {
        LOGGER.info("waitForSignInComplete START");
        type(getLoginInput(), LOGIN);
        type(getPasswordInput(), PASSWORD);
        click(getSignInButton(), true);
        Instant startTime = Instant.now();
        waitForElementDisplayed(getProfileLocator());
        LOGGER.info("waitForSignInComplete END");
        return startTime;
    }

    public void clickOnProfile() {
        LOGGER.info("Click on profile START");
        click(getProfile(), true);
        LOGGER.info("Click on profile END");
    }
}
