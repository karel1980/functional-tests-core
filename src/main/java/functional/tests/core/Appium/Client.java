package functional.tests.core.Appium;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

public class Client {

    public static AppiumDriver<?> driver;

    public static void initAppiumDriver() {

        Log.info("Start Appium client...");

        if (Server.service == null || !Server.service.isRunning()) {
            Log.fatal("An appium server node is not started!");
            throw new RuntimeException("An appium server node is not started!");
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, Settings.appiumVersion);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, Settings.platformVersion);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Settings.platform);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, Settings.deviceName);
        capabilities.setCapability(MobileCapabilityType.APP, Settings.baseTestAppDir + File.separator + Settings.testAppName);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, Settings.defaultTimeout);
        capabilities.setCapability(MobileCapabilityType.LAUNCH_TIMEOUT, true);

        // iOS Simulator does not have deviceId
        if (Settings.deviceId != null) {
            capabilities.setCapability(MobileCapabilityType.UDID, Settings.deviceId);
        }

        // BaseDevice Specific Settings
        if (Settings.platform == PlatformType.Andorid) {
            driver = new AndroidDriver<>(Server.service.getUrl(), capabilities);
        }

        // iOS Specific Settings
        if (Settings.platform == PlatformType.iOS) {
            capabilities.setCapability("screenshotWaitTimeout", Settings.defaultTimeout);
            capabilities.setCapability("autoAcceptAlerts", true);
            capabilities.setCapability("launchTimeout", Settings.deviceBootTimeout * 1000);
            driver = new IOSDriver<>(Server.service.getUrl(), capabilities);
        }

        // Set default timeout
        driver.manage().timeouts().implicitlyWait(Settings.defaultTimeout, TimeUnit.SECONDS);

        Log.info("Appium client started.");
    }

    public static void stopAppiumDriver() {

        Log.info("Stop Appium client...");

        if (driver != null) {
            driver.quit();
        }

        Log.info("Appium client stoped.");
    }

    /**
     * Set implicit wait in seconds *
     */
    public static void setWait(int seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }
}
