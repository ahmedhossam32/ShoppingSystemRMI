package Mainclasses;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productId;
    private String productName;
    private double productPrice;
    private int quantity;

    public CartItem() {}

    public CartItem(String productId, String productName, double productPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public CartItem(Product product, int quantity) {
    this.productId = product.getProductId();
    this.productName = product.getName();
    this.productPrice = product.getPrice();
    this.quantity = quantity;
}

    
    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public double getItemTotal() {
        return productPrice * quantity;
    }
}
