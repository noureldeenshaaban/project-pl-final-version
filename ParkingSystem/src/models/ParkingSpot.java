package models;

public class ParkingSpot {
    private String spotId;
    private boolean isOccupied;
    private String occupiedByTicketId;
    
    public ParkingSpot(String spotId) {
        this.spotId = spotId;
        this.isOccupied = false;
        this.occupiedByTicketId = "";
    }
    
    public ParkingSpot(String spotId, boolean isOccupied, String occupiedByTicketId) {
        this.spotId = spotId;
        this.isOccupied = isOccupied;
        this.occupiedByTicketId = occupiedByTicketId;
    }
    
    // Getters and Setters
    public String getSpotId() { return spotId; }
    public boolean isOccupied() { return isOccupied; }
    public String getOccupiedByTicketId() { return occupiedByTicketId; }
    
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }
    public void setOccupiedByTicketId(String ticketId) { this.occupiedByTicketId = ticketId; }
    
    @Override
    public String toString() {
        return spotId + "," + isOccupied + "," + occupiedByTicketId;
    }
}