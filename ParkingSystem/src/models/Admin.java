package models;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "ADMIN");
    }
    
    @Override
    public void performRoleAction() {
        System.out.println("Admin managing system...");
    }
}