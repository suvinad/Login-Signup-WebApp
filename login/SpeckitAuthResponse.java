package org.deepak.login;

/**
 * SpeckitAuthResponse - Represents the response from Speckit service authentication
 */
public class SpeckitAuthResponse {
    
    private boolean success;
    private String message;
    private String token;
    
    public SpeckitAuthResponse() {
        this.success = false;
        this.message = "";
        this.token = null;
    }
    
    public SpeckitAuthResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "SpeckitAuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token='" + (token != null ? "***" : "null") + '\'' +
                '}';
    }
}