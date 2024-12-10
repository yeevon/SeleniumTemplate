package config;

/**
 * Enum representing supported browsers.
 * Used for parameterized tests and WebDriver configuration.
 */
public enum Browser {
    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge");

    private final String value;

    Browser(String value) {
        this.value = value;
    }

    /**
     * Gets the browser name for WebDriver configuration.
     * @return String value of browser name
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
} 