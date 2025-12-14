/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import Mainclasses.Product;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminProductInterface extends Remote {
    void Add_Product(Product product) throws RemoteException;
    void Update_Product(Product product) throws RemoteException;
    void delete_Product(Product product) throws RemoteException;
    void Apply_Discount(Product product, double discount) throws RemoteException;
}
