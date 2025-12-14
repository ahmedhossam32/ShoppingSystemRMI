package rmi_implementations; 

import Database.DB;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;
import Mainclasses.User;
import Mainclasses.*;
import rmi.UserInterface;

public class UserService extends UnicastRemoteObject implements UserInterface {

    private DB db;

    public UserService() throws RemoteException {
        db = new DB();
    }


@Override
public User register(String name, String email, String password,
                     boolean gender, int phoneNumber, String address) throws RemoteException {

    User existing = db.findUserByEmail(email);
    if (existing != null) {
        throw new RemoteException("Email already in use");
    }

    String id = UUID.randomUUID().toString();
    String lowerEmail = email.toLowerCase();

    User user;

    if (lowerEmail.contains("delivery")) {
        user = new DeliveryStaff(id, name, email, password, gender, phoneNumber);

        db.insertDeliveryStaff((DeliveryStaff) user);

        db.insertUser(user);

    } else if (lowerEmail.contains("admin")) {
        user = new Admin(id, name, email, password, gender, phoneNumber, address);
        db.insertUser(user);

    } else {
        user = new Customer(id, name, email, password, gender, phoneNumber, address);
        db.insertUser(user);
    }

    return user;
}


    @Override
    public User login(String email, String password) throws RemoteException {

        User user = db.findUserByEmail(email);
        if (user == null) {
            return null;
        }
        if (user.checkPassword(password)) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public void addAdmin(Admin admin) throws RemoteException {
    db.insertAdmin(admin);
}


    @Override
    public User updateProfile(String userId, String newName, String newEmail, String newPassword,int newPhoneNumber) throws RemoteException {
        User user = db.findUserById(userId);
        if (user == null) {
            return null;
        }
        user.updateProfile(newName, newEmail, newPassword, newPhoneNumber);
        db.updateUser(user);

        return user;
    }


    @Override
    public User getUserById(String userId) throws RemoteException {
        return db.findUserById(userId);
    }

    @Override
    public List<User> getAllUsers() throws RemoteException {
        return db.getAllUsers();
    }
}
