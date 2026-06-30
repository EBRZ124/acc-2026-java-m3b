package org.example.payment;

import org.example.model.PaymentResult;

public class GiftCardPayment extends PaymentMethod{
    private final String code;
    private double balance;

    public GiftCardPayment(String code, double balance) {
        super("GiftCard");
        this.code = code;
        this.balance = balance;
    }

    @Override
    public PaymentResult processPayment(double amount){
        if (code == null || code.isBlank()) {
            return new PaymentResult(false, "Gift card number is required.");
        }
        if (amount <= 0) {
            return new PaymentResult(false, "Payment amount must be greater than 0.");
        }
        if (balance < amount) {
            return new PaymentResult(false, "Gift card has " + balance +
                    " but required amount is: " + amount);
        }
        return new PaymentResult(true, "Paid " + amount + " using gift card");
    }
}