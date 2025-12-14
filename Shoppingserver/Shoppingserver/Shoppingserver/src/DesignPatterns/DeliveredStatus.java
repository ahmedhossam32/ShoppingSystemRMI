package DesignPatterns;

import Mainclasses.Order;


public class DeliveredStatus extends OrderStatus {

    @Override
    public void confirm(Order order) {
        System.out.println("Order already delivered.");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Order already delivered.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Order already delivered.");
    }

    @Override
    public void pending(Order order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
