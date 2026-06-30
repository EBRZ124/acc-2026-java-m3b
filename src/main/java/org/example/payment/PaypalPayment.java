package org.example.payment;

import org.example.model.PaymentResult;

public class PaypalPayment extends PaymentMethod {
    private final String email;

    public PaypalPayment(String email) {
        super("PayPal");
        this.email = email;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if (email == null || email.isBlank()) {
            return new PaymentResult(false, "PayPal email is required.");
        }
        if (amount <= 0) {
            return new PaymentResult(false, "Payment amount must be greater than 0.");
        }

        return new PaymentResult(true, "Paid " + amount + " using PayPal");
    }
}