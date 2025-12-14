package rmi;

import DesignPatterns.OrderStatus;
import Mainclasses.Order;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DeliverystaffInterface extends Remote {

    List<Order> viewAssignedOrders(String deliveryStaffId) throws RemoteException;
    void updateDeliveryStatus(String deliveryStaffId, Order order, OrderStatus status) throws RemoteException;
}
