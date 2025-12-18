package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Ticket {
    private String ticketId;
    private String spotId;
    private String vehiclePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double amountPaid;
    private boolean isPaid;
    
    public Ticket(String ticketId, String spotId, String vehiclePlate) {
        this.ticketId = ticketId;
        this.spotId = spotId;
        this.vehiclePlate = vehiclePlate;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.amountPaid = 0.0;
        this.isPaid = false;
    }
    
    public Ticket(String ticketId, String spotId, String vehiclePlate, 
                  String entryTime, String exitTime, double amountPaid, boolean isPaid) {
        this.ticketId = ticketId;
        this.spotId = spotId;
        this.vehiclePlate = vehiclePlate;
        this.entryTime = LocalDateTime.parse(entryTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.exitTime = exitTime != null && !exitTime.isEmpty() ? 
                       LocalDateTime.parse(exitTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        this.amountPaid = amountPaid;
        this.isPaid = isPaid;
    }
    
    
    public long getParkedHours() {
        if (exitTime == null) {
            return ChronoUnit.HOURS.between(entryTime, LocalDateTime.now());
        }
        return ChronoUnit.HOURS.between(entryTime, exitTime);
    }
    
    
    public double calculateAmountDue() {
        long hours = getParkedHours();
        if (hours < 1) hours = 1; 
        return hours * 5.0; 
    }
    
    
    public String getTicketId() { return ticketId; }
    public String getSpotId() { return spotId; }
    public String getVehiclePlate() { return vehiclePlate; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getAmountPaid() { return amountPaid; }
    public boolean isPaid() { return isPaid; }
    
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setAmountPaid(double amountPaid) { 
        this.amountPaid = amountPaid; 
        this.isPaid = true;
    }
    
    @Override
    public String toString() {
        String exitTimeStr = exitTime != null ? exitTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
        return ticketId + "," + spotId + "," + vehiclePlate + "," + 
               entryTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," + 
               exitTimeStr + "," + amountPaid + "," + isPaid;
    }
}