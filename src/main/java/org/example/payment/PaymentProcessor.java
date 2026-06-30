package org.example.payment;

import org.example.model.Order;
import org.example.model.PaymentResult;

public class PaymentProcessor {
    public PaymentResult process(Order order, PaymentMethod paymentMethod){

        if (order.isPaid()) {
            return new PaymentResult(false, "This order has already been paid off.");
        }

        if (order.getItems().isEmpty()){
            return new PaymentResult(false, "Nothing to be paid for.");
        }

        PaymentResult result = paymentMethod.pay(order.calculateTotal());

        if(result.isSuccessful()){
            order.markAsPaid();
        }

        return result;
    }
}
