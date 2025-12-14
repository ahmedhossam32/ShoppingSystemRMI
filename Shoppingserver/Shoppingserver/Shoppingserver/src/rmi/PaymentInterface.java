package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PaymentInterface extends Remote {
    void setStrategy(String type) throws RemoteException;
    boolean processPayment(double amount) throws RemoteException;
    public void setCreditCardDetails(String cardNum, String expiryDate, String cvv) throws RemoteException ;
      public void setInstapayDetails(String instapayId) throws RemoteException;
}

