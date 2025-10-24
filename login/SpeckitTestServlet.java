package org.deepak.login;

import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * SpeckitTestServlet - Test servlet for Speckit integration
 * This servlet provides endpoints to test Speckit service functionality
 */
@WebServlet("/SpeckitTestServlet")
public class SpeckitTestServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Resource(name="jdbc/login-webapp")
    private DataSource datasource;
    
    private SpeckitLoginDb speckitLoginDb;
    private SpeckitServiceClient speckitClient;
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            speckitLoginDb = new SpeckitLoginDb(datasource);
            speckitClient = new SpeckitServiceClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Speckit Integration Test</title></head><body>");
        out.println("<h1>Speckit Service Integration Test</h1>");
        
        if (action == null) {
            showTestMenu(out);
        } else {
            switch (action) {
                case "test-connection":
                    testSpeckitConnection(out);
                    break;
                case "test-auth":
                    testAuthentication(request, out);
                    break;
                case "test-register":
                    testRegistration(request, out);
                    break;
                case "test-verify":
                    testUserVerification(request, out);
                    break;
                case "config":
                    showConfiguration(out);
                    break;
                default:
                    out.println("<p>Unknown action: " + action + "</p>");
            }
        }
        
        out.println("</body></html>");
    }
    
    private void showTestMenu(PrintWriter out) {
        out.println("<h2>Test Menu</h2>");
        out.println("<ul>");
        out.println("<li><a href='?action=test-connection'>Test Speckit Connection</a></li>");
        out.println("<li><a href='?action=test-auth'>Test Authentication</a></li>");
        out.println("<li><a href='?action=test-register'>Test Registration</a></li>");
        out.println("<li><a href='?action=test-verify'>Test User Verification</a></li>");
        out.println("<li><a href='?action=config'>Show Configuration</a></li>");
        out.println("</ul>");
    }
    
    private void testSpeckitConnection(PrintWriter out) {
        out.println("<h2>Speckit Connection Test</h2>");
        
        try {
            // Test basic connection
            SpeckitAuthResponse response = speckitClient.authenticateUser("test", "test");
            
            out.println("<p><strong>Connection Status:</strong> ");
            if (response.isSuccess()) {
                out.println("<span style='color: green;'>SUCCESS</span></p>");
            } else {
                out.println("<span style='color: red;'>FAILED</span></p>");
            }
            
            out.println("<p><strong>Response Message:</strong> " + response.getMessage() + "</p>");
            out.println("<p><strong>Base URL:</strong> " + speckitClient.getBaseUrl() + "</p>");
            out.println("<p><strong>API Key:</strong> " + 
                       (speckitClient.getApiKey().isEmpty() ? "Not Set" : "Set") + "</p>");
            
        } catch (Exception e) {
            out.println("<p style='color: red;'><strong>Error:</strong> " + e.getMessage() + "</p>");
        }
    }
    
    private void testAuthentication(PrintWriter out) {
        out.println("<h2>Authentication Test</h2>");
        out.println("<form method='post' action='?action=test-auth'>");
        out.println("<p>Username: <input type='text' name='username' value='testuser'></p>");
        out.println("<p>Password: <input type='password' name='password' value='testpass'></p>");
        out.println("<p><input type='submit' value='Test Authentication'></p>");
        out.println("</form>");
    }
    
    private void testRegistration(PrintWriter out) {
        out.println("<h2>Registration Test</h2>");
        out.println("<form method='post' action='?action=test-register'>");
        out.println("<p>Username: <input type='text' name='username' value='newuser'></p>");
        out.println("<p>Password: <input type='password' name='password' value='newpass'></p>");
        out.println("<p>Email: <input type='email' name='email' value='newuser@example.com'></p>");
        out.println("<p><input type='submit' value='Test Registration'></p>");
        out.println("</form>");
    }
    
    private void testUserVerification(PrintWriter out) {
        out.println("<h2>User Verification Test</h2>");
        out.println("<form method='post' action='?action=test-verify'>");
        out.println("<p>Username: <input type='text' name='username' value='testuser'></p>");
        out.println("<p><input type='submit' value='Test Verification'></p>");
        out.println("</form>");
    }
    
    private void showConfiguration(PrintWriter out) {
        out.println("<h2>Speckit Configuration</h2>");
        
        SpeckitConfig config = SpeckitConfig.getInstance();
        
        out.println("<table border='1' style='border-collapse: collapse;'>");
        out.println("<tr><th>Setting</th><th>Value</th></tr>");
        out.println("<tr><td>Enabled</td><td>" + config.isEnabled() + "</td></tr>");
        out.println("<tr><td>Base URL</td><td>" + config.getBaseUrl() + "</td></tr>");
        out.println("<tr><td>API Key</td><td>" + 
                   (config.getApiKey().isEmpty() ? "Not Set" : "Set") + "</td></tr>");
        out.println("<tr><td>Timeout</td><td>" + config.getTimeout() + " ms</td></tr>");
        out.println("<tr><td>Fallback to Local</td><td>" + config.isFallbackToLocal() + "</td></tr>");
        out.println("<tr><td>Sync on Auth</td><td>" + config.isSyncOnAuth() + "</td></tr>");
        out.println("<tr><td>Log Requests</td><td>" + config.isLogRequests() + "</td></tr>");
        out.println("<tr><td>Log Responses</td><td>" + config.isLogResponses() + "</td></tr>");
        out.println("</table>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Speckit Test Result</title></head><body>");
        out.println("<h1>Speckit Test Result</h1>");
        
        try {
            switch (action) {
                case "test-auth":
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    
                    out.println("<h2>Authentication Test Result</h2>");
                    out.println("<p><strong>Username:</strong> " + username + "</p>");
                    
                    boolean authResult = speckitLoginDb.getStudent(username, password);
                    out.println("<p><strong>Result:</strong> " + 
                               (authResult ? "SUCCESS" : "FAILED") + "</p>");
                    break;
                    
                case "test-register":
                    String regUsername = request.getParameter("username");
                    String regPassword = request.getParameter("password");
                    String email = request.getParameter("email");
                    
                    out.println("<h2>Registration Test Result</h2>");
                    out.println("<p><strong>Username:</strong> " + regUsername + "</p>");
                    out.println("<p><strong>Email:</strong> " + email + "</p>");
                    
                    speckitLoginDb.setStudent(regUsername, regPassword, email);
                    out.println("<p><strong>Result:</strong> SUCCESS</p>");
                    break;
                    
                case "test-verify":
                    String verifyUsername = request.getParameter("username");
                    
                    out.println("<h2>User Verification Test Result</h2>");
                    out.println("<p><strong>Username:</strong> " + verifyUsername + "</p>");
                    
                    boolean verifyResult = speckitLoginDb.check(verifyUsername);
                    out.println("<p><strong>User Exists:</strong> " + verifyResult + "</p>");
                    break;
                    
                default:
                    out.println("<p>Unknown action: " + action + "</p>");
            }
            
        } catch (Exception e) {
            out.println("<p style='color: red;'><strong>Error:</strong> " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("<p><a href='SpeckitTestServlet'>Back to Test Menu</a></p>");
        out.println("</body></html>");
    }
}