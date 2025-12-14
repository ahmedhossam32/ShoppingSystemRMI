package controllers;

import gui.ViewCart;
import gui.Payment;

import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Mainclasses.Cart;
import Mainclasses.CartItem;
import Mainclasses.Customer;

import rmi.OrderInterface;
import rmi.UserInterface;

import java.rmi.registry.Registry;

public class ViewCartController {

    private final ViewCart gui;
    private final Customer customer;
    private final OrderInterface orderService;
    private final UserInterface userService;
    private final Registry registry;

    private JTable cartTable;
    private JLabel nameLabel;
    private JLabel totalLabel;
    private JButton confirmBtn;

    private DefaultTableModel model;

    public ViewCartController(ViewCart gui,
                              Customer customer,
                              OrderInterface orderService,
                              UserInterface userService,
                              Registry registry) {

        this.gui = gui;
        this.customer = customer;
        this.orderService = orderService;
        this.userService = userService;
        this.registry = registry;

        wireByReflection();
        setupTableModel();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadCart();
            }
        });

        confirmBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmOrder();
            }
        });
    }

    private void wireByReflection() {
        try {
            cartTable  = (JTable) getPrivateField(gui, "mycarttabel");
            nameLabel  = (JLabel) getPrivateField(gui, "name");
            totalLabel = (JLabel) getPrivateField(gui, "gettotal");
            confirmBtn = (JButton) getPrivateField(gui, "Confrim");
        } catch (Exception e) {
            throw new RuntimeException("ViewCartController wiring failed. Check variable names in ViewCart.", e);
        }

        if (customer != null && customer.getName() != null) {
            nameLabel.setText(customer.getName());
        }

        if (customer != null && customer.getCart() == null) {
            customer.setCart(new Cart());
        }
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    private void setupTableModel() {
        model = new DefaultTableModel(new Object[]{"Product", "Price", "QTY", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable.setModel(model);
    }

    private void loadCart() {
        model.setRowCount(0);

        double total = 0.0;
        if (customer == null) {
            totalLabel.setText("0.0");
            return;
        }

        Cart cart = customer.getCart();
        if (cart != null && cart.getItems() != null) {
            for (CartItem it : cart.getItems()) {
                double subtotal = it.getProductPrice() * it.getQuantity();
                total += subtotal;

                model.addRow(new Object[]{
                        it.getProductName(),
                        it.getProductPrice(),
                        it.getQuantity(),
                        subtotal
                });
            }
        }

        totalLabel.setText(String.valueOf(total));
    }

    private void confirmOrder() {

        if (customer.getCart() == null ||
            customer.getCart().getItems() == null ||
            customer.getCart().getItems().isEmpty()) {

            JOptionPane.showMessageDialog(gui, "Your cart is empty.");
            return;
        }

        try {
            customer.getCart().setCustomer(customer);

            Payment paymentGui = new Payment();
            new PaymentController(paymentGui, registry, customer, userService);

            paymentGui.setLocationRelativeTo(null);
            paymentGui.setVisible(true);

            gui.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(gui, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
