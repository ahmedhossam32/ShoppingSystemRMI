package Mainclasses;

import java.util.ArrayList;
import java.util.List;
import DesignPatterns.OrderObserver;

public class Customer extends User implements OrderObserver {

    private String address;
    private transient  Cart cart;
    private transient  List<Order> orders;
    private transient  List<String> notifications;

    public Customer() {
        super();
        this.orders = new ArrayList<>();
        this.notifications = new ArrayList<>();

        this.cart = new Cart();
        this.cart.setCustomer(this); 
    }

    public Customer(String id,
                    String name,
                    String email,
                    String password,
                    boolean gender,
                    int phoneNumber,
                    String address) {
        super(id, name, email, password, gender, phoneNumber);
        this.address = address;

        this.orders = new ArrayList<>();
        this.notifications = new ArrayList<>();

        this.cart = new Cart("CART-" + id, this);
    }

    // Delegate cart operations to Cart class
    public void addToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) return;
        cart.addItem(product, quantity);
    }

    public void removeFromCart(Product product) {
        if (product == null) return;
        cart.removeItem(product);
    }

    public void updateCart(Product product, int quantity) {
        if (product == null) return;
        cart.updateItem(product, quantity);
    }

    public String trackOrder(Order order) {
        if (order == null || order.getCurrentStatus() == null) return "Unknown";
        return order.getCurrentStatus().toString();
    }

    public void addOrder(Order order) {
        if (order == null) return;
        if (!orders.contains(order)) orders.add(order);
    }

    @Override
    public void update(Order order) {
        if (order == null) return;

        String message = "Order " + order.getId()
                + " status updated to: " + trackOrder(order);

        notifications.add(message);
        addOrder(order);
    }

    // Getters & setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) {
        this.cart = (cart == null) ? new Cart() : cart; 
        this.cart.setCustomer(this);                    
    }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) {
        this.orders = (orders == null) ? new ArrayList<>() : orders;
    }

    public List<String> getNotifications() { return notifications; }
    public void setNotifications(List<String> notifications) {
        this.notifications = (notifications == null) ? new ArrayList<>() : notifications;
    }
}
