package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import config.BasePage;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;

public class GooglePage extends BasePage {
    private final By searchBox = By.name("q");
    private final By searchButton = By.name("btnK");

    public By getSearchBox() {
        return searchBox;
    }

    public By getSearchButton() {
        return searchButton;
    }

    public GooglePage(WebDriver driver) {
        super(driver);
    }

    public GooglePage searchFor(String text) {
        sendTestToSearchBox(text);
        sendKeys(searchButton, Keys.ENTER);
        
        return this;
    }

    public GooglePage sendTestToSearchBox(String text) {
        sendKeys(searchBox, text);
        return this;
    }

    public GooglePage verifySearchResults(String text) {
        Assertions.assertTrue(driver.getTitle().contains(text), 
        "Search results page title does not contain the expected text");
        return this;
    }
} 