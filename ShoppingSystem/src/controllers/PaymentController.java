package controllers;

import gui.Payment;
import gui.CreditCard;
import gui.InstaPay;
import gui.CustomerPage;

import java.rmi.registry.Registry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import Mainclasses.Customer;
import Mainclasses.Order;
import rmi.UserInterface;
import rmi.OrderInterface;

public class PaymentController {

    private Payment gui;
    private Registry registry;
    private Customer customer;
    private UserInterface userService;

    public PaymentController(Payment gui, Registry registry, Customer customer, UserInterface userService) {
        this.gui = gui;
        this.registry = registry;
        this.customer = customer;
        this.userService = userService;

        gui.getContinueToCheckoutButton().addActionListener(new ContinueAction());
    }

    class ContinueAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (gui.getCreditCardRadioButton().isSelected()) {
                    CreditCard cc = new CreditCard();
                    new CreditCardController(cc, registry, customer, userService);
                    cc.setLocationRelativeTo(null);
                    cc.setVisible(true);
                    gui.dispose();

                } else if (gui.getInstapayRadioButton().isSelected()) {
                    InstaPay ip = new InstaPay();
                    new InstaPayController(ip, registry, customer, userService);
                    ip.setLocationRelativeTo(null);
                    ip.setVisible(true);
                    gui.dispose();

                } else if (gui.getCashOnDeliveryRadioButton().isSelected()) {

                    OrderInterface orderService = (OrderInterface) registry.lookup("OrderService");

                    // ✅ 1) Create + save order
                    Order created = orderService.placeOrder(customer.getId(), customer.getCart().getItems());

                    // ✅ 2) Finalize checkout for SAME order
                    orderService.processCheckout(created.getId(), true);

                    // ✅ Clear cart locally
                    customer.getCart().getItems().clear();

                    JOptionPane.showMessageDialog(gui,
                            "Order placed successfully! Pay on delivery.",
                            "COD",
                            JOptionPane.INFORMATION_MESSAGE);

                    CustomerPage page = new CustomerPage();
                    new CustomerPageController(page, customer, userService, registry);
                    page.setLocationRelativeTo(null);
                    page.setVisible(true);

                    gui.dispose();

                } else {
                    JOptionPane.showMessageDialog(gui, "Please select a payment method first.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(gui, "Server error: " + ex.getMessage());
            }
        }
    }
}
