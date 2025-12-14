/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmi_implementations;

import Database.DB;
import Mainclasses.Product;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import rmi.ProductInterface;


public class ProductService extends UnicastRemoteObject implements ProductInterface {

    private DB db;

    public ProductService() throws RemoteException {
        super();
        db = new DB();
    }


    @Override
    public String getId(String productId) throws RemoteException {
        Product p = db.getProduct(productId);
        return (p != null) ? p.getProductId() : null;
    }

    @Override
    public String getName(String productId) throws RemoteException {
        Product p = db.getProduct(productId);
        return (p != null) ? p.getName() : null;
    }

    @Override
    public String getDescription(String productId) throws RemoteException {
        Product p = db.getProduct(productId);
        return (p != null) ? p.getDescription() : null;
    }

    @Override
    public int getQuantity(String productId) throws RemoteException {
        Product p = db.getProduct(productId);
        return (p != null) ? p.getQuantity() : -1;
    }

    @Override
    public double getPrice(String productId) throws RemoteException {
        Product p = db.getProduct(productId);
        return (p != null) ? p.getPrice() : -1;
    }

    @Override
    public ArrayList<Product> getAllProducts() throws RemoteException {
        return db.getAllProducts();
    }

   
}
