package config;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected static final int TIMEOUT = 10;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        this.actions = new Actions(driver);
    }

    // Click Methods
    protected void click(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    protected void clickWithJS(By by) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // Type Methods
    protected void sendKeys(By by, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        element.clear();
        element.sendKeys(text);
    }

    protected void sendKeys(By by, Keys key) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by)).sendKeys(key);
    }

    // Mouse Actions
    protected void mouseHover(By by) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        actions.moveToElement(element).perform();
    }

    protected void dragAndDrop(By source, By target) {
        WebElement sourceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(source));
        WebElement targetElement = wait.until(ExpectedConditions.visibilityOfElementLocated(target));
        actions.dragAndDrop(sourceElement, targetElement).perform();
    }

    // Dropdown Methods
    protected void selectByVisibleText(By by, String text) {
        Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
        select.selectByVisibleText(text);
    }

    protected void selectByValue(By by, String value) {
        Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
        select.selectByValue(value);
    }

    // Wait Methods
    protected void waitForVisibility(By by) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void waitForInvisibility(By by) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    protected void waitForClickable(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    // Get Methods
    protected String getText(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText();
    }

    protected String getValue(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getAttribute("value");
    }

    protected List<WebElement> getElements(By by) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    // Verification Methods
    protected boolean isDisplayed(By by) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isEnabled(By by) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(by)).isEnabled();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    // Scroll Methods
    protected void scrollToElement(By by) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
    }

    protected void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    // Frame Methods
    protected void switchToFrame(By by) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
    }

    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // Alert Methods
    protected void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    protected void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
    }

    // Window Methods
    protected void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    protected String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }
} 