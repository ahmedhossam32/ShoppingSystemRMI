package Mainclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {

    private String cartId;
    private Customer customer;
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public Cart(String cartId, Customer customer) {
        this.cartId = cartId;
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    // --------------------------------------------------
    // Add item to the cart
    // --------------------------------------------------
    public void addItem(Product product, int quantity) {

        for (CartItem item : items) {

            if (item.getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        items.add(new CartItem(product, quantity));
    }

    // --------------------------------------------------
    // Remove item completely from the cart
    // --------------------------------------------------
    public void removeItem(Product product) {
        items.removeIf(i -> i.getProductId().equals(product.getProductId()));
    }

    // --------------------------------------------------
    // Update item quantity
    // --------------------------------------------------
    public void updateItem(Product product, int quantity) {

        if (quantity <= 0) {
            removeItem(product);
            return;
        }

        for (CartItem item : items) {
            if (item.getProductId().equals(product.getProductId())) {
                item.setQuantity(quantity);
                return;
            }
        }

        // If not found, add as new
        items.add(new CartItem(product, quantity));
    }

    // --------------------------------------------------
    // Calculate total cost of cart
    // --------------------------------------------------
    public double calculateTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getProductPrice() * item.getQuantity();
        }
        return total;
    }

    // --------------------------------------------------
    // Getters & Setters
    // --------------------------------------------------

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
