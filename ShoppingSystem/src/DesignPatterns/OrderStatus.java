package DesignPatterns;

import Mainclasses.Order;
import java.io.Serializable;



public abstract class OrderStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract void confirm(Order order);
    public abstract void ship(Order order);
    public abstract void deliver(Order order);
    public abstract void pending(Order order);
    
}
