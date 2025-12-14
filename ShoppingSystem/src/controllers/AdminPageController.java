package controllers;

import gui.AdminPage;
import gui.AddProductPage;
import gui.UpdateProductPage;
import gui.LoginPage;

import Mainclasses.Product;

import rmi.AdminInterface;
import rmi.AdminProductInterface;
import rmi.ProductInterface;
import rmi.UserInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

public class AdminPageController {

    private static final String HOST = "localhost";
    private static final int PORT = 3000;

    private final AdminPage adminPage;

    // ✅ received from Login and used directly
    private final AdminInterface adminInterface;
    private final AdminProductInterface adminProductInterface;

    // ✅ only this is looked up here
    private ProductInterface productInterface;

    // UI buttons
    private JButton addProductButton;      // jButton2
    private JButton updateProductButton;   // jButton5
    private JButton deleteProductButton;   // jButton4
    private JButton viewAnalyticsButton;   // jButton3
    private JButton logoutButton;          // jButton6

    public AdminPageController(AdminPage adminPage,
                               AdminInterface adminInterface,
                               AdminProductInterface adminProductInterface) {

        this.adminPage = adminPage;
        this.adminInterface = adminInterface;
        this.adminProductInterface = adminProductInterface;

        initProductService();      // ✅ only product lookup
        initializeButtons();
        setupButtonListeners();
    }

    private void initProductService() {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);

            // ✅ server binds: "ProductService"
            productInterface = (ProductInterface) registry.lookup("ProductService");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(adminPage,
                    "Error connecting to ProductService: " + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeButtons() {
        try {
            addProductButton = getPrivateButton("jButton2");
            updateProductButton = getPrivateButton("jButton5");
            deleteProductButton = getPrivateButton("jButton4");
            viewAnalyticsButton = getPrivateButton("jButton3");
            logoutButton = getPrivateButton("jButton6");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(adminPage,
                    "Error initializing buttons: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupButtonListeners() {

        if (addProductButton != null) {
            addProductButton.addActionListener(e -> navigateToAddProduct());
        }

        if (updateProductButton != null) {
            updateProductButton.addActionListener(e -> navigateToUpdateProduct());
        }

        if (deleteProductButton != null) {
            deleteProductButton.addActionListener(e -> handleDeleteProduct());
        }

        if (viewAnalyticsButton != null) {
            viewAnalyticsButton.addActionListener(e -> handleViewAnalytics());
        }

        if (logoutButton != null) {
            logoutButton.addActionListener(e -> handleLogout());
        }
    }

    public void navigateToAddProduct() {
        try {
            AddProductPage addProductPage = new AddProductPage();

            // ✅ pass the SAME adminProductInterface
            new AddProductController(addProductPage, adminInterface, adminProductInterface);

            addProductPage.setVisible(true);
            adminPage.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(adminPage,
                    "Error opening Add Product page: " + e.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void navigateToUpdateProduct() {
        try {
            String productId = JOptionPane.showInputDialog(adminPage,
                    "Enter Product ID to update:",
                    "Update Product",
                    JOptionPane.QUESTION_MESSAGE);

            if (productId == null || productId.trim().isEmpty()) return;
            productId = productId.trim();

            if (productInterface == null) {
                JOptionPane.showMessageDialog(adminPage, "Product service not connected.");
                return;
            }

            String id = productInterface.getId(productId);
            if (id == null || id.isEmpty()) {
                JOptionPane.showMessageDialog(adminPage,
                        "Product with ID '" + productId + "' not found",
                        "Product Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            UpdateProductPage updateProductPage = new UpdateProductPage();
            new UpdateProductController(updateProductPage, adminProductInterface, productInterface, productId);

            updateProductPage.setVisible(true);
            adminPage.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(adminPage,
                    "Error opening Update Product page: " + e.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void handleDeleteProduct() {
        try {
            String productId = JOptionPane.showInputDialog(adminPage,
                    "Enter Product ID to delete:",
                    "Delete Product",
                    JOptionPane.QUESTION_MESSAGE);

            if (productId == null || productId.trim().isEmpty()) return;
            productId = productId.trim();

            if (productInterface == null) {
                JOptionPane.showMessageDialog(adminPage, "Product service not connected.");
                return;
            }

            String id = productInterface.getId(productId);
            if (id == null || id.isEmpty()) {
                JOptionPane.showMessageDialog(adminPage,
                        "Product with ID '" + productId + "' not found",
                        "Product Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(adminPage,
                    "Are you sure you want to delete product with ID '" + productId + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) return;

            String name = productInterface.getName(productId);
            String description = productInterface.getDescription(productId);
            double price = productInterface.getPrice(productId);
            int quantity = productInterface.getQuantity(productId);

            Product product = new Product(id, name, price, description, quantity);

            adminProductInterface.delete_Product(product);

            JOptionPane.showMessageDialog(adminPage,
                    "Product deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(adminPage,
                    "Error deleting product: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void handleViewAnalytics() {
        try {
            String analyticsReport = adminInterface.View_analytics();

            JOptionPane.showMessageDialog(adminPage,
                    analyticsReport,
                    "Sales Analytics Report",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(adminPage,
                    "Error viewing analytics: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(adminPage,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // optional: go back to login page
            LoginPage login = new LoginPage();

            try {
                Registry registry = LocateRegistry.getRegistry(HOST, PORT);
                UserInterface userService = (UserInterface) registry.lookup("UserService");
                new LoginPageController(login, userService);
            } catch (Exception ex) {
                // If lookup fails, still show login page (but it won't work until server ok)
                ex.printStackTrace();
            }

            login.setVisible(true);
            adminPage.dispose();
        }
    }

    private JButton getPrivateButton(String fieldName) throws Exception {
        Field field = adminPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JButton) field.get(adminPage);
    }
}
