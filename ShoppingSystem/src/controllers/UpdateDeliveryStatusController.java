package controllers;

import gui.OrderPage;
import gui.DeliveryPersonPage;

import Mainclasses.Order;
import Mainclasses.DeliveryStaff;

import rmi.DeliverystaffInterface;
import rmi.OrderInterface;

import DesignPatterns.*;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.ButtonGroup;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Controller for OrderPage (used for updating delivery status)
 * Handles order status updates using State pattern
 * Follows the same pattern as AdminPageController
 */
public class UpdateDeliveryStatusController {
    
    private static final String HOST = "localhost";
    private static final int PORT = 3000;
    
    private final OrderPage orderPage;
    private final DeliverystaffInterface deliveryStaffInterface;
    private final DeliveryStaff currentDeliveryStaff;
    
    private OrderInterface orderInterface;
    
    // UI Components
    private JTextField orderIdField;
    private JRadioButton pendingRadio;
    private JRadioButton confirmedRadio;
    private JRadioButton shippedRadio;
    private JRadioButton deliveredRadio;
    private JButton confirmButton;
    private JButton backButton;
    private ButtonGroup statusGroup;
    
    private Order currentOrder;
    
    public UpdateDeliveryStatusController(OrderPage orderPage,
                                         DeliverystaffInterface deliveryStaffInterface,
                                         DeliveryStaff deliveryStaff) {
        this.orderPage = orderPage;
        this.deliveryStaffInterface = deliveryStaffInterface;
        this.currentDeliveryStaff = deliveryStaff;
        
        initOrderService();
        initializeComponents();
        setupListeners();
        clearSelection();
    }
    
