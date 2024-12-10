package tests;

import pageobjects.GooglePage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import config.BaseTest;
import config.Browser;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GoogleSearchTest extends BaseTest {
    private GooglePage googlePage;

    @ParameterizedTest
    @EnumSource(value=Browser.class, names={"CHROME"})
    void testBasicSearch(Browser browser) {
        initDriver(browser);
        driver.get("https://www.google.com");
        googlePage = new GooglePage(driver);
        googlePage
            .searchFor("Selenium WebDriver")
            .verifySearchResults("Selenium WebDriver");
    }
}