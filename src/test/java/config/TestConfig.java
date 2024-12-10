package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties props = new Properties();
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        String env = System.getProperty("env", "qa");
        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("config/" + env + ".properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + env + " properties", e);
        }
    }
    
    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
    
    public static String getProperty(String key) {
        return props.getProperty(key);
    }
} 