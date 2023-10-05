package com.tanyalova.test;


import com.tanyalova.appium.AppiumBaseClass;
import com.tanyalova.appium.AppiumController;
import com.tanyalova.common.ConfigProperties;
import com.tanyalova.pages.BasePage;
import com.tanyalova.pages.SystemHomePage;
import com.tanyalova.pages.webapp.*;
import com.tanyalova.pages.someApp.*;
import com.tanyalova.pages.webapp.SignInPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import java.time.Instant;

import static com.tanyalova.appium.AppiumController.IOS_DEVICE_ID;
import static com.tanyalova.common.ConfigProperties.getEnv;
import static com.tanyalova.influxdb.InfluxDbReporter.getMethodName;


/**
 * Created by Thomas on 2016-06-15.
 */
public class BaseTestClass extends AppiumBaseClass {
    SystemHomePage systemHomePage;
    MainPage mainPage;
    SignInPage signInPage;
    HomePage homePage;
    SearchPage searchPage;
    ContentPage contentPage;
    VideoPage videoPage;
    SomeAppHomePage someAppHomePage;
    SignInListPage signInListPage;
    SomeAppMenuPage someAppMenuPage;
    SomeAppVideoPlayerPage someAppVideoPlayerPage;
    SomeAppWatchLivePage someAppWatchLivePage;
    SomeAppSignInPage someAppSignInPage;
    SomeAppSettingsPage someAppSettingsPage;
    private BasePage page;
    //public static final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //public static final PrintStream old = System.out;

    @BeforeSuite
    public synchronized void setUp() {
        if ("true".equalsIgnoreCase(System.getProperty("runIOSWebDriverAgent"))) {
            AppiumController.instance.runWebDriverAgent(IOS_DEVICE_ID);
        }
        AppiumController.instance.startAppiumServer();
    }

    @BeforeClass
    @Parameters({"executionOS", "appPackage"})
    public synchronized void setUp(AppiumController.OS executionOS, String appPackage) {
        if (getEnv() == ConfigProperties.Env.LOCAL) {
            setConsoleOutput();
        }
//        InfluxDbApi.refreshBucket();
        AppiumController.instance.start(executionOS);
        page = new BasePage(driver());
        systemHomePage = new SystemHomePage(driver());
        switch (appPackage) {
            case "chrome":
                initWebPages();
                break;
            case "com.someApp.someApp":
                initSomeAppPages();
                break;
            case "com.android.chrome":
                initAndroidChromePages();
                break;
        }
    }

    public synchronized void initWebPages() {
        mainPage = new MainPage(driver());
        signInPage = new SignInPage(driver());
        homePage = new HomePage(driver());
        searchPage = new SearchPage(driver());
        contentPage = new ContentPage(driver());
        videoPage = new VideoPage(driver());
    }

    public synchronized void initSomeAppPages() {
        someAppHomePage = new SomeAppHomePage(driver());
        someAppMenuPage = new SomeAppMenuPage(driver());
        signInListPage = new SignInListPage(driver());
        someAppSignInPage = new SomeAppSignInPage(driver());
        someAppWatchLivePage = new SomeAppWatchLivePage(driver());
        someAppVideoPlayerPage = new SomeAppVideoPlayerPage(driver());
        someAppSettingsPage = new SomeAppSettingsPage(driver());
    }

    public synchronized void initAndroidChromePages() {

    }

    private void setConsoleOutput() {
        //PrintStream ps = new PrintStream(baos);
        //System.setOut(ps);
    }

    @AfterClass
    public synchronized void tearDown() {
        if (getEnv() == ConfigProperties.Env.LOCAL) {
            //System.setOut(old);
            //System.out.println(baos);
        }
    }

    public void logKpi(Instant startTime) {
        page.logKpi(getMethodName(), startTime);
    }

    public void logKpi(Instant startTime, boolean takeScreenshot) {
        page.logKpi(getMethodName(), startTime, takeScreenshot);
    }
}

