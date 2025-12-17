package operator;

import models.Ticket;
import utils.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExitOperatorGUI extends JFrame {
    private JTextField ticketIdField;
    private JTextArea calculationArea;
    
    public ExitOperatorGUI() {
        setTitle("Exit Operator Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Parking Exit Station");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Enter Ticket ID:"));
        ticketIdField = new JTextField();
        inputPanel.add(ticketIdField);
        
        JButton calculateButton = new JButton("Calculate Hours");
        JButton clearButton = new JButton("Clear");
        
        calculateButton.addActionListener(e -> calculateHours());
        clearButton.addActionListener(e -> clearFields());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        
        inputPanel.add(buttonPanel);
        
        // Calculation area
        calculationArea = new JTextArea(10, 40);
        calculationArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(calculationArea);
        
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void calculateHours() {
        String ticketId = ticketIdField.getText().trim();
        
        if (ticketId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Ticket ID");
            return;
        }
        
        // Find ticket
        List<String> ticketLines = FileUtil.readAllLines(Constants.TICKETS_FILE);
        Ticket ticket = null;
        
        for (String line : ticketLines) {
            String[] parts = line.split(",");
            if (parts.length >= 7 && parts[0].trim().equals(ticketId)) {
                ticket = new Ticket(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                                  parts[3].trim(), parts[4].trim(), 
                                  Double.parseDouble(parts[5].trim()),
                                  Boolean.parseBoolean(parts[6].trim()));
                break;
            }
        }
        
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Ticket not found!");
            return;
        }
        
        if (ticket.isPaid()) {
            calculationArea.setText("Ticket already paid.\n" +
                                  "Amount Paid: $" + String.format("%.2f", ticket.getAmountPaid()));
            return;
        }
        
        // Calculate and display
        long hours = ticket.getParkedHours();
        if (hours < 1) hours = 1; // Minimum charge
        
        double amountDue = hours * Constants.HOURLY_RATE;
        
        String calculation = "=== PARKING CALCULATION ===\n" +
                           "Ticket ID: " + ticketId + "\n" +
                           "Vehicle: " + ticket.getVehiclePlate() + "\n" +
                           "Spot: " + ticket.getSpotId() + "\n" +
                           "Entry Time: " + ticket.getEntryTime().format(
                               DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                           "Current Time: " + LocalDateTime.now().format(
                               DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                           "Hours Parked: " + hours + "\n" +
                           "Hourly Rate: $" + Constants.HOURLY_RATE + "\n" +
                           "Amount Due: $" + String.format("%.2f", amountDue) + "\n\n" +
                           "Please collect payment from customer.";
        
        calculationArea.setText(calculation);
    }
    
    private void clearFields() {
        ticketIdField.setText("");
        calculationArea.setText("");
    }
}