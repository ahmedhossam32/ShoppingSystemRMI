package rmi;

import Mainclasses.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface OrderInterface extends Remote {

Order placeOrder(String customerId, List<CartItem> items) throws RemoteException;

    Order getOrderById(String orderId) throws RemoteException;

    List<Order> getAllOrders() throws RemoteException;

    List<Order> getOrdersForCustomer(String customerId) throws RemoteException;

    void updateOrderStatus(String orderId, String newStatusName) throws RemoteException;

    void assignDeliveryStaff(String orderId, DeliveryStaff staff) throws RemoteException;
    
    List<String> getNotificationsForCustomer(String customerId) throws RemoteException;
Order processCheckout(String orderId, boolean paymentCompleted) throws RemoteException;
}
