package shoppingclient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import controllers.LoginPageController;
import gui.LoginPage;

import rmi.UserInterface;
import rmi.ProductInterface;
import rmi.OrderInterface;
import rmi.DeliverystaffInterface;
import rmi.PaymentInterface;
import rmi.AdminProductInterface;

import Mainclasses.User;
import Mainclasses.DeliveryStaff;

public class ShoppingClient {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 3000);

            AdminProductInterface adminService =
                    (AdminProductInterface) registry.lookup("AdminService");

            DeliverystaffInterface deliveryService =
                    (DeliverystaffInterface) registry.lookup("DeliveryStaffService");

            OrderInterface orderService =
                    (OrderInterface) registry.lookup("OrderService");

            PaymentInterface paymentService =
                    (PaymentInterface) registry.lookup("PaymentService");

            ProductInterface productService =
                    (ProductInterface) registry.lookup("ProductService");

            UserInterface userService =
                    (UserInterface) registry.lookup("UserService");

            System.out.println("Client connected to all RMI services successfully.");

            
            LoginPage loginGui = new LoginPage();
            new LoginPageController(loginGui, userService);
            loginGui.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
