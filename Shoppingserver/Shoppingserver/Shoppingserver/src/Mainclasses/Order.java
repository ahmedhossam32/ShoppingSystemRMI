package Mainclasses;

import DesignPatterns.OrderObserver;
import DesignPatterns.OrderSubject;
import DesignPatterns.OrderStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable, OrderSubject {

    private static final long serialVersionUID = 1L;

    private String id;
    private String customerId;
    private Customer customer;

    private DeliveryStaff deliveryStaff;
    private List<CartItem> items;
    private double totalAmount;

    private String statusName;

    private transient OrderStatus currentStatus;
    private transient List<OrderObserver> observers;

    public Order() {
        this.items = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.statusName = "PENDING";
    }

    public Order(String id, Customer customer) {
        this();
        this.id = id;
        this.customer = customer;
        if (customer != null) this.customerId = customer.getId();
        recalculateTotal();
    }

    public Order(String orderId, Customer customer, DeliveryStaff assignedStaff, Cart cart) {
        this(); 
        this.id = orderId;
        this.customer = customer;
        this.deliveryStaff = assignedStaff;

        if (customer != null) this.customerId = customer.getId();

        this.items = (cart == null || cart.getItems() == null)
                ? new ArrayList<>()
                : new ArrayList<>(cart.getItems());

        recalculateTotal();
        this.statusName = "PENDING";
    }

    public void recalculateTotal() {
        double sum = 0;
        if (items != null) {
            for (CartItem ci : items) {
                sum += ci.getProductPrice() * ci.getQuantity();
            }
        }
        this.totalAmount = sum;
    }

    public void setCurrentStatus(OrderStatus status) {
        this.currentStatus = status;
        this.statusName = (status == null) ? "UNKNOWN" : status.getClass().getSimpleName().toUpperCase();
        notifyObservers();
    }

    public OrderStatus getCurrentStatus() { return currentStatus; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public void changeStatus(OrderStatus st, String name) {
        this.currentStatus = st;
        this.statusName = name;
        notifyObservers();
    }

    @Override
    public void attach(OrderObserver observer) {
        if (observers == null) observers = new ArrayList<>();
        if (observer != null && !observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void detach(OrderObserver observer) {
        if (observers != null) observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        if (observers == null) return;
        for (OrderObserver o : observers) {
            try { o.update(this); } catch (Exception ignored) {}
        }
    }

    // ---------------- Getters/Setters ----------------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) this.customerId = customer.getId();
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public DeliveryStaff getDeliveryStaff() { return deliveryStaff; }
    public void setDeliveryStaff(DeliveryStaff deliveryStaff) { this.deliveryStaff = deliveryStaff; }

    public void setItems(List<CartItem> items) {
        this.items = (items == null) ? new ArrayList<CartItem>() : new ArrayList<>(items);
        recalculateTotal();
    }

    public List<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
}
