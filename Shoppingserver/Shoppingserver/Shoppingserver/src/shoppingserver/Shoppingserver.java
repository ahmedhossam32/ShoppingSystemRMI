package shoppingserver;


import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import rmi.UserInterface;
import rmi.ProductInterface;
import rmi.OrderInterface;
import rmi.DeliverystaffInterface;
import rmi.PaymentInterface;

import rmi_implementations.AdminService;
import rmi_implementations.UserService;
import rmi_implementations.ProductService;
import rmi_implementations.OrderService;
import rmi_implementations.DeliverystaffService;
import rmi_implementations.PaymentService;
import rmi.AdminProductInterface;

public class Shoppingserver {

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        

        Registry registry = LocateRegistry.createRegistry(3000);

        AdminProductInterface adminService = new AdminService();   
        DeliverystaffInterface deliveryService = new DeliverystaffService();
        OrderInterface orderService = new OrderService();
        PaymentInterface paymentService = new PaymentService();
        ProductInterface productService = new ProductService();
        UserInterface userService = new UserService();

        
        registry.rebind("AdminService", adminService);
        registry.rebind("DeliveryStaffService", deliveryService);
        registry.rebind("OrderService", orderService);
        registry.rebind("PaymentService", paymentService);     
        registry.rebind("ProductService", productService);
        registry.rebind("UserService", userService);
        


        System.out.println("Shopping server is up and RMI services are bound on port 3000.");

       
    }
}
