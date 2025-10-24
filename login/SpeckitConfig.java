package org.deepak.login;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SpeckitConfig - Configuration manager for Speckit service settings
 */
public class SpeckitConfig {
    
    private static SpeckitConfig instance;
    private Properties properties;
    
    private SpeckitConfig() {
        loadProperties();
    }
    
    public static SpeckitConfig getInstance() {
        if (instance == null) {
            instance = new SpeckitConfig();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("speckit-config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                // Load default properties
                setDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("Failed to load Speckit configuration: " + e.getMessage());
            setDefaultProperties();
        }
    }
    
    private void setDefaultProperties() {
        properties.setProperty("speckit.enabled", "true");
        properties.setProperty("speckit.base.url", "https://api.speckit.com/v1");
        properties.setProperty("speckit.api.key", "");
        properties.setProperty("speckit.timeout", "30000");
        properties.setProperty("speckit.fallback.to.local", "true");
        properties.setProperty("speckit.sync.on.auth", "true");
        properties.setProperty("speckit.log.requests", "false");
        properties.setProperty("speckit.log.responses", "false");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(properties.getProperty(key, "false"));
    }
    
    public int getIntProperty(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key, "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public boolean isEnabled() {
        return getBooleanProperty("speckit.enabled");
    }
    
    public String getBaseUrl() {
        return getProperty("speckit.base.url", "https://api.speckit.com/v1");
    }
    
    public String getApiKey() {
        return getProperty("speckit.api.key", "");
    }
    
    public int getTimeout() {
        return getIntProperty("speckit.timeout");
    }
    
    public boolean isFallbackToLocal() {
        return getBooleanProperty("speckit.fallback.to.local");
    }
    
    public boolean isSyncOnAuth() {
        return getBooleanProperty("speckit.sync.on.auth");
    }
    
    public boolean isLogRequests() {
        return getBooleanProperty("speckit.log.requests");
    }
    
    public boolean isLogResponses() {
        return getBooleanProperty("speckit.log.responses");
    }
}