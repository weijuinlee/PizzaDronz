package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;
import java.util.regex.*;

import static uk.ac.ed.inf.ilp.constant.OrderStatus.*;
import static uk.ac.ed.inf.ilp.constant.OrderValidationCode.*;
import static uk.ac.ed.inf.ilp.constant.SystemConstants.*;

public class OrderValidator implements OrderValidation {

    /**
     * Checks if order is valid
     * @author B209981
     * @param orderToValidate Order from REST
     * @param definedRestaurants List of restaurant details
     * Return true if all pizza ordered are available in at least one restaurant
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        // Extract pizzas ordered into list
        List<String> pizzaOrders = Arrays.stream(orderToValidate.getPizzasInOrder()).map(Pizza::name).collect(Collectors.toList());

        // Extract pairs of name and price of pizzas for quick search
        HashMap<String, Integer> pizzaPrices = getPizzaPrices(definedRestaurants);

        if (orderToValidate.getPizzasInOrder().length > 0 && definedRestaurants.length > 0){
            if (!isUnderMaxCount(orderToValidate)){
                orderToValidate.setOrderValidationCode(MAX_PIZZA_COUNT_EXCEEDED);
                orderToValidate.setOrderStatus(INVALID);
            } else if (!isCVVValid(orderToValidate.getCreditCardInformation().getCvv())) {
                orderToValidate.setOrderValidationCode(CVV_INVALID);
                orderToValidate.setOrderStatus(INVALID);
            } else if (isExpiryDateValid(orderToValidate.getCreditCardInformation().getCreditCardExpiry())) {
                orderToValidate.setOrderValidationCode(EXPIRY_DATE_INVALID);
                orderToValidate.setOrderStatus(INVALID);
            } else if (!isCardNumberValid(orderToValidate.getCreditCardInformation().getCreditCardNumber())) {
                orderToValidate.setOrderValidationCode(CARD_NUMBER_INVALID);
                orderToValidate.setOrderStatus(INVALID);
            } else if (!isPizzaNameValid(pizzaOrders, pizzaPrices)){
                orderToValidate.setOrderValidationCode(PIZZA_NOT_DEFINED);
                orderToValidate.setOrderStatus(INVALID);
            } else if (!isTotalValid(orderToValidate, pizzaOrders, pizzaPrices)){
                orderToValidate.setOrderValidationCode(TOTAL_INCORRECT);
                orderToValidate.setOrderStatus(INVALID);
            } else if (!isPizzaFromOneRestaurant(pizzaOrders, definedRestaurants)){
                orderToValidate.setOrderValidationCode(PIZZA_FROM_MULTIPLE_RESTAURANTS);
                orderToValidate.setOrderStatus(INVALID);
            } else if (!isRestaurantOpen(pizzaOrders, definedRestaurants)){
                orderToValidate.setOrderValidationCode(RESTAURANT_CLOSED);
                orderToValidate.setOrderStatus(INVALID);
            } else {
                isRestaurantOpen(pizzaOrders, definedRestaurants);
                orderToValidate.setOrderStatus(VALID_BUT_NOT_DELIVERED);
                orderToValidate.setOrderValidationCode(NO_ERROR);
            }
        }
        return orderToValidate;
    }

    /**
     * Checks if pizza name exist in menus
     * @param orderedPizzas List of pizza ordered
     * @param pizzaPrices Pairs of pizza and price
     * Return true if all pizza ordered are available in at least one restaurant
     */
    private static boolean isPizzaNameValid(List orderedPizzas, HashMap<String, Integer> pizzaPrices) {

        Set<String> uniqueOrderedPizzas = (Set<String>) orderedPizzas.stream().collect(Collectors.toSet());
        Set<Integer> uniquePizzaNames = new HashSet(pizzaPrices.keySet());

        //Check if all pizza ordered are available
        return uniquePizzaNames.containsAll(uniqueOrderedPizzas);
    }

