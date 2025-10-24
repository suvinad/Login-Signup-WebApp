package org.deepak.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

/**
 * SpeckitLoginDb - Enhanced database operations with Speckit service integration
 * This class extends the original LoginDb functionality with Speckit service calls
 */
public class SpeckitLoginDb {
    
    private DataSource datasource;
    private SpeckitServiceClient speckitClient;
    private boolean speckitEnabled;
    
    public SpeckitLoginDb(DataSource dataSource) {
        this.datasource = dataSource;
        this.speckitClient = new SpeckitServiceClient();
        // Enable Speckit integration by default, can be configured
        this.speckitEnabled = Boolean.parseBoolean(System.getProperty("speckit.enabled", "true"));
    }
    
    public SpeckitLoginDb(DataSource dataSource, SpeckitServiceClient speckitClient) {
        this.datasource = dataSource;
        this.speckitClient = speckitClient;
        this.speckitEnabled = true;
    }
    
    /**
     * Authenticate user with both local database and Speckit service
     */
    public boolean getStudent(String username, String password) throws Exception {
        // First try local database authentication
        boolean localAuth = authenticateLocal(username, password);
        
        if (speckitEnabled) {
            // Try Speckit service authentication
            SpeckitAuthResponse speckitResponse = speckitClient.authenticateUser(username, password);
            
            if (speckitResponse.isSuccess()) {
                // If Speckit authentication succeeds, sync with local database if needed
                syncUserWithLocal(username, password, speckitResponse.getToken());
                return true;
            } else if (localAuth) {
                // If local auth succeeds but Speckit fails, sync to Speckit
                syncUserWithSpeckit(username, password);
                return true;
            }
            
            return false;
        } else {
            // Speckit disabled, use only local authentication
            return localAuth;
        }
    }
    
    /**
     * Register user with both local database and Speckit service
     */
    public void setStudent(String username, String password, String email) throws Exception {
        // Register with local database
        registerLocal(username, password, email);
        
        if (speckitEnabled) {
            // Register with Speckit service
            SpeckitAuthResponse speckitResponse = speckitClient.registerUser(username, password, email);
            
            if (!speckitResponse.isSuccess()) {
                // Log the error but don't fail the registration
                System.err.println("Speckit registration failed: " + speckitResponse.getMessage());
            }
        }
    }
    
    /**
     * Check if user exists in both local database and Speckit service
     */
    public boolean check(String username) throws SQLException {
        // Check local database
        boolean localExists = checkLocal(username);
        
        if (speckitEnabled) {
            // Check Speckit service
            boolean speckitExists = speckitClient.verifyUser(username);
            return localExists || speckitExists;
        } else {
            return localExists;
        }
    }
    
    /**
     * Update password in both local database and Speckit service
     */
    public void update(String newPassword, String username) throws SQLException {
        // Update local database
        updateLocal(newPassword, username);
        
        if (speckitEnabled) {
            // Update Speckit service
            SpeckitAuthResponse speckitResponse = speckitClient.updatePassword(username, newPassword);
            
            if (!speckitResponse.isSuccess()) {
                // Log the error but don't fail the update
                System.err.println("Speckit password update failed: " + speckitResponse.getMessage());
            }
        }
    }
    
    /**
     * Local database authentication (original functionality)
     */
    private boolean authenticateLocal(String username, String password) throws Exception {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        Connection mycon = null;
        Statement sts = null;
        ResultSet rs = null;
        
        try {
            mycon = datasource.getConnection();
            String query = "select * from logindata";
            sts = mycon.createStatement();
            rs = sts.executeQuery(query);
            
            while (rs.next()) {
                String user = rs.getString("username");
                String pass = rs.getString("password");
                hashmap.put(user, pass);
            }
            
            return hashmap.containsKey(username) && hashmap.containsValue(password);
            
        } finally {
            close(mycon, sts, rs);
        }
    }
    
    /**
     * Local database registration (original functionality)
     */
    private void registerLocal(String username, String password, String email) throws Exception {
        Connection mycon = null;
        PreparedStatement sts = null;
        
        try {
            mycon = datasource.getConnection();
            String query = "insert into logindata (username, password, email) values (?, ?, ?)";
            sts = mycon.prepareStatement(query);
            sts.setString(1, username);
            sts.setString(2, password);
            sts.setString(3, email);
            sts.execute();
            
        } finally {
            close(mycon, sts, null);
        }
    }
    
    /**
     * Local database user check (original functionality)
     */
    private boolean checkLocal(String username) throws SQLException {
        ArrayList<String> arr = new ArrayList<String>();
        Connection mycon = null;
        Statement sts = null;
        ResultSet rs = null;
        
        try {
            mycon = datasource.getConnection();
            sts = mycon.createStatement();
            String query = "select * from logindata";
            rs = sts.executeQuery(query);
            
            while (rs.next()) {
                arr.add(rs.getString("username"));
            }
            
            return arr.contains(username);
            
        } finally {
            close(mycon, sts, rs);
        }
    }
    
    /**
     * Local database password update (original functionality)
     */
    private void updateLocal(String newPassword, String username) throws SQLException {
        Connection mycon = null;
        PreparedStatement sts = null;
        
        try {
            mycon = datasource.getConnection();
            String query = "update logindata set password = ? where username = ?";
            sts = mycon.prepareStatement(query);
            sts.setString(1, newPassword);
            sts.setString(2, username);
            sts.executeUpdate();
            
        } finally {
            close(mycon, sts, null);
        }
    }
    
    /**
     * Sync user data with Speckit service
     */
    private void syncUserWithSpeckit(String username, String password) {
        try {
            // Get email from local database
            String email = getEmailFromLocal(username);
            if (email != null) {
                speckitClient.registerUser(username, password, email);
            }
        } catch (Exception e) {
            System.err.println("Failed to sync user with Speckit: " + e.getMessage());
        }
    }
    
    /**
     * Sync user data with local database
     */
    private void syncUserWithLocal(String username, String password, String token) {
        try {
            // Store Speckit token or additional data if needed
            // This could be extended to store tokens in a separate table
            System.out.println("User " + username + " authenticated via Speckit with token: " + 
                             (token != null ? "***" : "null"));
        } catch (Exception e) {
            System.err.println("Failed to sync Speckit data locally: " + e.getMessage());
        }
    }
    
    /**
     * Get email from local database
     */
    private String getEmailFromLocal(String username) throws SQLException {
        Connection mycon = null;
        PreparedStatement sts = null;
        ResultSet rs = null;
        
        try {
            mycon = datasource.getConnection();
            String query = "select email from logindata where username = ?";
            sts = mycon.prepareStatement(query);
            sts.setString(1, username);
            rs = sts.executeQuery();
            
            if (rs.next()) {
                return rs.getString("email");
            }
            
            return null;
            
        } finally {
            close(mycon, sts, rs);
        }
    }
    
    /**
     * Close database resources
     */
    private void close(Connection mycon, Statement sts, ResultSet rs) throws SQLException {
        if (mycon != null)
            mycon.close();
        if (sts != null)
            sts.close();
        if (rs != null)
            rs.close();
    }
    
    // Getters and Setters
    public boolean isSpeckitEnabled() {
        return speckitEnabled;
    }
    
    public void setSpeckitEnabled(boolean speckitEnabled) {
        this.speckitEnabled = speckitEnabled;
    }
    
    public SpeckitServiceClient getSpeckitClient() {
        return speckitClient;
    }
    
    public void setSpeckitClient(SpeckitServiceClient speckitClient) {
        this.speckitClient = speckitClient;
    }
}