package rmi_implementations;

import Database.DB;
import Mainclasses.Cart;
import Mainclasses.CartItem;
import Mainclasses.Customer;
import Mainclasses.DeliveryStaff;
import Mainclasses.Order;

import DesignPatterns.OrderStatus;
import DesignPatterns.PendingStatus;
import DesignPatterns.ConfirmedStatus;
import DesignPatterns.ShippedStatus;
import DesignPatterns.DeliveredStatus;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rmi.OrderInterface;

public class OrderService extends UnicastRemoteObject implements OrderInterface {

    private DB db;

    public OrderService() throws RemoteException {
        super();
        db = new DB();
    }

   @Override
public Order placeOrder(String customerId, List<CartItem> items) throws RemoteException {

    if (customerId == null) throw new RemoteException("customerId null");

    String orderId = UUID.randomUUID().toString();
    Order order = new Order();
    order.setId(orderId);
    order.setCustomerId(customerId);

    if (items == null) items = new ArrayList<>();
    order.setItems(new ArrayList<>(items)); // this recalculates total

    order.setStatusName("PENDING");

    db.insertOrder(order);

    return order;
}

    @Override
    public Order getOrderById(String orderId) throws RemoteException {
        return db.getOrder(orderId);
    }

    @Override
    public List<Order> getAllOrders() throws RemoteException {
        return db.getAllOrders();
    }

    @Override
    public List<Order> getOrdersForCustomer(String customerId) throws RemoteException {

        if (customerId == null) {
            throw new RemoteException("customerId cannot be null");
        }

        List<Order> allOrders = db.getAllOrders();
        List<Order> customerOrders = new ArrayList<Order>();

        if (allOrders != null) {
            for (Order o : allOrders) {
                if (o == null) continue;

                // ✅ filter by customerId (NOT o.getCustomer().getId())
                if (customerId.equals(o.getCustomerId())) {
                    customerOrders.add(o);
                }
            }
        }

        return customerOrders;
    }

    @Override
    public void updateOrderStatus(String orderId, String newStatusName) throws RemoteException {

        Order order = db.getOrder(orderId);
        if (order == null) {
            throw new RemoteException("Order not found: " + orderId);
        }

        if (newStatusName == null) {
            throw new RemoteException("Status name cannot be null");
        }

        OrderStatus newStatus;

        switch (newStatusName.toLowerCase()) {
            case "pending":
                newStatus = new PendingStatus();
                order.setStatusName("PENDING");
                break;
            case "confirmed":
                newStatus = new ConfirmedStatus();
                order.setStatusName("CONFIRMED");
                break;
            case "shipped":
                newStatus = new ShippedStatus();
                order.setStatusName("SHIPPED");
                break;
            case "delivered":
                newStatus = new DeliveredStatus();
                order.setStatusName("DELIVERED");
                break;
            default:
                throw new RemoteException("Invalid new order status: " + newStatusName);
        }

        order.setCurrentStatus(newStatus);

        db.updateOrder(order);
    }

    @Override
    public void assignDeliveryStaff(String orderId, DeliveryStaff staff) throws RemoteException {

        Order order = db.getOrder(orderId);
        if (order == null) {
            throw new RemoteException("Order not found: " + orderId);
        }

        if (staff == null || staff.getId() == null) {
            throw new RemoteException("Delivery staff (or staffId) cannot be null");
        }

        order.setDeliveryStaff(staff);

        // ✅ persist
        db.updateOrder(order);
    }

    @Override
    public List<String> getNotificationsForCustomer(String customerId) throws RemoteException {

        if (customerId == null) {
            throw new RemoteException("customerId cannot be null");
        }

        List<Order> allOrders = db.getAllOrders();
        List<String> notifications = new ArrayList<String>();

        if (allOrders != null) {
            for (Order o : allOrders) {
                if (o == null) continue;

                // ✅ compare by stored customerId
                if (customerId.equals(o.getCustomerId())) {

                    // ✅ use DB-friendly statusName
                    String status = (o.getStatusName() == null) ? "Unknown" : o.getStatusName();

                    notifications.add("Order " + o.getId() + " status: " + status);
                }
            }
        }

        return notifications;
    }

    @Override
    public Order processCheckout(String orderId, boolean paymentCompleted) throws RemoteException {

    if (orderId == null || orderId.trim().isEmpty()) {
        throw new RemoteException("orderId cannot be null/empty");
    }

    if (!paymentCompleted) {
        System.out.println("Payment failed - checkout cancelled");
        return null;
    }

    Order order = db.getOrder(orderId);
    if (order == null) {
        throw new RemoteException("Order not found: " + orderId);
    }

    List<DeliveryStaff> allStaff = db.getAllDeliveryStaff();
    if (allStaff == null || allStaff.isEmpty()) {
        throw new RemoteException("No delivery staff available");
    }

    DeliveryStaff firstStaff = allStaff.get(0);
    order.setDeliveryStaff(firstStaff);

    order.setStatusName("PENDING");

    db.updateOrder(order);
    return order;
}
}
