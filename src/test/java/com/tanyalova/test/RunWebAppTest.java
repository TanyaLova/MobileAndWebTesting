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


public class RunWebAppTest extends BaseTestClass {

    public static final Logger LOGGER = LoggerFactory.getLogger(RunWebAppTest.class);

    @BeforeClass
    @Parameters("executionOS")
    public void beforeStart(AppiumController.OS executionOS) {
        setDeviceName(executionOS.name());
        setTestRunId(UUID.randomUUID().toString());
        setFailedTestCounter(0);
    }

    @Test
    public void startRecordingScreen() {
        mainPage.startRecordingScreen();
    }

    @Test(dependsOnMethods = "startRecordingScreen", description = "App launch time")
    public void testWaitForLoadingMainPage() {
        logKpi(mainPage.waitForLoadingMainPage());
        LOGGER.info("Site Launched");
        Assert.assertTrue(mainPage.isDisplayed(mainPage.getMainPageHero()),"MainPage is not displayed");
    }

    @Test(dependsOnMethods = "testWaitForLoadingMainPage", description = "Sign in nav_time")
    public void testWaitForLoadingSignInPage() {
        mainPage.clickOnSignInButton();
        logKpi(signInPage.waitForLoadingSignInPage());
        LOGGER.info("Sign in page Launched");
        Assert.assertTrue(signInPage.isDisplayed(signInPage.getLoginInput()), "Login input is not displayed");
    }

    @Test(dependsOnMethods = "testWaitForLoadingSignInPage", description = "Profile nav_time")
    public void testWaitForSignInComplete() {
        LOGGER.info("Profile Page Loaded");
        logKpi(signInPage.waitForSignInComplete());
        Assert.assertTrue(signInPage.getProfile().isDisplayed(), "Profile page is not displayed");
    }

    @Test(dependsOnMethods = "testWaitForSignInComplete", description = "HomePage nav_time")
    public void testWaitForLoadingHomePage() {
        signInPage.clickOnProfile();
        LOGGER.info("Home Page Loaded");
        logKpi(homePage.waitForLoadingHomePage());
        Assert.assertTrue(homePage.isDisplayed(homePage.getHomeHero()), "Home page is not displayed");
    }

    @Test(dependsOnMethods = "testWaitForLoadingHomePage", description = "SearchPage nav_time")
    public void testWaitForLoadingSearchPage() {
        homePage.clickSearchButton();
        LOGGER.info("Search Page Loaded");
        logKpi(searchPage.waitForLoadingSearchPage());
        Assert.assertTrue(searchPage.isDisplayed(searchPage.getSearchHeroLocator()), "Search image is not displayed");
    }

    @Test(dependsOnMethods = "testWaitForLoadingSearchPage", description = "SearchAction nav_time")
    public void testTypeAndSearchContent() {
        LOGGER.info("Content Typed And Loaded");
        logKpi(searchPage.typeAndSearchContent());
        Assert.assertTrue(searchPage.getHouseImage().isDisplayed(), "HouseImage is not displayed");
    }

    @Test(dependsOnMethods = "testTypeAndSearchContent", description = "ContentPage nav_time")
    public void testWaitLoadingContentPage() {
        searchPage.clickOnHouseImage();
        LOGGER.info("Content Page Loaded");
        logKpi(contentPage.waitLoadingContentPage());
        Assert.assertTrue(contentPage.isDisplayed(contentPage.getHouseHeroLocator()), "HouseHero is not displayed");
    }

    @Test(dependsOnMethods = "testWaitLoadingContentPage", description = "VideoInitialPlayer")
    public void testWaitVideoInitialPlayer() {
        contentPage.clickRestart();
        LOGGER.info("Video Started To Load");
        logKpi(contentPage.waitVideoInitialPlayer());
    }

    @Test(dependsOnMethods = "testWaitVideoInitialPlayer", description = "VideoBufferingTime")
    public void testWaitVideoBufferingPage() {
        LOGGER.info("Video Is Playing");
        logKpi(videoPage.waitVideoBufferingPage());
        Assert.assertTrue(videoPage.getRatingLabel().isDisplayed(), "Time Label is not displayed");
    }

    @Test(dependsOnMethods = "testWaitVideoBufferingPage", description = "VideoExit nav_time")
    public void test10WaitExitFromVideo() {
        videoPage.clickExitFromVideo();
        LOGGER.info("Exit from video");
        logKpi(contentPage.waitExitFromVideo());
        Assert.assertTrue(contentPage.isDisplayed(contentPage.getHouseHeroLocator()), "HouseHero is not displayed");
    }

    @AfterClass
    @Parameters("appPackage")
    public synchronized void afterClass(String appPackage) {
        LOGGER.info("afterClass: " + getDeviceName());
        AppiumController.instance.terminateApp(appPackage);
        sleep(5);
        mainPage.decodeVideo(mainPage.stopRecordingScreen());
        AppiumController.instance.stopDriver();
        postTestClassStatus(RunWebAppTest.class.getName(), getFailedTestCounter());
        InfluxDbApi.getInstance().validatePost();
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

