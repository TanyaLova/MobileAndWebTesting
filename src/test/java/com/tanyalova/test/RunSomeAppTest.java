package com.tanyalova.test;

import com.tanyalova.appium.AppiumController;
import com.tanyalova.influxdb.InfluxDbApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tanyalova.influxdb.InfluxDbReporter.*;


public class RunSomeAppTest extends BaseTestClass {

    public static final Logger LOGGER = LoggerFactory.getLogger(RunSomeAppTest.class);

    @BeforeClass
    @Parameters("executionOS")
    public void beforeStart(AppiumController.OS executionOS) {
        setDeviceName(executionOS.name());
        setTestRunId(UUID.randomUUID().toString());
        setFailedTestCounter(0);
    }

    @Test(description = "start Recording Screen")
    public void startRecordingScreen() {
        systemHomePage.startRecordingScreen();
    }

    @Test(dependsOnMethods = "startRecordingScreen")
    public void waitForSomeAppIconAndClick() {
        systemHomePage.waitForSomeAppIconAndClick();
        LOGGER.info("Some App Launched");
    }

    @Test(dependsOnMethods = "waitForSomeAppIconAndClick", description = "HomePage nav_time")
    public void testRunHomePage() {
        LOGGER.info("Home Page Loaded");
        logKpi(someAppHomePage.homeNavTime());
        Assert.assertTrue(someAppHomePage.isDisplayed(someAppHomePage.getHomeHeroPromoImageLocator()), "Home page is not displayed");
    }
    @Test(dependsOnMethods = "testRunHomePage")
    public void testRunSettings() {
        someAppHomePage.clickMenuButton();
        someAppMenuPage.clickSettingsButton();
        someAppSettingsPage.settingsPageIsDisplayed();
        LOGGER.info("SettingsPage loaded");
    }
    @Test(dependsOnMethods = "testRunSettings")
    public void testRunSignOutAndSignInPage() {
        someAppSettingsPage.clickOnSignOutAndSignIn();
        LOGGER.info("User has signed out");
    }
    @Test(dependsOnMethods = "testRunSignOutAndSignInPage", description = "SignInListPage nav_time")
    public void testRunSignInPage() {
        LOGGER.info("Sign in Page Loaded");
        logKpi(signInListPage.signInPageNavTime());
        Assert.assertTrue(signInListPage.isDisplayed(signInListPage.getSignInAgainLocator()), "SignInListPage is not displayed");
    }
    @Test(dependsOnMethods = "testRunSignInPage", description = "ProviderSignInPage nav_time")
    public void testRunProviderSignInPage() {
        signInListPage.clickOnProvider();
        LOGGER.info("ProviderSignInPage loaded");
        logKpi(someAppSignInPage.signInPageNavTime());
        Assert.assertTrue(someAppSignInPage.isDisplayed(someAppSignInPage.getPasswordInputLocator()), "ProviderSignInPage is not displayed");
    }
    @Test(dependsOnMethods = "testRunProviderSignInPage")
    public void testRunTypeCredentials(){
        someAppSignInPage.typeCredentials();
        LOGGER.info("TestRunTypeCredentials Ended");
    }
    @Test(dependsOnMethods = "testRunTypeCredentials", description = "SettingsPage nav_time")
    public void testRunSettingsPage() {
        LOGGER.info("SettingsPage loaded");
        logKpi(someAppSettingsPage.settingsPageNavTime());
        Assert.assertTrue(someAppSettingsPage.isDisplayed(someAppSettingsPage.getSettingsHeroLocator()), "SettingsPage Is not Playing");
    }
    @Test(dependsOnMethods = "testRunSettingsPage", description = "Menu nav_time")
    public void testRunMenuPage() {
        someAppHomePage.clickMenuButton();
        LOGGER.info("Menu Loaded");
        logKpi(someAppMenuPage.waitMenuNavTime());
        Assert.assertTrue(someAppMenuPage.isDisplayed(someAppMenuPage.getWatchLiveButtonLocator()),"Menu is not displayed");
    }

    @Test(dependsOnMethods = "testRunMenuPage", description = "WatchLivePage nav_time")
    public void testRunWatchLivePage() {
        someAppMenuPage.clickWatchLiveButton();
        LOGGER.info("Watch Live Page Loaded");
        logKpi(someAppWatchLivePage.watchLiveNavTime());
        Assert.assertTrue(someAppWatchLivePage.isDisplayed(someAppWatchLivePage.getWatchLiveImageLocator()), "WatchLivePage is not displayed");
    }
    @Test(dependsOnMethods = "testRunWatchLivePage", description = "VideoBufferingTime nav_time")
    public void testRunVideoBufferingPage() {
        someAppWatchLivePage.clickIsPlayingNow();
        LOGGER.info("Video Is Playing");
        logKpi(someAppVideoPlayerPage.videoBufferingTime());
        Assert.assertFalse(someAppVideoPlayerPage.isDisplayed(someAppVideoPlayerPage.getLoadingImageLocator()), "Video Is not Playing");
    }
    @Test(dependsOnMethods = "testRunVideoBufferingPage")
    public void testRunExitFromVideo() {
        someAppVideoPlayerPage.clickBackButtonInVideo();
        someAppWatchLivePage.watchLiveIsDisplayed();
        LOGGER.info("Video is closed");
    }

    @AfterClass
    @Parameters("appPackage")
    public synchronized void afterClass(String appPackage) {
        LOGGER.info("afterClass: " + getDeviceName());
        AppiumController.instance.terminateApp(appPackage);
        sleep(5);
        systemHomePage.decodeVideo(systemHomePage.stopRecordingScreen());
        AppiumController.instance.stopDriver();
        postTestClassStatus(getClass().getName(), getFailedTestCounter());
        InfluxDbApi.getInstance().validatePost();
//        systemHomePage.runScript();
    }

    public static void sleep(long sec) {
        LOGGER.info("Sleep for " + sec + " seconds");
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

