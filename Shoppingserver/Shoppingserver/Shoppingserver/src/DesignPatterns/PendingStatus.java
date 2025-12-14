package DesignPatterns;

import Mainclasses.Order;


public class PendingStatus extends OrderStatus {

   
    @Override
    public void confirm(Order order) {
       order.setCurrentStatus(new ConfirmedStatus());
        System.out.println("Order confirmed.");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Cannot ship. Order is pending.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Cannot deliver. Order is pending.");
    }
    
    @Override
    public void pending(Order order) {
        System.out.println("Cannot deliver. Order is pending.");
    }
    
    
}
