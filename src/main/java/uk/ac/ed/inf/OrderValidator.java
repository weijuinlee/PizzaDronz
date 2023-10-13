package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import static uk.ac.ed.inf.ilp.constant.OrderStatus.*;
import static uk.ac.ed.inf.ilp.constant.OrderValidationCode.*;
import static uk.ac.ed.inf.ilp.constant.SystemConstants.*;

public class OrderValidator implements OrderValidation {

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        HashMap<String, Integer> pizzaPrices = getPizzaPrices(definedRestaurants);
        HashMap<String, Integer> orderPizzaDetails = getOrderPizzaDetails(orderToValidate);

        if (isPizzaNameValid(orderToValidate, orderPizzaDetails, pizzaPrices)){
            if (isUnderMaxCount(orderToValidate)) {
                if (isTotalValid(orderToValidate, orderPizzaDetails, pizzaPrices)){
                    if (isCreditCardValid(orderToValidate)) {
                        isPizzaFromOneRestaurant(orderPizzaDetails, definedRestaurants);
                        orderToValidate.setOrderValidationCode(NO_ERROR);
                        orderToValidate.setOrderStatus(VALID_BUT_NOT_DELIVERED);
                    }
                }
            }
        }

        return orderToValidate;
    }

    /**
     * Checks if pizza name exist in pizzaPrices
     * @param orderToValidate Order to be checked,  HashMap<String, Integer> orderedPizzaPrices, HashMap<String, Integer> pizzaPrices to be checked against
     * Sets TOTAL_INCORRECT if false
     */
    private static boolean isPizzaNameValid(Order orderToValidate, HashMap<String, Integer> orderedPizzaPrices, HashMap<String, Integer> pizzaPrices) {

        Set<String> orderedPizzaNames = orderedPizzaPrices.keySet();
        Set<String> pizzaNames = pizzaPrices.keySet();

        // Check if orderedPizzaNames is a subset of pizzaNames
        if (pizzaNames.containsAll(orderedPizzaNames)) {
            return true;
        } else {
            orderToValidate.setOrderValidationCode(PIZZA_NOT_DEFINED);
            return false;
        }
    }

    /**
     * Checks if total is valid
     * @param orderToValidate Order to be checked,  HashMap<String, Integer> pizzaPrices pizzaPrice to be checked against
     * Sets TOTAL_INCORRECT if false
     */
    private static boolean isTotalValid(Order orderToValidate, HashMap<String, Integer> orderedPizzaPrices, HashMap<String, Integer> pizzaPrices) {

        Set<String> orderedPizzaNames = orderedPizzaPrices.keySet();

        //set sum as 100 due to delivery charge
        int sum = ORDER_CHARGE_IN_PENCE;

        //get the total amount plus delivery charge
        for (String orderedPizzaName : orderedPizzaNames) {
            if (pizzaPrices.containsKey(orderedPizzaName)) {
                sum += pizzaPrices.get(orderedPizzaName);
            }
        }

        //Check if the total amount plus delivery charge matches the total amount provided
        if (sum != orderToValidate.getPriceTotalInPence()){
            orderToValidate.setOrderValidationCode(TOTAL_INCORRECT);
            return false;
        }
        return true;
    }

    /**
     * Obtain pairs of pizza name and price from order
     * @param orderToValidate order to be checked.
     * @return a HashMap of pizza name and price pairs from order.
     */
    private HashMap<String, Integer> getOrderPizzaDetails (Order orderToValidate) {

        HashMap<String,Integer> orderedPizzaPrices = new HashMap<>();

        // Obtain a hashmap of pizza name and price pairs from order
        for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
            orderedPizzaPrices.put(pizza.name(), pizza.priceInPence());
        }
        return orderedPizzaPrices;
    }

    /**
     * Obtain pairs of pizza name and price from all restaurants
     * @param definedRestaurants Restaurants to be checked.
     * @return a HashMap of pizza name and price pairs.
     */
    private HashMap<String, Integer> getPizzaPrices(Restaurant[] definedRestaurants) {

        HashMap<String,Integer> pizzaPrices = new HashMap<>();

        // Obtain a hashmap of pizza name and price pairs from order
        for (Restaurant definedRestaurant : definedRestaurants) {
            Pizza[] pizzaList = definedRestaurant.menu();
            for (Pizza pizza : pizzaList) {
                pizzaPrices.put(pizza.name(),pizza.priceInPence());
            }
        }

        return pizzaPrices;
    }

    /**
     * Checks if pizza are from one restaurant
     * @param orderPizzaDetails pizza ordered
     * @param definedRestaurants menu to be checked against
     * Sets MAX_PIZZA_COUNT_EXCEEDED if false
     */
    private static boolean isPizzaFromOneRestaurant(HashMap<String, Integer> orderPizzaDetails, Restaurant[] definedRestaurants) {
//        orderPizzaDetails
        // Check if the order has less than 4 pizzas
//        if (orderToValidate.getPizzasInOrder().length > 4) {
//            orderToValidate.setOrderValidationCode(MAX_PIZZA_COUNT_EXCEEDED);
//            return false;
//        }
        return true;
    }


    /**
     * Checks if the order has less than 4 pizzas
     * @param orderToValidate Order to be checked.
     * Sets MAX_PIZZA_COUNT_EXCEEDED if false
     */
    private static boolean isUnderMaxCount(Order orderToValidate) {

        // Check if the order has less than 4 pizzas
        if (orderToValidate.getPizzasInOrder().length > MAX_PIZZAS_PER_ORDER) {
            orderToValidate.setOrderValidationCode(MAX_PIZZA_COUNT_EXCEEDED);
            return false;
        }
        return true;
    }

    /**
     * Checks if credit card is valid
     * @param orderToValidate Order to be checked.
     * Sets validation code according to checks
     */
    private static boolean isCreditCardValid(Order orderToValidate) {

        if (isCardNumberValid(orderToValidate.getCreditCardInformation().getCreditCardNumber())) {
            orderToValidate.setOrderValidationCode(CARD_NUMBER_INVALID);
            return false;
        } else if (isCVVValid(orderToValidate.getCreditCardInformation().getCvv())) {
            orderToValidate.setOrderValidationCode(CVV_INVALID);
            return false;
        } else if (isExpiryDateValid(orderToValidate.getCreditCardInformation().getCreditCardExpiry())) {
            orderToValidate.setOrderValidationCode(EXPIRY_DATE_INVALID);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if expiry date is valid
     * @param expiryDate String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    private static boolean isExpiryDateValid(String expiryDate) {

        // Define the expected credit card date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
        dateFormat.setLenient(false);

        // Get the current date
        Date currentDate = new Date();

        try {
            // Parse the expiry date
            Date parsedExpiryDate = dateFormat.parse(expiryDate);

            // Check if the parsed date is not in the past
            return parsedExpiryDate.before(currentDate);

        } catch (ParseException e) {
            // If parsing fails, the date format is invalid
            return true;
        }
    }

    /**
     * Checks if cvv number is valid
     * @param CVV String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    private static boolean isCVVValid(String CVV) {

        // Check if the cvv is numeric
        if (isNumeric(CVV)) {

            // Check if the number has exactly 3 digits
            return CVV.length() != 3;
        } else {
            return true;
        }

    }

    /**
     * Checks if credit card number is valid
     * @param creditCardNumber String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    private static boolean isCardNumberValid(String creditCardNumber) {

        // Check if the number is numeric
        if (isNumeric(creditCardNumber)) {

            // Check if the number has exactly 16 digits
            if (creditCardNumber.length() == 16) {
                // Check if the cleaned number is a valid credit card number using the Luhn algorithm
                return !isValidLuhn(creditCardNumber);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * Luhn algorithm for validating credit card number. The implementation is taken from the source below.
     * https://www.geeksforgeeks.org/luhn-algorithm/
     * @return True if valid, otherwise false.
     */
    private static boolean isValidLuhn(String number) {

        int nDigits = number.length();

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {

            int d = number.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            // We add two digits to handle
            // cases that make two digits
            // after doubling
            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    /**
     * Checks if string is numeric
     * @param strNum String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

