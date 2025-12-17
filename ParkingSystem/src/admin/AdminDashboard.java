package admin;

import system.SessionManager;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    
    public AdminDashboard() {
        setTitle("Admin Dashboard - Parking Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        // Create tabbed pane for different admin functions
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add different management panels
        tabbedPane.addTab("User Management", new UserManagementPanel());
        tabbedPane.addTab("Spot Management", new SpotManagementPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        
        // Add logout button at the bottom
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            SessionManager.logout();
            this.dispose();
            new auth.LoginFrame().setVisible(true);
        });
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton revenueButton = new JButton("View Ticket Revenue Report");
        JButton parkedCarsButton = new JButton("View Currently Parked Cars");
        JButton allSpotsButton = new JButton("View All Parking Spots");
        
        revenueButton.addActionListener(e -> showRevenueReport());
        parkedCarsButton.addActionListener(e -> showParkedCarsReport());
        allSpotsButton.addActionListener(e -> showAllSpotsReport());
        
        panel.add(revenueButton);
        panel.add(parkedCarsButton);
        panel.add(allSpotsButton);
        
        return panel;
    }
    
    private void showRevenueReport() {
        // Implementation for revenue report
        JOptionPane.showMessageDialog(this, 
            "Revenue report feature would show total earnings from tickets.\n" +
            "This would parse all tickets and sum the amounts paid.");
    }
    
    private void showParkedCarsReport() {
        // Implementation for parked cars report
        JOptionPane.showMessageDialog(this, 
            "Parked cars report would show all currently occupied spots.\n" +
            "This would check spots.txt for occupied status.");
    }
    
    private void showAllSpotsReport() {
        // Implementation for all spots report
        JOptionPane.showMessageDialog(this, 
            "All spots report would show complete parking lot status.\n" +
            "This would display all spots with their current status.");
    }
}