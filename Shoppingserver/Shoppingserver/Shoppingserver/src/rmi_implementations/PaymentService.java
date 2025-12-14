package rmi_implementations;

import DesignPatterns.PaymentContext;
import DesignPatterns.CreditCardPayment;
import DesignPatterns.InstapayPayment;
import DesignPatterns.CashOnDeliveryPayment;
import rmi.PaymentInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PaymentService extends UnicastRemoteObject implements PaymentInterface {

    private final PaymentContext context;
    private String CardNum, Expiry, Cvv;
    private String InstapayId;

    public PaymentService() throws RemoteException {
        super();
        context = new PaymentContext();
    }

    @Override
    public void setStrategy(String type) throws RemoteException {
        if (type == null) return;

        switch (type.toLowerCase().trim()) {
            case "creditcard":
                context.setPaymentStrategy(
                    new CreditCardPayment(CardNum, Expiry, Cvv)
                );
                break;

            case "instapay":
                context.setPaymentStrategy(
                    new InstapayPayment(InstapayId)
                );
                break;

            case "cod":
                context.setPaymentStrategy(
                    new CashOnDeliveryPayment()
                );
                break;

            default:
                System.out.println("Unknown payment type");
                context.setPaymentStrategy(null);
        }
    }

    @Override
    public void setCreditCardDetails(String cardNum, String expiryDate, String cvv) throws RemoteException {
        this.CardNum = cardNum;
        this.Expiry = expiryDate;
        this.Cvv = cvv;
    }

    @Override
    public void setInstapayDetails(String instapayId) throws RemoteException {
        this.InstapayId = instapayId;
    }

    @Override
    public boolean processPayment(double amount) throws RemoteException {
        return context.executePayment(amount);
    }
}