package org.example.payment;

import org.example.model.PaymentResult;

public class BankTransferPayment extends PaymentMethod {
    private final String accountId;
    private final String accountHolderName;
    private final double transferAmount;

    public BankTransferPayment(String accountId, String accountHolderName, double transferAmount) {
        super("BankTransfer");
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.transferAmount = transferAmount;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if (accountId == null || accountId.isBlank()) {
            return new PaymentResult(false, "Account ID is required.");
        }
        if (accountHolderName == null || accountHolderName.isBlank()) {
            return new PaymentResult(false, "Account holder name is required.");
        }
        if (transferAmount < amount) {
            return new PaymentResult(false, "Transfer amount " + transferAmount +
                    " is insufficient for required amount: " + amount);
        }
        if (transferAmount > amount) {
            return new PaymentResult(false, "Woa, don't overpay us. You need to pay " + amount
                    + " you want to pay us "+transferAmount);
        }
        if (amount <= 0) {
            return new PaymentResult(false, "Payment amount must be greater than 0.");
        }

        return new PaymentResult(true, "Paid " + amount + " using bank transfer from account " + accountId);
    }
}