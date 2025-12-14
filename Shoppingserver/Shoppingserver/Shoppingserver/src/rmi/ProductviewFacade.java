package rmi;


import Mainclasses.Product;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ProductviewFacade extends Remote {
    String getProductDetails(String productId) throws RemoteException;
}
