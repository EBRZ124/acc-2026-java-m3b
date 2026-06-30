package org.example.payment;

public class PaymentMethodFactory {
    public static PaymentMethod createCreditCardPayment(String cardNumber,String cardHolderName){
        return new CreditCardPayment(cardNumber,cardHolderName);
    }

    public static PaymentMethod createPaypalPayment(String email){
        return new PaypalPayment(email);
    }

    public static PaymentMethod createGiftCardPayment(String giftCardCode, double availabelBalance){
        return new GiftCardPayment(giftCardCode, availabelBalance);
    }

    public static PaymentMethod createBankTransferPayment(String accountId, String accountHolderName, double transferAmount) {
        return new BankTransferPayment(accountId, accountHolderName, transferAmount);
    }

}
