package DesignPatterns;

import Mainclasses.Order;


public class ConfirmedStatus extends OrderStatus {

    @Override
    public void confirm(Order order) {
        System.out.println("Order already confirmed.");
    }

    @Override
    public void ship(Order order) {
        order.setCurrentStatus(new ShippedStatus());
        System.out.println("Order shipped.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Cannot deliver. Order must be shipped first.");
    }

    @Override
    public void pending(Order order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
