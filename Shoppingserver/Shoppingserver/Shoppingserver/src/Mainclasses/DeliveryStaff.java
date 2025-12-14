package Mainclasses;

import DesignPatterns.OrderObserver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeliveryStaff extends User implements Serializable, OrderObserver {

      private static final long serialVersionUID = 1L;  

    private List<Order> assignedOrders;

    public DeliveryStaff() {
        super();
        this.assignedOrders = new ArrayList<>();
    }

    public DeliveryStaff(String id,
                         String name,
                         String email,
                         String password,
                         boolean gender,
                         int phoneNumber) {

        super(id, name, email, password, gender, phoneNumber);
        this.assignedOrders = new ArrayList<>();
    }

    // ---------------- Delivery Logic ----------------

    public void assignOrder(Order order) {
        if (order == null) return;

        assignedOrders.add(order);
        order.setDeliveryStaff(this);
        order.attach(this); // observer pattern
    }

    public List<Order> viewAssignedOrders() {
        return assignedOrders;
    }

    @Override
    public void update(Order order) {
        System.out.println(
            "DeliveryStaff " + getName() +
            " notified: Order " + order.getId() +
            " status changed to " + order.getCurrentStatus()
        );
    }
}
