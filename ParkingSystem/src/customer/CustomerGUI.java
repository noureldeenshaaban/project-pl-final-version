package customer;

import models.Ticket;
import models.User;
import system.SessionManager;
import utils.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class CustomerGUI extends JFrame {
    private JLabel welcomeLabel;
    private JButton printTicketButton;
    private JButton payTicketButton;
    private JTextArea ticketInfoArea;
    
    public CustomerGUI() {
        setTitle("Customer Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        
        // Display welcome message
        User user = SessionManager.getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getUsername());
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Welcome
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel);
        
        // Center panel - Ticket info
        ticketInfoArea = new JTextArea(10, 40);
        ticketInfoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ticketInfoArea);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        printTicketButton = new JButton("Print Entry Ticket");
        payTicketButton = new JButton("Pay for Parking");
        
        printTicketButton.addActionListener(e -> printTicket());
        payTicketButton.addActionListener(e -> payForParking());
        
        buttonPanel.add(printTicketButton);
        buttonPanel.add(payTicketButton);
        
        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void printTicket() {
        User user = SessionManager.getCurrentUser();
        if (!(user instanceof models.Customer)) {
            JOptionPane.showMessageDialog(this, "Invalid user type");
            return;
        }
        
        models.Customer customer = (models.Customer) user;
        
        // Find available spot
        List<String> spotLines = FileUtil.readAllLines(Constants.SPOTS_FILE);
        String availableSpot = null;
        
        for (String line : spotLines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                boolean isOccupied = Boolean.parseBoolean(parts[1].trim());
                if (!isOccupied) {
                    availableSpot = parts[0].trim();
                    break;
                }
            }
        }
        
        if (availableSpot == null) {
            JOptionPane.showMessageDialog(this, "No parking spots available!");
            return;
        }
        
        // Generate ticket ID
        String ticketId = "TKT" + System.currentTimeMillis() % 10000;
        
        // Create ticket
        Ticket ticket = new Ticket(ticketId, availableSpot, customer.getVehiclePlate());
        
        // Save ticket
        FileUtil.appendLine(Constants.TICKETS_FILE, ticket.toString());
        
        // Update spot as occupied
        for (int i = 0; i < spotLines.size(); i++) {
            String[] parts = spotLines.get(i).split(",");
            if (parts[0].trim().equals(availableSpot)) {
                spotLines.set(i, availableSpot + ",true," + ticketId);
                break;
            }
        }
        FileUtil.writeAllLines(Constants.SPOTS_FILE, spotLines);
        
        // Display ticket info
        String ticketInfo = "=== PARKING TICKET ===\n" +
                          "Ticket ID: " + ticketId + "\n" +
                          "Vehicle Plate: " + customer.getVehiclePlate() + "\n" +
                          "Spot ID: " + availableSpot + "\n" +
                          "Entry Time: " + ticket.getEntryTime().format(
                              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                          "\nPlease keep this ticket for exit!";
        
        ticketInfoArea.setText(ticketInfo);
        
        JOptionPane.showMessageDialog(this, "Ticket printed successfully!\nTicket ID: " + ticketId);
    }
    
    private void payForParking() {
        String ticketId = JOptionPane.showInputDialog(this, "Enter your Ticket ID:");
        
        if (ticketId == null || ticketId.trim().isEmpty()) {
            return;
        }
        
        // Find ticket
        List<String> ticketLines = FileUtil.readAllLines(Constants.TICKETS_FILE);
        Ticket ticket = null;
        int ticketIndex = -1;
        
        for (int i = 0; i < ticketLines.size(); i++) {
            String[] parts = ticketLines.get(i).split(",");
            if (parts.length >= 7 && parts[0].trim().equals(ticketId.trim())) {
                ticket = new Ticket(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                                  parts[3].trim(), parts[4].trim(), 
                                  Double.parseDouble(parts[5].trim()),
                                  Boolean.parseBoolean(parts[6].trim()));
                ticketIndex = i;
                break;
            }
        }
        
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Ticket not found!");
            return;
        }
        
        if (ticket.isPaid()) {
            JOptionPane.showMessageDialog(this, "Ticket already paid!");
            return;
        }
        
        // Calculate amount due
        double amountDue = ticket.calculateAmountDue();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Amount due: $" + String.format("%.2f", amountDue) + 
            "\n\nConfirm payment?", "Payment Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Update ticket with payment
            ticket.setExitTime(LocalDateTime.now());
            ticket.setAmountPaid(amountDue);
            
            ticketLines.set(ticketIndex, ticket.toString());
            FileUtil.writeAllLines(Constants.TICKETS_FILE, ticketLines);
            
            // Free up the parking spot
            List<String> spotLines = FileUtil.readAllLines(Constants.SPOTS_FILE);
            for (int i = 0; i < spotLines.size(); i++) {
                String[] parts = spotLines.get(i).split(",");
                if (parts.length >= 3 && parts[2].trim().equals(ticketId)) {
                    spotLines.set(i, parts[0].trim() + ",false,");
                    break;
                }
            }
            FileUtil.writeAllLines(Constants.SPOTS_FILE, spotLines);
            
            JOptionPane.showMessageDialog(this, 
                "Payment successful!\nThank you for using our parking service.");
            
            // Display receipt
            String receipt = "=== PAYMENT RECEIPT ===\n" +
                           "Ticket ID: " + ticketId + "\n" +
                           "Vehicle: " + ticket.getVehiclePlate() + "\n" +
                           "Parking Spot: " + ticket.getSpotId() + "\n" +
                           "Entry Time: " + ticket.getEntryTime().format(
                               DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                           "Exit Time: " + ticket.getExitTime().format(
                               DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                           "Hours Parked: " + ticket.getParkedHours() + "\n" +
                           "Amount Paid: $" + String.format("%.2f", amountDue) + "\n" +
                           "\nHave a safe journey!";
            
            ticketInfoArea.setText(receipt);
        }
    }
}