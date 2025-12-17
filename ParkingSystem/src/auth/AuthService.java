package auth;

import models.*;
import utils.*;
import javax.swing.JOptionPane;
import java.util.List;

public class AuthService {
    
    // Check credentials
    public static User authenticate(String username, String password) {
        List<String> userLines = FileUtil.readAllLines(Constants.USERS_FILE);
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String storedUsername = parts[0].trim();
                String storedPassword = parts[1].trim();
                String role = parts[2].trim();
                
                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    // Create appropriate user object based on role
                    switch (role) {
                        case Constants.ROLE_ADMIN:
                            return new Admin(username, password);
                        case Constants.ROLE_CUSTOMER:
                            String vehiclePlate = parts.length > 3 ? parts[3].trim() : "";
                            return new Customer(username, password, vehiclePlate);
                        case Constants.ROLE_ENTRY_OPERATOR:
                            return new EntryOperator(username, password);
                        case Constants.ROLE_EXIT_OPERATOR:
                            return new ExitOperator(username, password);
                        default:
                            return null;
                    }
                }
            }
        }
        return null;
    }
    
    // Register new user
    public static boolean register(String username, String password, String role, String vehiclePlate) {
        List<String> userLines = FileUtil.readAllLines(Constants.USERS_FILE);
        
        // Check if username already exists
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && parts[0].trim().equals(username)) {
                return false; // Username exists
            }
        }
        
        // Add new user
        String userData = username + "," + password + "," + role;
        if (role.equals(Constants.ROLE_CUSTOMER)) {
            userData += "," + vehiclePlate;
        }
        
        FileUtil.appendLine(Constants.USERS_FILE, userData);
        return true;
    }
    
    // Create default admin if no users exist
    public static void createDefaultAdmin() {
        List<String> userLines = FileUtil.readAllLines(Constants.USERS_FILE);
        if (userLines.isEmpty()) {
            register("admin", "admin123", Constants.ROLE_ADMIN, "");
            register("customer", "cust123", Constants.ROLE_CUSTOMER, "ABC123");
            register("entryop", "entry123", Constants.ROLE_ENTRY_OPERATOR, "");
            register("exitop", "exit123", Constants.ROLE_EXIT_OPERATOR, "");
            
            // Create some default parking spots
            for (int i = 1; i <= 10; i++) {
                FileUtil.appendLine(Constants.SPOTS_FILE, "SPOT" + i + ",false,");
            }
        }
    }
}