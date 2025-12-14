/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rmi;

import Mainclasses.Product;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface ProductInterface extends Remote{
    String getId(String productId) throws RemoteException;
    String getName(String productId) throws RemoteException;
    String getDescription(String productId) throws RemoteException;
    int getQuantity(String productId) throws RemoteException;
    double getPrice(String productId) throws RemoteException;
    ArrayList<Product> getAllProducts() throws RemoteException;
}
