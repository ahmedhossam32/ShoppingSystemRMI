package controllers;

import gui.ViewProducts;
import gui.ViewCart;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import Mainclasses.Customer;
import Mainclasses.Product;
import rmi.ProductInterface;
import rmi.OrderInterface;
import rmi.UserInterface;

import java.rmi.registry.Registry;

public class ViewProductsController {

    private final ViewProducts gui;
    private final Customer customer;
    private final ProductInterface productService;
    private final OrderInterface orderService;
    private final UserInterface userService;
    private final Registry registry;

    private JTable table;
    private JLabel nameLabel;

    private DefaultTableModel model;
    private List<Product> products = new ArrayList<>();

    public ViewProductsController(ViewProducts gui, Customer customer,
                                  ProductInterface productService,
                                  OrderInterface orderService,
                                  UserInterface userService,
                                  Registry registry) {
        this.gui = gui;
        this.customer = customer;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.registry = registry;

        wireByReflection();
        setupTable();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadProducts();
            }
        });
    }

    private void wireByReflection() {
        try {
            table = (JTable) getPrivateField(gui, "proudcttable");
            nameLabel = (JLabel) getPrivateField(gui, "nametextfield");
        } catch (Exception e) {
            throw new RuntimeException("ViewProductsController wiring failed. Check variable names.", e);
        }

        if (customer != null && customer.getName() != null) {
            nameLabel.setText(customer.getName());
        }

        if (customer != null && customer.getCart() == null) {
            customer.setCart(new Mainclasses.Cart());
        }
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    private void setupTable() {
        model = new DefaultTableModel(new Object[]{"Name", "Price", "Stock", "+", "-"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 4;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 1) return Double.class;
                if (col == 2) return Integer.class;
                return String.class;
            }
        };

        table.setModel(model);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer("+"));
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor("+", this, true));

        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("-"));
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor("-", this, false));
    }

    private void loadProducts() {
        try {
            products = productService.getAllProducts();
            if (products == null) products = new ArrayList<>();

            model.setRowCount(0);
            for (Product p : products) {
                model.addRow(new Object[]{p.getName(), p.getPrice(), p.getQuantity(), "+", "-"});
            }

        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(gui, "Error loading products: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void handlePlus(int row) { addToCart(row); }
    void handleMinus(int row) { removeFromCart(row); }

    private void addToCart(int row) {
        if (!validRow(row)) return;

        Product p = products.get(row);
        if (p.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(gui, "Out of stock!");
            return;
        }

        customer.addToCart(p, 1);
        showChoiceDialog("Added successfully: " + p.getName());
    }

    private void removeFromCart(int row) {
        if (!validRow(row)) return;

        Product p = products.get(row);
        customer.removeFromCart(p);
        showChoiceDialog("Removed successfully: " + p.getName());
    }

    private boolean validRow(int row) {
        return row >= 0 && row < products.size();
    }

    private void showChoiceDialog(String msg) {
        Object[] options = {"Continue", "View Cart"};
        int choice = JOptionPane.showOptionDialog(
                gui, msg, "Cart",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
        );

        if (choice == 1) openCartPage();
    }

    private void openCartPage() {
        ViewCart page = new ViewCart();
        new ViewCartController(page, customer, orderService, userService, registry);
        page.setVisible(true);
        gui.dispose();
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int col) {
            setText(String.valueOf(value));
            return this;
        }
    }

    private static class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton button = new JButton();
        private final ViewProductsController controller;
        private final boolean isPlus;
        private int currentRow;

        ButtonEditor(String label, ViewProductsController controller, boolean isPlus) {
            this.controller = controller;
            this.isPlus = isPlus;

            button.setText(label);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    if (ButtonEditor.this.isPlus) controller.handlePlus(currentRow);
                    else controller.handleMinus(currentRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int col) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}
