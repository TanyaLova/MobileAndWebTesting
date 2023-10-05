package com.tanyalova.appium;

import org.openqa.selenium.WebDriver;

public abstract class AppiumBaseClass {

    protected WebDriver driver() {
        return AppiumController.instance.getDriver();
    }
}
