package admin;

import utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    
    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadUsers();
    }
    
    private void initComponents() {
        // Table for users
        String[] columns = {"Username", "Role", "Vehicle Plate"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(e -> addUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsers());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        
        List<String> userLines = FileUtil.readAllLines(Constants.USERS_FILE);
        
        for (String line : userLines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String username = parts[0].trim();
                String role = parts[2].trim();
                String vehiclePlate = parts.length > 3 ? parts[3].trim() : "N/A";
                
                tableModel.addRow(new Object[]{username, role, vehiclePlate});
            }
        }
    }
    
    private void addUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{
            Constants.ROLE_ADMIN, Constants.ROLE_CUSTOMER,
            Constants.ROLE_ENTRY_OPERATOR, Constants.ROLE_EXIT_OPERATOR
        });
        JTextField plateField = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Vehicle Plate (if customer):"));
        panel.add(plateField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add New User", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            String plate = plateField.getText().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required");
                return;
            }
            
            // Check if username exists
            List<String> userLines = FileUtil.readAllLines(Constants.USERS_FILE);
            for (String line : userLines) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists");
                    return;
                }
            }
            
            // Add user
            String userData = username + "," + password + "," + role;
            if (role.equals(Constants.ROLE_CUSTOMER)) {
                userData += "," + plate;
            }
            
            FileUtil.appendLine(Constants.USERS_FILE, userData);
            loadUsers();
            
            JOptionPane.showMessageDialog(this, "User added successfully");
        }
    }
    
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete user: " + username + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<String> userLines = FileUtil.readAllLines(Constants.USERS_FILE);
            
            // Remove the selected user
            for (int i = 0; i < userLines.size(); i++) {
                String[] parts = userLines.get(i).split(",");
                if (parts.length > 0 && parts[0].trim().equals(username)) {
                    userLines.remove(i);
                    break;
                }
            }
            
            FileUtil.writeAllLines(Constants.USERS_FILE, userLines);
            loadUsers();
            
            JOptionPane.showMessageDialog(this, "User deleted successfully");
        }
    }
}