    /**
     * Initialize RMI connection to OrderService
     */
    private void initOrderService() {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            
            // ✅ server binds: "OrderService"
            orderInterface = (OrderInterface) registry.lookup("OrderService");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(orderPage,
                    "Error connecting to OrderService: " + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize UI components using reflection
     */
    private void initializeComponents() {
        try {
            orderIdField = getPrivateTextField("jTextField1");
            pendingRadio = getPrivateRadioButton("jRadioButton1");
            confirmedRadio = getPrivateRadioButton("jRadioButton3");
            shippedRadio = getPrivateRadioButton("jRadioButton2");
            deliveredRadio = getPrivateRadioButton("jRadioButton4");
            confirmButton = getPrivateButton("jButton1");
            backButton = getPrivateButton("jButton2");
            
            // Clear the default text
            if (orderIdField != null) {
                orderIdField.setText("");
            }
            
            // Create button group for radio buttons
            statusGroup = new ButtonGroup();
            statusGroup.add(pendingRadio);
            statusGroup.add(confirmedRadio);
            statusGroup.add(shippedRadio);
            statusGroup.add(deliveredRadio);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(orderPage, 
                "Error initializing components: " + e.getMessage(), 
                "Initialization Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Setup event listeners
     */
    private void setupListeners() {
        if (confirmButton != null) {
            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleUpdateStatus();
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
        
        // Add listener to load order when ID is entered
        if (orderIdField != null) {
            orderIdField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadOrderById();
                }
            });
        }
    }
    
    /**
     * Load order by ID when user enters it
     */
    private void loadOrderById() {
        String orderId = orderIdField.getText().trim();
        
        if (orderId.isEmpty()) {
            JOptionPane.showMessageDialog(orderPage,
                    "Please enter an Order ID",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (orderInterface == null) {
                JOptionPane.showMessageDialog(orderPage,
                        "Order service not connected.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            currentOrder = orderInterface.getOrderById(orderId);
            
            if (currentOrder == null) {
                JOptionPane.showMessageDialog(orderPage,
                        "Order with ID '" + orderId + "' not found.",
                        "Order Not Found",
                        JOptionPane.ERROR_MESSAGE);
                currentOrder = null;
                clearSelection();
                return;
            }
            
            // Display current status
            displayCurrentStatus();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(orderPage,
                    "Error loading order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Display current order status
     */
    private void displayCurrentStatus() {
        if (currentOrder != null && currentOrder.getCurrentStatus() != null) {
            String statusName = currentOrder.getCurrentStatus().getClass().getSimpleName();
            JOptionPane.showMessageDialog(orderPage, 
                "Current Order Status: " + statusName.replace("Status", ""), 
                "Order Status", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Handle status update using State pattern
     */
    private void handleUpdateStatus() {
        try {
            // Validate order is loaded
            if (currentOrder == null) {
                JOptionPane.showMessageDialog(orderPage,
                        "Please load an order first by entering Order ID and pressing Enter.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Determine which status is selected
            OrderStatus newStatus = getSelectedStatus();
            
            if (newStatus == null) {
                JOptionPane.showMessageDialog(orderPage,
                        "Please select a status.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate service connection
            if (deliveryStaffInterface == null) {
                JOptionPane.showMessageDialog(orderPage,
                        "Delivery service not connected.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (currentDeliveryStaff == null || currentDeliveryStaff.getId() == null) {
                JOptionPane.showMessageDialog(orderPage,
                        "Delivery staff information not available.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Confirm update
            int confirm = JOptionPane.showConfirmDialog(orderPage,
                    "Are you sure you want to update the order status?",
                    "Confirm Update",
                    JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Apply the state transition using State pattern
            applyStatusTransition(newStatus);
            
            // Update via RMI
            String staffId = currentDeliveryStaff.getId();
            deliveryStaffInterface.updateDeliveryStatus(staffId, currentOrder, newStatus);
            
            JOptionPane.showMessageDialog(orderPage,
                    "Order status updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Clear and reset
            clearSelection();
            orderIdField.setText("");
            currentOrder = null;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(orderPage,
                    "Error updating status: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Get the selected status from radio buttons
     */
    private OrderStatus getSelectedStatus() {
        if (pendingRadio.isSelected()) {
            return new PendingStatus();
        } else if (confirmedRadio.isSelected()) {
            return new ConfirmedStatus();
        } else if (shippedRadio.isSelected()) {
            return new ShippedStatus();
        } else if (deliveredRadio.isSelected()) {
            return new DeliveredStatus();
        }
        return null;
    }
    
    /**
     * Apply status transition using State pattern
     * This respects the state machine transitions
     */
    private void applyStatusTransition(OrderStatus targetStatus) {
        if (currentOrder == null || currentOrder.getCurrentStatus() == null) {
            return;
        }
        
        OrderStatus currentStatus = currentOrder.getCurrentStatus();
        
        // Apply appropriate transition based on target status
        if (targetStatus instanceof PendingStatus) {
            currentStatus.pending(currentOrder);
        } else if (targetStatus instanceof ConfirmedStatus) {
            currentStatus.confirm(currentOrder);
        } else if (targetStatus instanceof ShippedStatus) {
            currentStatus.ship(currentOrder);
        } else if (targetStatus instanceof DeliveredStatus) {
            currentStatus.deliver(currentOrder);
        }
    }
    
    /**
     * Clear radio button selection
     */
    private void clearSelection() {
        if (statusGroup != null) {
            statusGroup.clearSelection();
        }
    }
    
    /**
     * Handle Back button
     */
    private void handleBack() {
        DeliveryPersonPage deliveryPersonPage = new DeliveryPersonPage();
        DeliveryPersonPageController controller = new DeliveryPersonPageController(
            deliveryPersonPage, 
            currentDeliveryStaff
        );
        deliveryPersonPage.setVisible(true);
        orderPage.dispose();
    }
    
    /**
     * Helper method to get private JTextField using reflection
     */
    private JTextField getPrivateTextField(String fieldName) throws Exception {
        Field field = orderPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JTextField) field.get(orderPage);
    }
    
    /**
     * Helper method to get private JRadioButton using reflection
     */
    private JRadioButton getPrivateRadioButton(String fieldName) throws Exception {
        Field field = orderPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JRadioButton) field.get(orderPage);
    }
    
    /**
     * Helper method to get private JButton using reflection
     */
    private JButton getPrivateButton(String fieldName) throws Exception {
        Field field = orderPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JButton) field.get(orderPage);
    }
}