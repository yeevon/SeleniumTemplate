package config;

import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.apache.commons.io.FileUtils;
import java.nio.file.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import java.nio.file.attribute.FileTime;

/**
 * Base test class providing common test functionality.
 * Handles WebDriver lifecycle, screenshots, and test utilities.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestResultLogger.class)
public abstract class BaseTest {
    protected WebDriver driver;
    protected Browser browser;
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    /**
     * Gets the current WebDriver instance.
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * Gets the current browser being used.
     * @return Browser enum value
     */
    protected Browser getBrowser() {
        return browser;
    }

    /**
     * Initializes WebDriver for specified browser.
     * @param browser Browser to initialize
     */
    protected void initDriver(Browser browser) {
        this.browser = browser;
        System.setProperty("browser", browser.getValue());
        driver = WebDriverConfig.getDriver();
    }

    @BeforeEach
    void setUp() {
        if (driver == null) {
            driver = WebDriverConfig.getDriver();
        }
    }

    protected void quitDriver() {
        if (driver != null) {
            WebDriverConfig.quitDriver();
            driver = null;
        }
    }

    public void takeScreenshot(String testName) {
        logger.info("Taking screenshot for test: {}", testName);
        
        if (driver == null || !(driver instanceof TakesScreenshot)) {
            logger.error("Driver is null or does not support screenshots");
            return;
        }

        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists() && !screenshotDir.mkdirs()) {
                logger.error("Failed to create screenshot directory");
                return;
            }

            // Parse test name from JUnit display name
            String[] parts = testName.split("\\[|\\]");
            String methodName = parts[0].trim();  // Test method name
            
            String filename = String.format("%s_%s_%s.png", 
                methodName,
                browser.name().toLowerCase(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            File destFile = new File(screenshotDir, filename);
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, destFile);
            
            logger.info("Screenshot successfully saved to: {}", destFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to take screenshot: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @BeforeAll
    void globalSetup() {
        cleanupOldScreenshots();
    }

    /**
     * Cleans up screenshots older than 24 hours.
     * Should be called periodically, perhaps in @BeforeAll or setUp
     */
    protected void cleanupOldScreenshots() {
        Path screenshotPath = Paths.get(SCREENSHOT_DIR);
        if (!Files.exists(screenshotPath)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(screenshotPath, "*.png")) {
            Instant dayAgo = Instant.now().minus(24, ChronoUnit.HOURS);
            
            for (Path file : stream) {
                try {
                    FileTime creationTime = Files.getLastModifiedTime(file);
                    if (creationTime.toInstant().isBefore(dayAgo)) {
                        Files.delete(file);
                        logger.info("Deleted old screenshot: {}", file.getFileName());
                    }
                } catch (IOException e) {
                    logger.error("Failed to process screenshot file: {}", file.getFileName(), e);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to cleanup screenshots", e);
        }
    }
} 