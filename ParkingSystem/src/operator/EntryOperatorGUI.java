package operator;

import utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EntryOperatorGUI extends JFrame {
    private JTable spotsTable;
    private DefaultTableModel tableModel;
    
    public EntryOperatorGUI() {
        setTitle("Entry Operator Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        loadParkingSpots();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Parking Entry Station");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table for parking spots
        String[] columns = {"Spot ID", "Status", "Occupied By"};
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
        JButton refreshButton = new JButton("Refresh Spots");
        JButton adviseButton = new JButton("Advise Free Spot");
        
        refreshButton.addActionListener(e -> loadParkingSpots());
        adviseButton.addActionListener(e -> adviseFreeSpot());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(adviseButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadParkingSpots() {
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
    
    private void adviseFreeSpot() {
        List<String> spotLines = FileUtil.readAllLines(Constants.SPOTS_FILE);
        
        for (String line : spotLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                boolean isOccupied = Boolean.parseBoolean(parts[1].trim());
                if (!isOccupied) {
                    String spotId = parts[0].trim();
                    JOptionPane.showMessageDialog(this, 
                        "Advise customer to use Spot: " + spotId);
                    return;
                }
            }
        }
        
        JOptionPane.showMessageDialog(this, "No free spots available!");
    }
}