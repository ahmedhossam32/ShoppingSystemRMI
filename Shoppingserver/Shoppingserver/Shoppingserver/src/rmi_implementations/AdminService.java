package rmi_implementations;

import Database.DB;
import Mainclasses.CartItem;
import Mainclasses.Order;
import Mainclasses.Product;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rmi.AdminInterface;
import rmi.AdminProductInterface;
import rmi.ProductviewFacade;

        
public class AdminService extends UnicastRemoteObject implements AdminProductInterface , ProductviewFacade, AdminInterface{
    private DB db;

    public AdminService() throws RemoteException 
    {
        db = new DB();
    }
    
    @Override
    public void Update_Product(Product product) throws RemoteException {
        if (db == null) db = new DB();
        db.updateProduct(product);
        System.out.println("Admin updated product: " + product.getProductId());
    }

    @Override
    public void delete_Product(Product product) throws RemoteException {
        if (db == null) db = new DB();
        db.deleteProduct(product.getProductId());
        System.out.println("Admin deleted product: " + product.getProductId());
    }

    @Override
    public void Apply_Discount(Product product, double discount) throws RemoteException {
        if (db == null) db = new DB();
        double newPrice = product.getPrice() - (product.getPrice() * (discount / 100));
        product.setPrice(newPrice);
        db.updateProduct(product);
        System.out.println("Discount applied. New Price: " + newPrice);
    }
    @Override 
    public void Add_Product(Product product) 
     {
        if (db == null) db = new DB();
        db.insertProduct(product);
        System.out.println("Admin inserted product: " + product.getProductId());
     }

    @Override
    public String getProductDetails(String productId) throws RemoteException {
        Product d = db.getProduct(productId);
        return "name:"+d.getName()+"ID:"+d.getProductId()+"Price:"+d.getPrice()+"description:"+d.getDescription();
    }

    @Override
    public String View_analytics() throws RemoteException {

    DB db = new DB();
    List<Order> orders = db.getAllOrders();

    int orderCount = 0;
    double totalRevenue = 0;

    Map<String, Integer> productCount = new HashMap<>();
    Map<String, Double> productRevenue = new HashMap<>();

   
    if (orders != null) {
        for (Order order : orders) {

            orderCount++;
            totalRevenue += order.getTotalAmount();

            if (order.getItems() != null) {
                for (CartItem ci : order.getItems()) {

                    String pid = ci.getProductId();
                    int qty = ci.getQuantity();
                    double price = ci.getProductPrice();

                    productCount.put(pid, productCount.getOrDefault(pid, 0) + qty);
                    productRevenue.put(pid,
                            productRevenue.getOrDefault(pid, 0.0) + (qty * price));
                }
            }
        }
    }

   
    String mostUsedProduct = null;
    int maxQuantity = 0;
    double maxRevenue = 0;

    for (String pid : productCount.keySet()) {
        int qty = productCount.get(pid);
        if (qty > maxQuantity) {
            maxQuantity = qty;
            mostUsedProduct = pid;
            maxRevenue = productRevenue.get(pid);
        }
    }

   
    StringBuilder report = new StringBuilder();
    report.append("===== SALES REPORT =====\n");
    report.append("Total Orders: ").append(orderCount).append("\n");
    report.append("Total Revenue: ").append(String.format("%.2f", totalRevenue)).append(" EGP\n");
    report.append("\n");

    report.append("===== PRODUCT REPORT =====\n");

    if (mostUsedProduct == null) {
        report.append("No products found in orders.\n");
    } else {
        report.append("Most Ordered Product ID: ").append(mostUsedProduct).append("\n");
        report.append("Total Quantity Ordered: ").append(maxQuantity).append("\n");
        report.append("Total Revenue From Product: ").append(String.format("%.2f", maxRevenue)).append(" EGP\n");
    }
    
    System.out.println(report.toString());
    
    return report.toString();
}

}