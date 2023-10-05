package com.tanyalova.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SystemHomePage extends BasePage {
    public static final Logger LOGGER = LoggerFactory.getLogger(SystemHomePage.class);


    public SystemHomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc='SomeApp']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeIcon[@name='SomeApp']")
    private WebElement someAppIcon;

    public void waitForSomeAppIconAndClick() {
        LOGGER.info("waitForSomeAppIconAndClick START");
        wait.until(ExpectedConditions.elementToBeClickable(someAppIcon)).click();
        LOGGER.info("waitForSomeAppIconAndClick END");
    }
}
