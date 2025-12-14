package DesignPatterns;

public class CashOnDeliveryPayment implements Payment {

    @Override
    public boolean processPayment(double amount) {
        System.out.println("Cash on Delivery selected. Amount: " + amount);
        return true;
    }
}
