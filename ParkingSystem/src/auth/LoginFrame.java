package auth;

import models.User;
import system.*;
import utils.*;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    public LoginFrame() {
        setTitle("Parking System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create default admin on startup
        AuthService.createDefaultAdmin();
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Parking Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Username
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(userLabel, gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);
        
        // Password
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passLabel, gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Login Button
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginButton, gbc);
        
        // Register Button
        registerButton = new JButton("Register as Customer");
        gbc.gridy = 4;
        mainPanel.add(registerButton, gbc);
        
        // Add action listeners
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> showRegistrationDialog());
        
        add(mainPanel);
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        
        User user = AuthService.authenticate(username, password);
        
        if (user != null) {
            // Store user in session
            SessionManager.setCurrentUser(user);
            
            // Open appropriate dashboard based on role
            switch (user.getRole()) {
                case Constants.ROLE_ADMIN:
                    new admin.AdminDashboard().setVisible(true);
                    break;
                case Constants.ROLE_CUSTOMER:
                    new customer.CustomerGUI().setVisible(true);
                    break;
                case Constants.ROLE_ENTRY_OPERATOR:
                    new operator.EntryOperatorGUI().setVisible(true);
                    break;
                case Constants.ROLE_EXIT_OPERATOR:
                    new operator.ExitOperator().setVisible(true);
                    break;
            }
            
            this.dispose(); // Close login window
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        }
    }
    
    private void showRegistrationDialog() {
        JTextField regUserField = new JTextField();
        JPasswordField regPassField = new JPasswordField();
        JTextField plateField = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Username:"));
        panel.add(regUserField);
        panel.add(new JLabel("Password:"));
        panel.add(regPassField);
        panel.add(new JLabel("Vehicle Plate:"));
        panel.add(plateField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Register as Customer", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = regUserField.getText().trim();
            String password = new String(regPassField.getPassword());
            String plate = plateField.getText().trim();
            
            if (username.isEmpty() || password.isEmpty() || plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required");
                return;
            }
            
            boolean success = AuthService.register(username, password, 
                Constants.ROLE_CUSTOMER, plate);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists");
            }
        }
    }
}