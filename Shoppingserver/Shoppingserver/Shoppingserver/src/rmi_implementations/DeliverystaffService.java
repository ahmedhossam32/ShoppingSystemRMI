package rmi_implementations;

import Database.DB;
import DesignPatterns.OrderStatus;
import Mainclasses.Order;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import rmi.DeliverystaffInterface;

public class DeliverystaffService extends UnicastRemoteObject implements DeliverystaffInterface {

    private DB db;

    public DeliverystaffService() throws RemoteException {
        super();
        this.db = new DB();
    }

    @Override
    public List<Order> viewAssignedOrders(String deliveryStaffId) throws RemoteException {

        if (deliveryStaffId == null || deliveryStaffId.trim().isEmpty()) {
            throw new RemoteException("deliveryStaffId is required");
        }

        List<Order> allOrders = db.getAllOrders();
        List<Order> assigned = new ArrayList<>();

        if (allOrders == null) return assigned;

        for (Order o : allOrders) {
            if (o.getDeliveryStaff() != null
                    && o.getDeliveryStaff().getId() != null
                    && o.getDeliveryStaff().getId().equals(deliveryStaffId)) {
                assigned.add(o);
            }
        }
        return assigned;
    }

    @Override
    public void updateDeliveryStatus(String deliveryStaffId, Order order, OrderStatus status) throws RemoteException {

        if (deliveryStaffId == null || deliveryStaffId.trim().isEmpty()) {
            throw new RemoteException("deliveryStaffId is required");
        }
        if (order == null) {
            throw new RemoteException("Order cannot be null");
        }
        if (status == null) {
            throw new RemoteException("Status cannot be null");
        }

        if (order.getDeliveryStaff() == null
                || order.getDeliveryStaff().getId() == null
                || !order.getDeliveryStaff().getId().equals(deliveryStaffId)) {
            throw new RemoteException("This order is not assigned to this delivery staff");
        }

        order.setCurrentStatus(status);
        db.updateOrder(order);
    }
}
