package models;

public class Customer extends User {
    private String vehiclePlate;
    
    public Customer(String username, String password, String vehiclePlate) {
        super(username, password, "CUSTOMER");
        this.vehiclePlate = vehiclePlate;
    }
    
    public String getVehiclePlate() { return vehiclePlate; }
    
    @Override
    public void performRoleAction() {
        System.out.println("Customer parking vehicle...");
    }
}