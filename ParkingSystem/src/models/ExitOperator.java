package models;

public class ExitOperator extends User {
    public ExitOperator(String username, String password) {
        super(username, password, "EXIT_OPERATOR");
    }
    
    @Override
    public void performRoleAction() {
        System.out.println("Exit operator managing exit...");
    }
}