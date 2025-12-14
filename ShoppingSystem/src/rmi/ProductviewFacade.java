package rmi;


import Mainclasses.Product;
import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author amr21
 */
public interface ProductviewFacade extends Remote {
    String getProductDetails(String productId) throws RemoteException;
}
