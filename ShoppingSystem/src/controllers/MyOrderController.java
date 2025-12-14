package controllers;

import gui.MyOrder;
import gui.CustomerPage;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Mainclasses.Customer;
import Mainclasses.Order;
import Mainclasses.CartItem;
import rmi.OrderInterface;
import rmi.UserInterface;

import java.rmi.registry.Registry;

public class MyOrderController {

    private final MyOrder gui;
    private final Customer customer;
    private final OrderInterface orderService;
    private final UserInterface userService;
    private final Registry registry;

    private JTable ordersTable;
    private DefaultTableModel model;

    private JButton backBtn; 

    public MyOrderController(MyOrder gui, Customer customer,
                             OrderInterface orderService, UserInterface userService,
                             Registry registry) {
        this.gui = gui;
        this.customer = customer;
        this.orderService = orderService;
        this.userService = userService;
        this.registry = registry;

        wireByReflection();
        setupModel();

        if (backBtn != null) {
            backBtn.addActionListener(e -> {
                CustomerPage page = new CustomerPage();
                new CustomerPageController(page, customer, userService, registry);
                page.setVisible(true);
                gui.dispose();
            });
        }

        SwingUtilities.invokeLater(() -> {
            loadOrders();
            showNotifications();
        });
    }

    private void wireByReflection() {
        try {
            ordersTable = (JTable) getPrivateField(gui, "jTable1");
            backBtn     = (JButton) getPrivateField(gui, "jButton1");
        } catch (Exception e) {
            throw new RuntimeException("MyOrderController wiring failed. Check variable names in MyOrder.", e);
        }
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    private void setupModel() {
        model = new DefaultTableModel(new Object[]{"OrderID", "Status", "ItemsCount", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 2) return Integer.class;
                if (col == 3) return Double.class;
                return String.class;
            }
        };
        ordersTable.setModel(model);
    }

    private void loadOrders() {
        if (customer == null || customer.getId() == null) {
            JOptionPane.showMessageDialog(gui, "Customer not found.");
            return;
        }

        try {
            List<Order> orders = orderService.getOrdersForCustomer(customer.getId());
            model.setRowCount(0);

            if (orders == null || orders.isEmpty()) return;

            for (Order o : orders) {
                if (o == null) continue;

                String orderId = safeStr(o.getId());

                String status = "Unknown";
                if (o.getStatusName() != null && !o.getStatusName().trim().isEmpty()) {
                    status = o.getStatusName();
                } else if (o.getCurrentStatus() != null) {
                    status = o.getCurrentStatus().toString();
                }

                int itemsCount = 0;
                double total = 0.0;

                if (o.getItems() != null) {
                    itemsCount = o.getItems().size();
                    for (CartItem it : o.getItems()) {
                        if (it == null) continue;
                        total += it.getProductPrice() * it.getQuantity();
                    }
                }

                model.addRow(new Object[]{orderId, status, itemsCount, total});
            }

        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(gui, "Server error loading orders: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showNotifications() {
        if (customer == null || customer.getId() == null) return;

        try {
            List<String> notes = orderService.getNotificationsForCustomer(customer.getId());
            if (notes == null || notes.isEmpty()) return;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < notes.size(); i++) {
                sb.append(notes.get(i));
                if (i != notes.size() - 1) sb.append("\n");
            }

            JOptionPane.showMessageDialog(gui, sb.toString(), "Order Updates", JOptionPane.INFORMATION_MESSAGE);

        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(gui, "Server error getting notifications: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String safeStr(String s) {
        return (s == null) ? "" : s;
    }
}
