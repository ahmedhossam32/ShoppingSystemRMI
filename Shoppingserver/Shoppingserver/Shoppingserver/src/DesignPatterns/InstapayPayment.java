package DesignPatterns;

public class InstapayPayment implements Payment {

    private String instapayId;

    public InstapayPayment(String instapayId) {
        this.instapayId = instapayId;
    }

    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing Instapay Payment from ID: " + instapayId);
        System.out.println("Amount: " + amount);
        return true;
    }
}