    /**
     * Checks if total is valid
     * @param orderToValidate Order get total cost input
     * @param orderedPizzas List of pizza ordered
     * @param pizzaPrices Pairs of pizza and price
     * Return true if total cost is correct
     */
    private static boolean isTotalValid(Order orderToValidate, List orderedPizzas, HashMap<String, Integer> pizzaPrices) {

        //set sum as 100 due to delivery charge
        int sum = ORDER_CHARGE_IN_PENCE;

        //get the total amount plus delivery charge
        for (Object pizza : orderedPizzas) {
            if (pizzaPrices.containsKey(pizza)) {
                sum += pizzaPrices.get(pizza);
            }
        }

        //Check if the total amount plus delivery charge matches the total amount provided
        return sum == orderToValidate.getPriceTotalInPence();
    }

    /**
     * Obtain pairs of pizza name and price from all restaurants
     * @param definedRestaurants Restaurants to be checked
     * Return HashMap of pizza name and price pairs
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
     * Obtain a set of opened days
     * @param definedRestaurants Restaurants to be checked
     * Return HashSet of days
     */
    private HashSet<String> getOpenedDays(Restaurant[] definedRestaurants) {

        HashSet<String> openedDays = new HashSet<>();

        for (Restaurant definedRestaurant : definedRestaurants) {
            DayOfWeek[] openedDaysList = definedRestaurant.openingDays();
            for ( DayOfWeek openingDays : openedDaysList) {
                openedDays.add(openingDays.name());
            }
        }
        return openedDays;
    }

    /**
     * Checks if pizza are from one restaurant
     * @param pizzaOrders List of pizza ordered
     * @param definedRestaurants menu to be checked against
     * Return true if orders are all fulfilled by only one restaurant
     */
    private static boolean isPizzaFromOneRestaurant(List pizzaOrders, Restaurant[] definedRestaurants) {

        boolean isValid = false;
        for (Restaurant definedRestaurant : definedRestaurants){
            ArrayList<String> pizzasOnMenu = Arrays.stream(definedRestaurant.menu()).map(Pizza::name).collect(Collectors.toCollection(ArrayList::new));
            if (pizzasOnMenu.containsAll(pizzaOrders)) {
                isValid = true;
            }
        }
        return isValid;
    }

    /**
     * Checks if pizza are from one restaurant
     * @param pizzaOrders List of pizza ordered
     * @param definedRestaurants menu to be checked against
     * Return true if orders are fulfil/ed by a single restaurant when open
     */
    private static boolean isRestaurantOpen(List pizzaOrders, Restaurant[] definedRestaurants) {

        boolean isValid = false;
        for (Restaurant definedRestaurant : definedRestaurants){
            ArrayList<String> pizzasOnMenu = Arrays.stream(definedRestaurant.menu()).map(Pizza::name).collect(Collectors.toCollection(ArrayList::new));
            if (pizzasOnMenu.containsAll(pizzaOrders)) {
                ArrayList<String> openedDays = Arrays.stream(definedRestaurant.openingDays()).map(DayOfWeek::name).collect(Collectors.toCollection(ArrayList::new));

                //Get the current date
                LocalDate currentDate = LocalDate.now();

                // Get the current day of the week
                DayOfWeek currentDay = currentDate.getDayOfWeek();

                if (openedDays.contains(currentDay.name())){
                    isValid = true;
                }
            }
        }
        return isValid;
    }

    /**
     * Checks if the order has less than 5 pizzas
     * @param orderToValidate Order to be checked
     * Returns true if less than 5 pizza
     */
    private static boolean isUnderMaxCount(Order orderToValidate) {

        // Check if the order has less than 5 pizzas
        return orderToValidate.getPizzasInOrder().length <= MAX_PIZZAS_PER_ORDER;
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

        boolean isValid = false;

        // Check if the cvv is numeric
        if (isNumeric(CVV)) {

            // Check if the number has exactly 3 digits
            String regex = "^[0-9]{3}$";
            isValid =  Pattern.matches(regex, CVV);
        }
        return isValid;
    }

    /**
     * Checks if credit card number is valid
     * @param creditCardNumber String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    private static boolean isCardNumberValid(String creditCardNumber) {

        boolean isValid = false;

        //Check is number are numeric
        if (isNumeric(creditCardNumber)
                && isValidLuhn(creditCardNumber)
                && creditCardNumber.length() == 16) {

            //Check is card is visa or master
            String regex = "^(?:(?<visa>4[0-9]{3})|"
                    + "(?<mastercard>(222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720|5[1-5][0-9]{2})))";
            isValid = Pattern.matches(regex, creditCardNumber.substring(0,4));
        }
        return isValid;
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

