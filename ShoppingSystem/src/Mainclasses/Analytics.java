package Mainclasses;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analytics implements Serializable {

    private static Analytics instance;

    private Analytics() {}

    public static synchronized Analytics getInstance() {
        if (instance == null)
            instance = new Analytics();
        return instance;
    }

    
    public String generateSalesReport(List<Order> orders) {

        int orderCount = 0;
        double totalRevenue = 0;

        if (orders != null) {
            for (Order order : orders) {
                
                    orderCount++;
                    totalRevenue += order.getTotalAmount();
                
            }
        }

        return " SALES REPORT\n"
                + "Delivered Orders: " + orderCount + "\n"
                + "Total Revenue: " + totalRevenue;
    }

    // --------------------------------------------------
    // Product Report: most ordered product and total revenue generated
    // --------------------------------------------------
    public String generateProductReport(List<Order> orders) {

        Map<String, Integer> productCount = new HashMap<>();
        Map<String, Double> productRevenue = new HashMap<>();

        if (orders != null) {
            for (Order order : orders) {
                for (CartItem ci : order.getItems()) {

                    String pid = ci.getProductId();
                    int qty = ci.getQuantity();
                    double price = ci.getProductPrice();  // You will add getter

                    productCount.put(pid, productCount.getOrDefault(pid, 0) + qty);
                    productRevenue.put(pid,
                            productRevenue.getOrDefault(pid, 0.0) + (qty * price));
                }
            }
        }

        // Find most-used product
        String mostUsedProduct = null;
        int maxQuantity = 0;
        double revenue = 0;

        for (String pid : productCount.keySet()) {
            int qty = productCount.get(pid);
            if (qty > maxQuantity) {
                maxQuantity = qty;
                mostUsedProduct = pid;
                revenue = productRevenue.get(pid);
            }
        }

        if (mostUsedProduct == null)
            return "No products found in orders.";

        return " PRODUCT REPORT\n"
                + "Most Ordered Product ID: " + mostUsedProduct + "\n"
                + "Total Quantity Ordered: " + maxQuantity + "\n"
                + "Total Revenue From Product: " + revenue;
    }
}