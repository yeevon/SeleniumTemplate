package config;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

/**
 * JUnit extension for test result logging and screenshot capture.
 * Handles test failures and success logging with screenshot capability.
 */
public class TestResultLogger implements TestWatcher {
    private static final Logger logger = LoggerFactory.getLogger(TestResultLogger.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots";

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = String.format("%s_%s",
            context.getTestMethod().get().getName(),
            context.getDisplayName().replaceAll("[\\[\\]]", ""));
            
        logger.error("Test failed: {} with error: {}", testName, cause.getMessage());
        
        if (context.getTestInstance().isPresent()) {
            BaseTest test = (BaseTest) context.getTestInstance().get();
            WebDriver driver = test.getDriver();
            
            try {
                if (driver != null && driver instanceof TakesScreenshot) {
                    File screenshotDir = new File(SCREENSHOT_DIR);
                    screenshotDir.mkdirs();

                    String filename = String.format("%s_%s_%s.png",
                        context.getTestMethod().get().getName(),  // Method name
                        test.getBrowser().name().toLowerCase(),   // Browser name
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

                    File destFile = new File(screenshotDir, filename);
                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(screenshot, destFile);
                    
                    logger.info("Screenshot saved to: {}", destFile.getAbsolutePath());
                } else {
                    logger.error("WebDriver not available for taking screenshot");
                }
            } catch (Exception e) {
                logger.error("Failed to capture screenshot: {}", e.getMessage());
                e.printStackTrace();
            } finally {
                test.quitDriver();
            }
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logger.info("Test passed: {}", context.getDisplayName());
        if (context.getTestInstance().isPresent()) {
            ((BaseTest) context.getTestInstance().get()).quitDriver();
        }
    }
} 