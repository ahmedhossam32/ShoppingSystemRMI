package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import Mainclasses.*;   

public interface UserInterface extends Remote {

    User register(String name,String email,String password,boolean gender,int phoneNumber,String address) throws RemoteException;

    User login(String email, String password) throws RemoteException;

    User updateProfile(String userId, String newName,String newEmail,String newPassword,int newPhoneNumber) throws RemoteException;

    User getUserById(String userId) throws RemoteException;
    
    void addAdmin(Admin admin) throws RemoteException;

        
    List<User> getAllUsers() throws RemoteException;
}
