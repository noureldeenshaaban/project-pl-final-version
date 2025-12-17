package models;

public class EntryOperator extends User {
    public EntryOperator(String username, String password) {
        super(username, password, "ENTRY_OPERATOR");
    }
    
    @Override
    public void performRoleAction() {
        System.out.println("Entry operator managing entry...");
    }
}