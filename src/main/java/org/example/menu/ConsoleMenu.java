package org.example.menu;

import org.example.config.AppConfig;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.PaymentResult;
import org.example.payment.PaymentMethod;
import org.example.payment.PaymentMethodFactory;
import org.example.payment.PaymentProcessor;

import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final PaymentProcessor paymentProcessor = new PaymentProcessor();

    private Order currentOrder;

    public void start(){
        AppConfig config = AppConfig.getInstance();
        System.out.println("Welcome to " + config.getApplicationName());

        boolean running = true;
        while(running){
            printMenu();

            System.out.print("Pick an option for creating or continuing an order: ");
            int option;
            try {
                option = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            switch (option){
                case 1 -> createOrder();
                case 2 -> addItem();
                case 3 -> viewOrder();
                case 4 -> payOrder();
                case 0 -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void createOrder(){
        while (true) {
            System.out.print("Customer name: ");
            String customerName = scanner.nextLine();

            try {
                currentOrder = Order.builder()
                        .customerName(customerName)
                        .build();
                System.out.println("Order created for " + customerName);
                return;
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage() + " Please try again.");
            }
        }
    }

    private String validateItemName() {
        String itemName;
        while (true) {
            System.out.print("Enter item name: ");
            itemName = scanner.nextLine().trim();
            if(!itemName.isEmpty()){
                return itemName;
            }
            System.out.print("Item name cannot be empty! ");
        }
    }
    private double validateItemPrice() {
        while (true) {
            System.out.print("Enter items price: ");
            String input = scanner.nextLine().trim();
            try {
                double price = Double.parseDouble(input);
                if (price <= 0) {
                    System.out.println("Price must be greater than 0.");
                    continue;
                }
                return price;
            } catch (NumberFormatException e) {
                System.out.println("That's not a valid number, try again.");
            }
        }
    }

    private int validateItemQuantity(){
        while (true) {
            System.out.print("Enter quantity: ");
            String input = scanner.nextLine().trim();
            try {
                int quantity = Integer.parseInt(input);
                if (quantity <= 0){
                    System.out.print("Quantity must be at least 1! ");
                    continue;
                }
                return quantity;
            } catch (NumberFormatException e){
                System.out.print("Not a valid number! ");
            }
        }
    }

    public void validateOrderExistance() {
        if (currentOrder== null || currentOrder.getCustomerName().isEmpty()){
            System.out.println("Order doesn't exist. Please make one.");
            createOrder();
        }
    }

    private void addItem(){
        validateOrderExistance();

        String itemName = validateItemName();
        double itemPrice = validateItemPrice();
        int itemQuantity = validateItemQuantity();

        currentOrder.addItem(new OrderItem(itemName, itemPrice, itemQuantity));
        System.out.println("Item added to order");
    }

    public void validateViewingOrder() {
        if (currentOrder == null){
            System.out.println("Your haven't made an order");
            createOrder();
        } else if (currentOrder.getItems().isEmpty()) {
            System.out.println("Your cart is empty:");
        }
    }

    private void viewOrder(){
        validateViewingOrder();

        System.out.println("Customer: " + currentOrder.getCustomerName());
        System.out.println("Status: " +  currentOrder.getStatus());
        System.out.println("Items: ");

        for (OrderItem item : currentOrder.getItems()){
            System.out.println("- " + item);
        }

        System.out.println("Total: $" + currentOrder.calculateTotal());
    }

    public boolean validatePayingForOrder() {
        if (currentOrder == null){
            System.out.print("There is nothing to pay for, you haven't made an order. ");
            createOrder();
        }
        if (currentOrder.getItems().isEmpty()) {
            int choice;
            while (true) {
                System.out.print("Your order has no items. Add an item now? (1 = yes, 0 = no): ");
                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter 1 or 0.");
                }
            }
            if (choice == 1){
                addItem();
            } else {
                return false;
            }
        }
        return !currentOrder.getItems().isEmpty();
    }

    private void payOrder(){
        if (!validatePayingForOrder()){
            return;
        }

        System.out.println("""
                Select payment method:
                1. Credit Card
                2. PayPal
                3. Gift Card
                """);
        int option;
        while (true) {
            System.out.print("Enter option of choice: ");
            try {
                option = Integer.parseInt(scanner.nextLine().trim());
                if (option < 1 || option > 3) {
                    System.out.println("Please choose 1, 2, or 3.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        PaymentMethod paymentMethod = switch(option){
            case 1 -> createCreditCardPayment();
            case 2 -> createPaypalPayment();
            case 3 -> createGiftCardPayment();
            default -> throw new IllegalArgumentException("Invalid payment method");
        };

        PaymentResult result = paymentProcessor.process(currentOrder, paymentMethod);
        System.out.println(result.getMessage());
    }

    private String validateNumberForPayments(String paymentName){
        while (true) {
            System.out.print("Enter " + paymentName + ": ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println(paymentName + " cannot be empty.");
                continue;
            }
            if (!input.matches("\\d+")) {
                System.out.println(paymentName + " must contain only digits.");
                continue;
            }
            try {
                Long.parseLong(input);
                return input;
            } catch (NumberFormatException e) {
                System.out.println(paymentName + " is too long to be valid.");
            }
        }
    }

    private String validateNameConvention(String nameType){
        while (true) {
            System.out.print("Enter " + nameType + ": ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(nameType + " cannot be empty.");
                continue;
            }
            return input;
        }
    }

    private PaymentMethod createCreditCardPayment(){
        String numberType = "Bank card number";
        String cardNumber = validateNumberForPayments(numberType);
        String nameType = "Bank card holder name";
        String cardHolderName = validateNameConvention(nameType);

        return PaymentMethodFactory.createCreditCardPayment(cardNumber, cardHolderName);
    }

    private PaymentMethod createPaypalPayment(){
        String nameType = "email";
        String email = validateNameConvention(nameType);

        return PaymentMethodFactory.createPaypalPayment(email);
    }

    private double validateGiftCardBalance(){
        while (true){
            System.out.print("Available balance: ");
            String input = scanner.nextLine().trim();
            try {
                double balance = Double.parseDouble(input);
                if (balance < 0) {
                    System.out.println("Balance cannot be negative.");
                    continue;
                }
                return balance;
            } catch (NumberFormatException e){
                System.out.println("Not a valid number!");
            }
        }
    }

    private PaymentMethod createGiftCardPayment(){
        String paymentName = "Gift card number";
        String giftCardNumber = validateNumberForPayments(paymentName);
        double giftCardBalance = validateGiftCardBalance();

        return PaymentMethodFactory.createGiftCardPayment(giftCardNumber, giftCardBalance);
    }

    private void printMenu(){
        System.out.println("""
                1. Create order
                2. Add item to order
                3. View order
                4. Pay order
                0. Exit
                """);
    }
}
