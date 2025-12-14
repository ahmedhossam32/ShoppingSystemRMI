package DesignPatterns;

import Mainclasses.Cart;
import Mainclasses.Order;
import rmi.OrderInterface;


public class OrderProcessingFacade {

    private Cart cart;
    private PaymentContext paymentContext;
    private OrderInterface orderInterface;

    public OrderProcessingFacade(Cart cart, PaymentContext paymentContext, OrderInterface orderInterface) {
        this.cart = cart;
        this.paymentContext = paymentContext;
        this.orderInterface = orderInterface;
    }

   
    public Order proceedToCheckout() {
        try {
            if (cart == null) {
                System.out.println("Cart is null - cannot checkout");
                return null;
            }
            if (cart.getCustomer() == null || cart.getCustomer().getId() == null) {
                System.out.println("Cart customer is missing - cannot checkout");
                return null;
            }
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                System.out.println("Cart is empty - cannot checkout");
                return null;
            }

            // Step 1: Calculate total price from cart
            double totalPrice = cart.calculateTotal();
            System.out.println("Processing checkout for total: " + totalPrice + " EGP");

            // Step 2: Execute payment using the payment strategy
            boolean paymentCompleted = paymentContext.executePayment(totalPrice);

            if (!paymentCompleted) {
                System.out.println("Payment failed. No order created.");
                return null;
            }

            System.out.println("Payment successful. Creating order...");

            // Step 3A: Create order via RMI (saves to DB)
            Order createdOrder = orderInterface.placeOrder(
                    cart.getCustomer().getId(),
                    cart.getItems()
            );

            if (createdOrder == null || createdOrder.getId() == null) {
                System.out.println("Failed to create order on server");
                return null;
            }

            System.out.println("Order created: " + createdOrder.getId() + " - Processing checkout...");

            // Step 3B: Finalize checkout for SAME order (assign staff/status/etc.)
            Order finalizedOrder = orderInterface.processCheckout(createdOrder.getId(), true);

            if (finalizedOrder != null) {
                System.out.println("Checkout processed successfully for order: " + finalizedOrder.getId());
                return finalizedOrder;
            } else {
                // if your server returns null, at least return the created order
                System.out.println("Checkout finalize returned null - returning created order");
                return createdOrder;
            }

        } catch (Exception e) {
            System.err.println("Error during checkout process: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Cart getCart() {
        return cart;
    }

    public PaymentContext getPaymentContext() {
        return paymentContext;
    }
}
