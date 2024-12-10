package config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Manages WebDriver instances and browser configuration.
 * Provides singleton pattern for WebDriver management.
 */
public class WebDriverConfig {
    private static WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(WebDriverConfig.class);
    private static final long DRIVER_TIMEOUT_MINUTES = 30;
    private static Timer cleanupTimer;
    private static long lastUsedTime;

    static {
        // Add shutdown hook to cleanup any remaining drivers
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Running shutdown hook to cleanup WebDriver instances");
            quitDriver();
        }));
    }

    /**
     * Gets or creates WebDriver instance.
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        if (driver == null) {
            String browser = System.getProperty("browser", "chrome");
            logger.info("Initializing {} browser", browser);
            createDriver(browser);
            scheduleCleanup();
        }
        updateLastUsedTime();
        return driver;
    }

    private static void createDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(new ChromeOptions());
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(new FirefoxOptions());
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(new EdgeOptions());
                break;
            default:
                logger.error("Unsupported browser: {}", browser);
                throw new RuntimeException("Unsupported browser: " + browser);
        }
        driver.manage().window().maximize();
        logger.info("Browser initialized successfully");
    }

    private static void updateLastUsedTime() {
        lastUsedTime = System.currentTimeMillis();
    }

    private static void scheduleCleanup() {
        if (cleanupTimer != null) {
            cleanupTimer.cancel();
        }
        cleanupTimer = new Timer(true);
        cleanupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long idleTime = System.currentTimeMillis() - lastUsedTime;
                if (idleTime >= TimeUnit.MINUTES.toMillis(DRIVER_TIMEOUT_MINUTES)) {
                    logger.warn("Driver idle for {} minutes. Cleaning up...", 
                        TimeUnit.MILLISECONDS.toMinutes(idleTime));
                    quitDriver();
                }
            }
        }, TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(1));
    }

    public static void quitDriver() {
        if (cleanupTimer != null) {
            cleanupTimer.cancel();
            cleanupTimer = null;
        }
        if (driver != null) {
            logger.info("Quitting browser");
            driver.quit();
            driver = null;
        }
    }
} 