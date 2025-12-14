package controllers;

import gui.InstaPay;
import gui.CustomerPage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.Registry;

import javax.swing.JOptionPane;

import Mainclasses.Customer;
import Mainclasses.Order;
import rmi.PaymentInterface;
import rmi.UserInterface;
import rmi.OrderInterface;

public class InstaPayController {

    private InstaPay gui;
    private Registry registry;
    private Customer customer;
    private UserInterface userService;

    public InstaPayController(InstaPay gui, Registry registry, Customer customer, UserInterface userService) {
        this.gui = gui;
        this.registry = registry;
        this.customer = customer;
        this.userService = userService;

        gui.getPayButton().addActionListener(new PayAction());
    }

    class PayAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                PaymentInterface payment = (PaymentInterface) registry.lookup("PaymentService");

                payment.setInstapayDetails(gui.getInstaPayIDField().getText());
                payment.setStrategy("instapay");

                boolean result = payment.processPayment(100.0);

                if (!result) {
                    JOptionPane.showMessageDialog(gui, "Payment failed!");
                    return;
                }

                // ✅ placeOrder ثم processCheckout لنفس الطلب
                OrderInterface orderService = (OrderInterface) registry.lookup("OrderService");

                Order created = orderService.placeOrder(customer.getId(), customer.getCart().getItems());
                orderService.processCheckout(created.getId(), true);

                // ✅ clear cart locally
                customer.getCart().getItems().clear();

                JOptionPane.showMessageDialog(gui, "Payment successful! Order saved.");

                CustomerPage page = new CustomerPage();
                new CustomerPageController(page, customer, userService, registry);
                page.setLocationRelativeTo(null);
                page.setVisible(true);

                gui.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(gui, "Server error: " + ex.getMessage());
            }
        }
    }
}
