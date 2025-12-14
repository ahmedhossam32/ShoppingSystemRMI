package controllers;

import gui.DeliveryPersonPage;
import gui.OrderPage;
import gui.LoginPage;

import Mainclasses.Order;
import Mainclasses.DeliveryStaff;

import rmi.DeliverystaffInterface;
import rmi.UserInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Controller for DeliveryPersonPage
 * Handles navigation and delivery person operations
 * Follows the same pattern as AdminPageController
 */
public class DeliveryPersonPageController {
    
    private static final String HOST = "localhost";
    private static final int PORT = 3000;
    
    private final DeliveryPersonPage deliveryPersonPage;
    private final DeliveryStaff currentDeliveryStaff;
    
    // ✅ received services
    private DeliverystaffInterface deliveryStaffInterface;
    
    // UI buttons
    private JButton viewAssignedDeliveriesButton;  // jButton2
    private JButton updateDeliveryStatusButton;    // jButton4
    private JButton logoutButton;                  // jButton1 (to be added)
    
    public DeliveryPersonPageController(DeliveryPersonPage deliveryPersonPage, DeliveryStaff deliveryStaff) {
        this.deliveryPersonPage = deliveryPersonPage;
        this.currentDeliveryStaff = deliveryStaff;
        
        initDeliveryService();
        initializeButtons();
        setupButtonListeners();
    }
    
    /**
     * Initialize RMI connection to DeliveryStaffService
     */
    private void initDeliveryService() {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            
            // ✅ server binds: "DeliveryStaffService"
            deliveryStaffInterface = (DeliverystaffInterface) registry.lookup("DeliveryStaffService");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(deliveryPersonPage,
                    "Error connecting to DeliveryStaffService: " + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    /**
     * Initialize button references using reflection
     */
    private void initializeButtons() {
        try {
            viewAssignedDeliveriesButton = getPrivateButton("jButton2");
            updateDeliveryStatusButton = getPrivateButton("jButton4");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(deliveryPersonPage,
                    "Error initializing buttons: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Setup event listeners for all buttons
     */
    private void setupButtonListeners() {
        
        if (viewAssignedDeliveriesButton != null) {
            viewAssignedDeliveriesButton.addActionListener(e -> viewAssignedDeliveries());
        }
        
        if (updateDeliveryStatusButton != null) {
            updateDeliveryStatusButton.addActionListener(e -> navigateToUpdateDeliveryStatus());
        }
        
        if (logoutButton != null) {
            logoutButton.addActionListener(e -> handleLogout());
        }
    }
    
    /**
     * View assigned deliveries for the current delivery staff
     */
    public void viewAssignedDeliveries() {
        try {
            if (deliveryStaffInterface == null) {
                JOptionPane.showMessageDialog(deliveryPersonPage,
                        "Delivery service not connected.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (currentDeliveryStaff == null || currentDeliveryStaff.getId() == null) {
                JOptionPane.showMessageDialog(deliveryPersonPage,
                        "Delivery staff information not available.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String staffId = currentDeliveryStaff.getId();
            List<Order> assignedOrders = deliveryStaffInterface.viewAssignedOrders(staffId);

            if (assignedOrders == null || assignedOrders.isEmpty()) {
                JOptionPane.showMessageDialog(deliveryPersonPage,
                        "No deliveries assigned to you at the moment.",
                        "No Deliveries",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create a table to display the orders
            displayOrdersTable(assignedOrders);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(deliveryPersonPage,
                    "Error retrieving assigned deliveries: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Display orders in a table format
     */
    private void displayOrdersTable(List<Order> orders) {
        // Create table model
        String[] columnNames = {"Order ID", "Customer", "Status", "Total Amount", "Items Count"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Populate table with order data
        for (Order order : orders) {
            Object[] rowData = {
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getName() : "N/A",
                getOrderStatusName(order),
                String.format("%.2f EGP", order.getTotalAmount()),
                order.getItems() != null ? order.getItems().size() : 0
            };
            tableModel.addRow(rowData);
        }
        
        // Create table
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        
        // Create frame to display the table
        JFrame tableFrame = new JFrame("Assigned Deliveries");
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tableFrame.add(new JScrollPane(table));
        tableFrame.setSize(700, 400);
        tableFrame.setLocationRelativeTo(deliveryPersonPage);
        tableFrame.setVisible(true);
    }
    
    /**
     * Get order status name from the order
     */
    private String getOrderStatusName(Order order) {
        if (order.getCurrentStatus() == null) {
            return "Unknown";
        }
        // Get the class name and extract status name
        String statusClassName = order.getCurrentStatus().getClass().getSimpleName();
        return statusClassName.replace("Status", "");
    }
    
    /**
     * Navigate to Update Delivery Status page
     */
    public void navigateToUpdateDeliveryStatus() {
        try {
            OrderPage orderPage = new OrderPage();
            UpdateDeliveryStatusController controller = new UpdateDeliveryStatusController(
                orderPage, 
                deliveryStaffInterface, 
                currentDeliveryStaff
            );
            orderPage.setVisible(true);
            deliveryPersonPage.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(deliveryPersonPage, 
                "Error opening Update Delivery Status page: " + e.getMessage(), 
                "Navigation Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Handle logout action
     */
    public void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(deliveryPersonPage,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Navigate back to login page
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
            deliveryPersonPage.dispose();
        }
    }
    
    /**
     * Helper method to get private JButton using reflection
     */
    private JButton getPrivateButton(String fieldName) throws Exception {
        Field field = deliveryPersonPage.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JButton) field.get(deliveryPersonPage);
    }
}