package controllers;

import gui.LoginPage;
import gui.SignupPage;
import gui.CustomerPage;
import gui.AdminPage;
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
import javax.swing.JTextField;

import rmi.UserInterface;
import rmi.AdminInterface;
import rmi.AdminProductInterface;

import Mainclasses.User;
import Mainclasses.Customer;
import Mainclasses.DeliveryStaff;

public class LoginPageController {

    private static final String HOST = "localhost";
    private static final int PORT = 3000;

    private final LoginPage gui;
    private final UserInterface userService;

    private JTextField emailtextfield;
    private JPasswordField passwordfield;
    private JButton loginbutton;
    private JButton signup;

    public LoginPageController(LoginPage gui, UserInterface userService) {
        this.gui = gui;
        this.userService = userService;

        wireComponentsByReflection();

        loginbutton.addActionListener(new LoginAction());
        signup.addActionListener(new SignUpAction());
    }

    private void wireComponentsByReflection() {
        try {
            emailtextfield = (JTextField) getPrivateField(gui, "emailtextfield");
            passwordfield  = (JPasswordField) getPrivateField(gui, "passwordfield");
            loginbutton    = (JButton) getPrivateField(gui, "loginbutton");
            signup         = (JButton) getPrivateField(gui, "signup");
        } catch (Exception e) {
            throw new RuntimeException("Failed to wire LoginPage components.", e);
        }
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    // ================= Actions =================

    private class LoginAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String email = emailtextfield.getText().trim();
            String password = new String(passwordfield.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(gui, "Please enter email and password.");
                return;
            }

            try {
                User u = userService.login(email, password);

                if (u == null) {
                    JOptionPane.showMessageDialog(gui, "Invalid email or password.");
                    return;
                }

                JOptionPane.showMessageDialog(gui, "Login successful!");

                String lowerEmail = (u.getEmail() == null) ? "" : u.getEmail().toLowerCase();

                // ================= ADMIN =================
                if (lowerEmail.contains("admin")) {

                    try {
                        Registry registry = LocateRegistry.getRegistry(HOST, PORT);

                        // ✅ server binds only: "AdminService"
                        Object adminObj = registry.lookup("AdminService");
                        AdminInterface adminInterface = (AdminInterface) adminObj;
                        AdminProductInterface adminProductInterface = (AdminProductInterface) adminObj;

                        AdminPage page = new AdminPage();
                        new AdminPageController(page, adminInterface, adminProductInterface);
                        page.setVisible(true);
                        gui.dispose();
                        return;

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(gui,
                                "Admin services not connected. Check server binding name 'AdminService' and port 3000.\n"
                                        + ex.getMessage(),
                                "RMI Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                        return;
                    }
                }

                // ================= DELIVERY =================
                if (lowerEmail.contains("delivery")) {

                    if (!(u instanceof DeliveryStaff)) {
                        JOptionPane.showMessageDialog(gui,
                                "Email says delivery, but returned user is not DeliveryStaff.",
                                "Type Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    DeliveryPersonPage page = new DeliveryPersonPage();
                    new DeliveryPersonPageController(page, (DeliveryStaff) u);
                    page.setVisible(true);
                    gui.dispose();
                    return;
                }

                // ================= DEFAULT CUSTOMER =================
                if (!(u instanceof Customer)) {
                    JOptionPane.showMessageDialog(gui,
                            "Default is customer, but returned user is not Customer.",
                            "Type Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    Registry registry = LocateRegistry.getRegistry(HOST, PORT);
                    CustomerPage page = new CustomerPage();
                    new CustomerPageController(page, (Customer) u, userService, registry);
                    page.setVisible(true);
                    gui.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(gui,
                            "Failed to connect to server: " + ex.getMessage(),
                            "RMI Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }

            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(gui, "Server error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class SignUpAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            SignupPage s = new SignupPage();
            new SignupPageController(s, userService);
            s.setVisible(true);
            gui.dispose();
        }
    }
}
