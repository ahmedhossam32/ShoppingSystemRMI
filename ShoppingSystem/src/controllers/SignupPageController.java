package controllers;

import gui.SignupPage;
import gui.LoginPage;
import gui.CustomerPage;
import gui.DeliveryPersonPage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Mainclasses.User;
import Mainclasses.Customer;
import Mainclasses.DeliveryStaff;
import rmi.UserInterface;

public class SignupPageController {

    private static final String HOST = "localhost";
    private static final int PORT = 3000;

    private final SignupPage gui;
    private final UserInterface userService;

    private JTextField nametextfield;
    private JTextField emailtextfield1;
    private JPasswordField password;
    private JPasswordField password2;

    private JButton signupBtn;
    private JButton loginBtn;

    private JRadioButton customer;
    private JRadioButton delivery;

    public SignupPageController(SignupPage gui, UserInterface userService) {
        this.gui = gui;
        this.userService = userService;

        wireComponentsByReflection();

        signupBtn.addActionListener(new SignUpAction());
        loginBtn.addActionListener(new GoToLoginAction());
    }

    private void wireComponentsByReflection() {
        try {
            nametextfield   = (JTextField) getPrivateField(gui, "nametextfield");
            emailtextfield1 = (JTextField) getPrivateField(gui, "emailtextfield1");
            password        = (JPasswordField) getPrivateField(gui, "password");
            password2       = (JPasswordField) getPrivateField(gui, "password2");

            signupBtn       = (JButton) getPrivateField(gui, "jButton1");
            loginBtn        = (JButton) getPrivateField(gui, "login");

            customer        = (JRadioButton) getPrivateField(gui, "customer");
            delivery        = (JRadioButton) getPrivateField(gui, "delivery");

        } catch (Exception e) {
            throw new RuntimeException("Failed to wire SignupPage components.", e);
        }
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    // =================== ACTIONS ===================

    private class SignUpAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String name  = nametextfield.getText().trim();
            String email = emailtextfield1.getText().trim();
            String pass1 = new String(password.getPassword()).trim();
            String pass2 = new String(password2.getPassword()).trim();

            if (name.isEmpty() || email.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                JOptionPane.showMessageDialog(gui, "Please fill all fields.");
                return;
            }

            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(gui, "Passwords do not match.");
                return;
            }

            if (!customer.isSelected() && !delivery.isSelected()) {
                JOptionPane.showMessageDialog(gui, "Please choose account type.");
                return;
            }

            // ✅ Enforce email convention:
            // Delivery accounts must contain "delivery" in email (your login relies on this)
            String lowerEmail = email.toLowerCase();
            if (delivery.isSelected() && !lowerEmail.contains("delivery")) {
                JOptionPane.showMessageDialog(gui,
                        "For Delivery accounts, email must contain the word 'delivery'.\n" +
                        "Example: delivery_john@mail.com",
                        "Invalid Email",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // (optional) you could also block customer from using delivery/admin words
            if (customer.isSelected() && (lowerEmail.contains("admin") || lowerEmail.contains("delivery"))) {
                JOptionPane.showMessageDialog(gui,
                        "Customer email cannot contain 'admin' or 'delivery'.",
                        "Invalid Email",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean gender = true;
            int phoneNumber = 0;
            String address = "Not set";

            try {
                // ✅ ONE register method inserts into USER collection only
                User u = userService.register(name, email, pass1, gender, phoneNumber, address);

                if (u == null) {
                    JOptionPane.showMessageDialog(gui, "Registration failed.");
                    return;
                }

                JOptionPane.showMessageDialog(gui, "Registered successfully!");

                // ✅ Navigation:
                // If server returns only User, we rely on email to decide which GUI to open.
                if (lowerEmail.contains("delivery")) {
                    DeliveryPersonPage page = new DeliveryPersonPage();

                    // if your Delivery page controller requires DeliveryStaff object,
                    // then you must either:
                    // 1) change controller to accept User (recommended), OR
                    // 2) create DeliveryStaff from User manually (quick workaround)
                    if (u instanceof DeliveryStaff) {
                        new DeliveryPersonPageController(page, (DeliveryStaff) u);
                    } else {
                        // quick workaround: block, or build DeliveryStaff from User
                        JOptionPane.showMessageDialog(gui,
                                "Registered as User. Delivery GUI needs DeliveryStaff object.\n" +
                                "Either change Delivery controller to accept User, or return DeliveryStaff from server.",
                                "Type Mismatch",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    page.setVisible(true);
                    gui.dispose();
                    return;
                }

                // default customer
                if (u instanceof Customer) {
                    try {
                        Registry registry = LocateRegistry.getRegistry(HOST, PORT);
                        CustomerPage page = new CustomerPage();
                        new CustomerPageController(page, (Customer) u, userService, registry);
                        page.setVisible(true);
                        gui.dispose();
                        return;
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(gui,
                                "Failed to connect to server: " + ex.getMessage(),
                                "RMI Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                        return;
                    }
                }

                // If your server returns plain User (not Customer), then you must also
                // change CustomerPageController to accept User OR return Customer from server.
                JOptionPane.showMessageDialog(gui,
                        "Registered as User but customer GUI expects Customer.\n" +
                        "Either return Customer from server, or update Customer controller to accept User.",
                        "Type Mismatch",
                        JOptionPane.ERROR_MESSAGE);

            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(gui, "Server error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class GoToLoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LoginPage loginPage = new LoginPage();
            new LoginPageController(loginPage, userService);
            loginPage.setVisible(true);
            gui.dispose();
        }
    }
}
