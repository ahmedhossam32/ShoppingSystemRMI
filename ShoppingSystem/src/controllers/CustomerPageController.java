package controllers;

import gui.CustomerPage;
import gui.LoginPage;
import gui.ViewProducts;
import gui.ViewCart;
import gui.MyOrder;

import java.lang.reflect.Field;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import Mainclasses.Customer;
import rmi.UserInterface;
import rmi.ProductInterface;
import rmi.OrderInterface;

public class CustomerPageController {

    private final CustomerPage gui;
    private final UserInterface userService;
    private final Customer customer;

    private ProductInterface productService;
    private OrderInterface orderService;
    private Registry registry;

    private JLabel nameLabel;
    private JButton viewproducts;
    private JButton viewcart;
    private JButton makeorder;
    private JButton myorder;
    private JButton logout;

    public CustomerPageController(CustomerPage gui, Customer customer, UserInterface userService, Registry registry) {
        this.gui = gui;
        this.customer = customer;
        this.userService = userService;
        this.registry = registry;

        wireComponentsByReflection();
        initRMI();

        nameLabel.setText(customer.getName());

        viewproducts.addActionListener(new ViewProductsAction());
        viewcart.addActionListener(new ViewCartAction());
        makeorder.addActionListener(new ViewCartAction());
        myorder.addActionListener(new MyOrderAction());
        logout.addActionListener(new LogoutAction());
    }

    private void initRMI() {
        try {
            if (registry == null) {
                registry = LocateRegistry.getRegistry("localhost", 3000);
            }
            productService = (ProductInterface) registry.lookup("ProductService");
            orderService   = (OrderInterface) registry.lookup("OrderService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gui, "Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void wireComponentsByReflection() {
        try {
            nameLabel    = (JLabel) getPrivateField(gui, "name");
            viewproducts = (JButton) getPrivateField(gui, "viewproducts");
            viewcart     = (JButton) getPrivateField(gui, "viewcart1");
            makeorder    = (JButton) getPrivateField(gui, "makeorder");
            myorder      = (JButton) getPrivateField(gui, "myorder");
            logout       = (JButton) getPrivateField(gui, "logout");
        } catch (Exception e) {
            throw new RuntimeException("Failed to wire CustomerPage components", e);
        }
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    // ================= ACTIONS =================

    private class ViewProductsAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ViewProducts page = new ViewProducts();
            new ViewProductsController(page, customer, productService, orderService, userService, registry);
            page.setVisible(true);
            gui.dispose();
        }
    }

    private class ViewCartAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ViewCart page = new ViewCart();
            new ViewCartController(page, customer, orderService, userService, registry);
            page.setVisible(true);
            gui.dispose();
        }
    }

    private class MyOrderAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyOrder page = new MyOrder();
            new MyOrderController(page, customer, orderService, userService, registry);
            page.setVisible(true);
            gui.dispose();
        }
    }

    private class LogoutAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LoginPage login = new LoginPage();
            new LoginPageController(login, userService);
            login.setVisible(true);
            gui.dispose();
        }
    }
}
