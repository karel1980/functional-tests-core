package common.Appium;

import common.Enums.OSType;
import common.Exceptions.AppiumException;
import common.Log.Log;
import common.Settings.Settings;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Server {

    public static AppiumDriverLocalService service;

    public static void initAppiumServer() throws IOException, AppiumException {
        Log.info("Init Appium server...");

        File logFile = new File(Settings.appiumLogFile);
        Files.deleteIfExists(logFile.toPath());
        boolean mkdirResult = logFile.getParentFile().mkdirs();
        boolean createLogFileResult = logFile.createNewFile();

        if (mkdirResult && createLogFileResult) {
            Log.debug("Appium log file created.");
        } else {
            Log.fatal("Failed to create appium log file.");
        }


        // Appium Version Manager is not available on Windows, so tests will use the global installation
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder()
                .withLogFile(logFile)
                .usingAnyFreePort()
                .withArgument(GeneralServerFlag.AUTOMATION_NAME, Settings.automationName);

        // On Linux and OSX use appium version manager
        if ((Settings.OS == OSType.Linux) || (Settings.OS == OSType.MacOS)) {
            // TODO: Use "avm bin <version>" to get path of appium executable
            String appiumPath = "/usr/local/avm/versions/" +
                    Settings.appiumVersion +
                    "/node_modules/appium/bin/appium.js";
            File appiumExecutable = new File(appiumPath);
            if (logFile.exists()) {
                serviceBuilder.withAppiumJS(appiumExecutable);
            } else {
                String error = "Failed to find appium " + Settings.appiumVersion + " at " + appiumPath;
                Log.fatal(error);
                throw new AppiumException(error);
            }
        }

        service = AppiumDriverLocalService.buildService(serviceBuilder);
        service.start();
        Log.info("Appium server started.");
    }

    public static void stopAppiumServer() {
        if (service != null) {
            service.stop();
        }
        Log.info("Appium server stopped.");
    }
}
