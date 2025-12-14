package DesignPatterns;

import java.io.Serializable;

public interface Payment extends Serializable {
    boolean processPayment(double amount);
}
