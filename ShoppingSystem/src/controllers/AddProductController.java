package controllers;

import gui.AddProductPage;
import gui.AdminPage;

import Mainclasses.Product;

import rmi.AdminInterface;
import rmi.AdminProductInterface;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

public class AddProductController {

    private final AddProductPage addProductPage;
    private final AdminInterface adminInterface;
    private final AdminProductInterface adminProductInterface;

    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JTextArea descriptionArea;
    private JTextField quantityField;
    private JButton addButton;
    private JButton backButton;

    public AddProductController(AddProductPage addProductPage,
                                AdminInterface adminInterface,
                                AdminProductInterface adminProductInterface) {

        this.addProductPage = addProductPage;
        this.adminInterface = adminInterface;
        this.adminProductInterface = adminProductInterface;

        initializeComponents();
        setupListeners();
        clearFields();
    }

    private void initializeComponents() {
        try {
            idField = getPrivateField("jTextField1");
            nameField = getPrivateField("jTextField2");
            priceField = getPrivateField("jTextField3");
            quantityField = getPrivateField("jTextField5");
            descriptionArea = getPrivateTextArea("jTextArea1");
            addButton = getPrivateButton("jButton1");
            backButton = getPrivateButton("jButton2");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(addProductPage,
                    "Error initializing components: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        if (addButton != null) {
            addButton.addActionListener(e -> handleAddProduct());
        }

        if (backButton != null) {
            backButton.addActionListener(e -> handleBack());
        }
    }

    private void handleAddProduct() {
        try {
            if (!validateInputs()) return;

            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String description = descriptionArea.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());

            Product product = new Product(id, name, price, description, quantity);

            if (adminProductInterface != null) {
                adminProductInterface.Add_Product(product);

                JOptionPane.showMessageDialog(addProductPage,
                        "Product added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                clearFields();
            } else {
                JOptionPane.showMessageDialog(addProductPage,
                        "AdminProductInterface not available",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(addProductPage,
                    "Please enter valid numeric values for Price and Quantity",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(addProductPage,
                    "Error adding product: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(addProductPage, "Product ID is required");
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(addProductPage, "Product Name is required");
            return false;
        }
        if (priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(addProductPage, "Price is required");
            return false;
        }
        if (quantityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(addProductPage, "Quantity is required");
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                JOptionPane.showMessageDialog(addProductPage, "Price must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(addProductPage, "Price must be a valid number");
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(addProductPage, "Quantity must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(addProductPage, "Quantity must be a valid integer");
            return false;
        }

        return true;
    }

    private void clearFields() {
        if (idField != null) idField.setText("");
        if (nameField != null) nameField.setText("");
        if (priceField != null) priceField.setText("");
        if (quantityField != null) quantityField.setText("");
        if (descriptionArea != null) descriptionArea.setText("");
    }

    private void handleBack() {
        AdminPage adminPage = new AdminPage();

        // ✅ pass SAME stubs back
        new AdminPageController(adminPage, adminInterface, adminProductInterface);

        adminPage.setVisible(true);
        addProductPage.dispose();
    }

    private JTextField getPrivateField(String fieldName) throws Exception {
        Field field = addProductPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JTextField) field.get(addProductPage);
    }

    private JTextArea getPrivateTextArea(String fieldName) throws Exception {
        Field field = addProductPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JTextArea) field.get(addProductPage);
    }

    private JButton getPrivateButton(String fieldName) throws Exception {
        Field field = addProductPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JButton) field.get(addProductPage);
    }
}
