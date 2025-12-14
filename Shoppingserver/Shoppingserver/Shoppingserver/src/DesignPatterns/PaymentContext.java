package DesignPatterns;

public class PaymentContext {

    private Payment paymentStrategy;

    public void setPaymentStrategy(Payment paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public boolean executePayment(double amount) {
        if (paymentStrategy == null) {
            System.out.println("No payment strategy selected.");
            return false;
        }
        return paymentStrategy.processPayment(amount);
    }
}
