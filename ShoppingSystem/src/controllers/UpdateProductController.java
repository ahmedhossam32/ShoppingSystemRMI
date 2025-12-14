package controllers;

import gui.UpdateProductPage;
import gui.AdminPage;
import Mainclasses.Product;
import rmi.AdminProductInterface;
import rmi.*;
import rmi.ProductInterface;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

/**
 * Controller for UpdateProductPage
 * Handles product update logic with pre-validated product ID
 */
public class UpdateProductController {
    
    private UpdateProductPage updateProductPage;
    private AdminProductInterface adminProductInterface;
    private ProductInterface productInterface;
   private  AdminInterface adminInterface;
 
    // UI Components
    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JTextArea descriptionArea;
    private JTextField quantityField;
    private JButton updateButton;
    private JButton backButton;
    
    private String productIdToUpdate;
    
    /**
     * Constructor with pre-validated product ID
     * @param updateProductPage The update product page GUI
     * @param adminProductInterface RMI interface for product operations
     * @param productInterface RMI interface for product queries
     * @param productId Pre-validated product ID to update
     */
    public UpdateProductController(UpdateProductPage updateProductPage, 
                                   AdminProductInterface adminProductInterface,
                                   ProductInterface productInterface,
                                   String productId) {
        this.updateProductPage = updateProductPage;
        this.adminProductInterface = adminProductInterface;
        this.productInterface = productInterface;
        this.productIdToUpdate = productId;
        
        initializeComponents();
        setupListeners();
        loadProductData();
    }
    
    /**
     * Load existing product data into fields
     */
    private void loadProductData() {
        try {
            if (productInterface != null && productIdToUpdate != null) {
                String id = productInterface.getId(productIdToUpdate);
                String name = productInterface.getName(productIdToUpdate);
                String description = productInterface.getDescription(productIdToUpdate);
                double price = productInterface.getPrice(productIdToUpdate);
                int quantity = productInterface.getQuantity(productIdToUpdate);
                
                // Populate fields
                idField.setText(id);
                idField.setEditable(false); // ID should not be editable
                nameField.setText(name);
                priceField.setText(String.valueOf(price));
                descriptionArea.setText(description);
                quantityField.setText(String.valueOf(quantity));
                
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Error loading product data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize UI components using reflection
     */
    private void initializeComponents() {
        try {
            idField = getPrivateField("jTextField1");
            nameField = getPrivateField("jTextField2");
            priceField = getPrivateField("jTextField3");
            quantityField = getPrivateField("jTextField5");
            descriptionArea = getPrivateTextArea("jTextArea1");
            updateButton = getPrivateButton("jButton1");
            backButton = getPrivateButton("jButton2");
            
            // Change button text from "Add" to "Update"
            if (updateButton != null) {
                updateButton.setText("Update");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Error initializing components: " + e.getMessage(), 
                "Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Setup event listeners for buttons
     */
    private void setupListeners() {
        if (updateButton != null) {
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleUpdateProduct();
                }
            });
        }
        
        if (backButton != null) {
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleBack();
                }
            });
        }
    }
    
    /**
     * Handle Update Product action
     */
    private void handleUpdateProduct() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }
            
            // Get values from fields
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String description = descriptionArea.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            // Create Product object with updated values
            Product product = new Product(id, name, price, description, quantity);
            
            // Confirm update
            int confirm = JOptionPane.showConfirmDialog(updateProductPage, 
                "Are you sure you want to update this product?", 
                "Confirm Update", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Call RMI method to update product
                if (adminProductInterface != null) {
                    adminProductInterface.Update_Product(product);
                    
                    JOptionPane.showMessageDialog(updateProductPage, 
                        "Product updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Go back to admin page after successful update
                    handleBack();
                } else {
                    JOptionPane.showMessageDialog(updateProductPage, 
                        "Server connection not available", 
                        "Connection Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Please enter valid numeric values for Price and Quantity", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Error updating product: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Validate input fields
     */
    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Product Name is required", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Price is required", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (quantityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Quantity is required", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                JOptionPane.showMessageDialog(updateProductPage, 
                    "Price must be a positive number", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Price must be a valid number", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(updateProductPage, 
                    "Quantity must be a positive number", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(updateProductPage, 
                "Quantity must be a valid integer", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Handle Back button action
     */
    public void handleBack() {
        AdminPage adminPage = new AdminPage();
        AdminPageController controller = new AdminPageController(adminPage,adminInterface,adminProductInterface);
        adminPage.setVisible(true);
        updateProductPage.dispose();
    }
    
    /**
     * Helper method to get private JTextField using reflection
     */
    private JTextField getPrivateField(String fieldName) throws Exception {
        Field field = updateProductPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JTextField) field.get(updateProductPage);
    }
    
    /**
     * Helper method to get private JTextArea using reflection
     */
    private JTextArea getPrivateTextArea(String fieldName) throws Exception {
        Field field = updateProductPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JTextArea) field.get(updateProductPage);
    }
    
    /**
     * Helper method to get private JButton using reflection
     */
    private JButton getPrivateButton(String fieldName) throws Exception {
        Field field = updateProductPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JButton) field.get(updateProductPage);
    }
}