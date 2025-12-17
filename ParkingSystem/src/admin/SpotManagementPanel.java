package admin;

import utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SpotManagementPanel extends JPanel {
    private JTable spotsTable;
    private DefaultTableModel tableModel;
    
    public SpotManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadSpots();
    }
    
    private void initComponents() {
        // Table for spots
        String[] columns = {"Spot ID", "Status", "Occupied By Ticket"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        spotsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(spotsTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Spot");
        JButton deleteButton = new JButton("Delete Spot");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(e -> addSpot());
        deleteButton.addActionListener(e -> deleteSpot());
        refreshButton.addActionListener(e -> loadSpots());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadSpots() {
        tableModel.setRowCount(0);
        
        List<String> spotLines = FileUtil.readAllLines(Constants.SPOTS_FILE);
        
        for (String line : spotLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                String spotId = parts[0].trim();
                boolean isOccupied = Boolean.parseBoolean(parts[1].trim());
                String occupiedBy = parts.length >= 3 ? parts[2].trim() : "";
                
                String status = isOccupied ? "Occupied" : "Available";
                tableModel.addRow(new Object[]{spotId, status, occupiedBy});
            }
        }
    }
    
    private void addSpot() {
        String spotId = JOptionPane.showInputDialog(this, "Enter new Spot ID:");
        
        if (spotId != null && !spotId.trim().isEmpty()) {
            spotId = spotId.trim().toUpperCase();
            
            // Check if spot already exists
            List<String> spotLines = FileUtil.readAllLines(Constants.SPOTS_FILE);
            for (String line : spotLines) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equalsIgnoreCase(spotId)) {
                    JOptionPane.showMessageDialog(this, "Spot ID already exists");
                    return;
                }
            }
            
            // Add new spot
            FileUtil.appendLine(Constants.SPOTS_FILE, spotId + ",false,");
            loadSpots();
            
            JOptionPane.showMessageDialog(this, "Parking spot added successfully");
        }
    }
    
    private void deleteSpot() {
        int selectedRow = spotsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a spot to delete");
            return;
        }
        
        String spotId = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Check if spot is occupied
        String status = (String) tableModel.getValueAt(selectedRow, 1);
        if ("Occupied".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot delete occupied spot. Please wait until it's vacant.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete spot: " + spotId + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<String> spotLines = FileUtil.readAllLines(Constants.SPOTS_FILE);
            
            // Remove the selected spot
            for (int i = 0; i < spotLines.size(); i++) {
                String[] parts = spotLines.get(i).split(",");
                if (parts.length > 0 && parts[0].trim().equals(spotId)) {
                    spotLines.remove(i);
                    break;
                }
            }
            
            FileUtil.writeAllLines(Constants.SPOTS_FILE, spotLines);
            loadSpots();
            
            JOptionPane.showMessageDialog(this, "Spot deleted successfully");
        }
    }
}