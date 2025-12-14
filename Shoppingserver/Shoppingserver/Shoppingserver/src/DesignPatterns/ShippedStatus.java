package DesignPatterns;

import Mainclasses.Order;

public class ShippedStatus extends OrderStatus {

    @Override
    public void confirm(Order order) {
        System.out.println("Order already shipped. Cannot confirm.");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Order already shipped.");
    }

    @Override
    public void deliver(Order order) {
        order.setCurrentStatus(new DeliveredStatus());
        System.out.println("Order delivered.");
    }

    @Override
    public void pending(Order order) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
