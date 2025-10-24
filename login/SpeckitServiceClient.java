package org.deepak.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * SpeckitServiceClient - Handles communication with Speckit service
 * This class provides methods to interact with the Speckit service API
 */
public class SpeckitServiceClient {
    
    private String baseUrl;
    private String apiKey;
    private int timeout;
    
    // Default configuration
    private static final String DEFAULT_BASE_URL = "https://api.speckit.com/v1";
    private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds
    
    public SpeckitServiceClient() {
        this.baseUrl = DEFAULT_BASE_URL;
        this.timeout = DEFAULT_TIMEOUT;
        // API key should be loaded from configuration
        this.apiKey = System.getProperty("speckit.api.key", "");
    }
    
    public SpeckitServiceClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.timeout = DEFAULT_TIMEOUT;
    }
    
    public SpeckitServiceClient(String baseUrl, String apiKey, int timeout) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.timeout = timeout;
    }
    
    /**
     * Authenticate user with Speckit service
     * @param username User's username
     * @param password User's password
     * @return SpeckitAuthResponse containing authentication result
     */
    public SpeckitAuthResponse authenticateUser(String username, String password) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("username", username);
            requestData.put("password", password);
            requestData.put("service", "login-webapp");
            
            String response = makeHttpRequest("/auth/login", "POST", requestData);
            return parseAuthResponse(response);
            
        } catch (Exception e) {
            return new SpeckitAuthResponse(false, "Authentication failed: " + e.getMessage(), null);
        }
    }
    
    /**
     * Register user with Speckit service
     * @param username User's username
     * @param password User's password
     * @param email User's email
     * @return SpeckitAuthResponse containing registration result
     */
    public SpeckitAuthResponse registerUser(String username, String password, String email) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("username", username);
            requestData.put("password", password);
            requestData.put("email", email);
            requestData.put("service", "login-webapp");
            
            String response = makeHttpRequest("/auth/register", "POST", requestData);
            return parseAuthResponse(response);
            
        } catch (Exception e) {
            return new SpeckitAuthResponse(false, "Registration failed: " + e.getMessage(), null);
        }
    }
    
    /**
     * Verify user exists in Speckit service
     * @param username User's username
     * @return boolean indicating if user exists
     */
    public boolean verifyUser(String username) {
        try {
            String response = makeHttpRequest("/auth/verify/" + username, "GET", null);
            SpeckitAuthResponse authResponse = parseAuthResponse(response);
            return authResponse.isSuccess();
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Update user password in Speckit service
     * @param username User's username
     * @param newPassword New password
     * @return SpeckitAuthResponse containing update result
     */
    public SpeckitAuthResponse updatePassword(String username, String newPassword) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("username", username);
            requestData.put("password", newPassword);
            
            String response = makeHttpRequest("/auth/update-password", "PUT", requestData);
            return parseAuthResponse(response);
            
        } catch (Exception e) {
            return new SpeckitAuthResponse(false, "Password update failed: " + e.getMessage(), null);
        }
    }
    
    /**
     * Make HTTP request to Speckit service
     */
    private String makeHttpRequest(String endpoint, String method, Map<String, Object> data) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Set request properties
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("User-Agent", "Login-WebApp/1.0");
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        
        // Handle request data
        if (data != null && (method.equals("POST") || method.equals("PUT"))) {
            connection.setDoOutput(true);
            String jsonData = mapToJson(data);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        
        // Read response
        int responseCode = connection.getResponseCode();
        BufferedReader reader;
        
        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * Convert Map to JSON string (simple implementation)
     * In production, use a proper JSON library like Gson
     */
    private String mapToJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Parse authentication response from Speckit service
     */
    private SpeckitAuthResponse parseAuthResponse(String response) {
        // Simple JSON parsing - in production, use a proper JSON library
        boolean success = response.contains("\"success\":true") || response.contains("\"status\":\"success\"");
        String message = "Operation completed";
        String token = null;
        
        // Extract message and token if present
        if (response.contains("\"message\"")) {
            int start = response.indexOf("\"message\":\"") + 11;
            int end = response.indexOf("\"", start);
            if (start > 10 && end > start) {
                message = response.substring(start, end);
            }
        }
        
        if (response.contains("\"token\"")) {
            int start = response.indexOf("\"token\":\"") + 9;
            int end = response.indexOf("\"", start);
            if (start > 8 && end > start) {
                token = response.substring(start, end);
            }
        }
        
        return new SpeckitAuthResponse(success, message, token);
    }
    
    // Getters and Setters
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}