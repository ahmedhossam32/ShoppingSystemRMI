package Mainclasses;

import java.io.Serializable;


public class Product implements Serializable {

    private String productId;
    private String name;
    private double price;
    private String description;
    private int quantity;  
    
    public Product() {
    }

    public Product(String productId,
                   String name,
                   double price,
                   String description,
                   int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }


    public boolean isInStock() {
        return quantity > 0;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) return;
        if (amount > quantity) {
            quantity = 0;
        } else {
            quantity -= amount;
        }
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) return;
        quantity += amount;
    }

    /** Optional helper for GUI / debugging. */
    public String getDetails() {
        return productId + " - " + name + " (" + price + " EGP, qty: " + quantity + ")";
    }

    // ---------------------- Getters & Setters ----------------------

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
