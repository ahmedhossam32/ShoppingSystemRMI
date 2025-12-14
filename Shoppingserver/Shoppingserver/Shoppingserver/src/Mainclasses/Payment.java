package Mainclasses;

import java.io.Serializable;

public class Payment implements Serializable {

    private String id;
    private User customer;        
    private String paymentType;    
    private double amount;

    public Payment() {
    }

    public Payment(String id, User customer, String paymentType, double amount) {
        this.id = id;
        this.customer = customer;
        this.paymentType = paymentType;
        this.amount = amount;
      
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

   
}
