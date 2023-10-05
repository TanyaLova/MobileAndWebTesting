package com.tanyalova.pages.someApp;

import com.tanyalova.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class SignInListPage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SignInListPage.class);

    public SignInListPage(WebDriver driver) {
        super(driver);
    }

    private static final String SIGN_IN_PROMO_LIST = "//androidx.recyclerview.widget.RecyclerView[@resource-id='com.:id/primary_picker_recycler_view'] " +
            "| //XCUIElementTypeOther[@name='Vertical scroll bar, 2 pages']";

    public By getSignInPromoListLocator() {
        return By.xpath(SIGN_IN_PROMO_LIST);
    }

    private static final String SIGN_IN_AGAIN = "//android.widget.TextView[@resource-id='com.:id/sign_in_text2'] " +
            "| //XCUIElementTypeStaticText[@name='Sign-in to your TV service provider']";

    public By getSignInAgainLocator() {
        return By.xpath(SIGN_IN_AGAIN);
    }

    public WebElement getContinueLogin() {
        return driver.findElement(By.xpath("//android.widget.Button[@resource-id='com.:id/loginButton'] " +
                "| //XCUIElementTypeButton[@name='Continue to LogIn']"));
    }

    public WebElement getProvider() {
        return driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc='Provider']"));
    }
    public By getAnotherAccountLocator() {
        return By.xpath("//android.widget.Button[@text='Sign in with another account'] | //XCUIElementTypeButton[@name='Sign in with another account']");
    }
    public WebElement getAnotherAccount() {
        return driver.findElement(By.xpath("//android.widget.Button[@text='Sign in with another account'] | //XCUIElementTypeButton[@name='Sign in with another account']"));
    }

    public Instant signInPageNavTime() {
        LOGGER.info("START: signInPageNavTime");
        Instant startTime = Instant.now();
        try {
            waitForElementDisplayed(getSignInAgainLocator());
        } catch (Exception ignored) {
            waitForElementDisplayed(getSignInPromoListLocator());
        }
        LOGGER.info("END: signInPageNavTime");
        return startTime;
    }

    public void clickOnProvider() {
        LOGGER.info("clickOnProvider START");
        if (isDisplayed(getContinueLogin())) {
            click(getContinueLogin());
        } else if (isAndroidDriver()) {
            click(getProvider());
        } else {
            click(190, 715);
        }
        try {
            waitForElementDisplayedShortTime(getAnotherAccountLocator());
        }catch (Exception ignored){
        }
        if (isDisplayed(getAnotherAccount())){
            click(getAnotherAccount());
        }
        LOGGER.info("clickOnProvider END");
    }

}