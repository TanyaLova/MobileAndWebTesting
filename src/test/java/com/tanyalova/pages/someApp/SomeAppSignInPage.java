package com.tanyalova.pages.someApp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static com.tanyalova.common.Constants.*;

public class SomeAppSignInPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeAppSignInPage.class);

    public SomeAppSignInPage(WebDriver driver) {
        super(driver);
    }

    private static final String SIGN_IN_HERO = "//android.view.View[@resource-id='root'] | //XCUIElementTypeApplication[@name='SomeApp']/XCUIElementTypeWindow[2]";

    public By getSignInHeroLocator() {
        return By.xpath(SIGN_IN_HERO);
    }

    private static final String LOGIN_INPUT = "//android.widget.EditText[@resource-id='username-input'] " +
            "| //XCUIElementTypeOther[@name='main']/XCUIElementTypeOther[4]/XCUIElementTypeTextField";

    public WebElement getLoginInput() {
        return driver.findElement(By.xpath(LOGIN_INPUT));
    }

    private static final String PASSWORD_INPUT = "//android.widget.EditText[@resource-id='password-input'] " +
            "| //XCUIElementTypeOther[@name='main']/XCUIElementTypeOther[4]/XCUIElementTypeSecureTextField";

    public WebElement getPasswordInput() {
        return driver.findElement(By.xpath(PASSWORD_INPUT));
    }

    public By getPasswordInputLocator() {
        return By.xpath(PASSWORD_INPUT);
    }

    private static final String SIGN_IN_BUTTON = "//android.widget.Button[@text='Sign In'] | //XCUIElementTypeButton[@name='Sign In']";

    public WebElement getSignInButton() {
        return driver.findElement(By.xpath(SIGN_IN_BUTTON));
    }

    public Instant signInPageNavTime() {
        LOGGER.info("START: SignInPageNavTime");
        Instant startTime = Instant.now();
        waitForElementDisplayed(getPasswordInputLocator());
        LOGGER.info("END: SignInPageNavTime");
        return startTime;
    }

    public void typeCredentials() {
        LOGGER.info("typeCredentials START");
        type(getPasswordInput(), PASSWORD);
        type(getLoginInput(), LOGIN);
        if (isIOSDriver()) {
            hideKeyboardIOS();
        } else click(getSignInButton());
        waitForElementNotDisplayed(getPasswordInputLocator());
        LOGGER.info("typeCredentials END");
    }

}