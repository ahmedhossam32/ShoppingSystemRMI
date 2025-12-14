package Mainclasses;

import java.util.List;

public class Admin extends User {

    private String role;
    private Analytics analytics;

    public Admin(String id,
                 String name,
                 String email,
                 String password,
                 boolean gender,
                 int phoneNumber,
                 String role) {

        super(id, name, email, password, gender, phoneNumber);
        this.role = role;
        this.analytics = Analytics.getInstance();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void viewAnalytics(List<Order> allOrders) {
        System.out.println(analytics.generateSalesReport(allOrders));
        System.out.println("---------------------------------------");
        System.out.println(analytics.generateProductReport(allOrders));
    }
}
