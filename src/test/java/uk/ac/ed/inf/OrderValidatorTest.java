package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Before;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Unit test for Order validator
 *
 *  @author B209981
 */

public class OrderValidatorTest {

    private Order order;
    private Restaurant[] restaurants;
    private OrderValidator orderValidator;

    @Before
    public void setUp() {
        orderValidator = new OrderValidator();
        restaurants = new Restaurant[]{
                new Restaurant("myRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY}, new Pizza[]{new Pizza("A", 1212)}),
                new Restaurant("otherRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY}, new Pizza[]{new Pizza("K", 1212), new Pizza("D", 1212)}),
                new Restaurant("anotherRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("T", 1212), new Pizza("E", 1212)})
        };
    }

    @Test
    public void whenCVVIsInvalidThenValidationFails() {

        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(2, 12), ThreadLocalRandom.current().nextInt(24, 29)), "22"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("K", 1212), new Pizza("D", 1212), new Pizza("D", 1212), new Pizza("D", 1212)});
        order.setPriceTotalInPence(4848 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void whenCardNumberIsInvalidThenValidationFails() {

        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("516767922379583", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(2, 12), ThreadLocalRandom.current().nextInt(24, 29)), "222"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("K", 1212), new Pizza("D", 1212), new Pizza("D", 1212), new Pizza("D", 1212)});
        order.setPriceTotalInPence(4848 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void whenCreditCardNumberIsValidThenLuhnValidationSucceeds() throws Exception {

        // Arrange
        Method isValidLuhnMethod = OrderValidator.class.getDeclaredMethod("isValidLuhn", String.class);
        isValidLuhnMethod.setAccessible(true); // Make the method accessible

        // Valid credit card numbers for testing (these should pass Luhn's algorithm)
        String[] validNumbers = {"4539578763621486", "4716347184862961", "4929778869082405"};

        for (String validNumber : validNumbers) {
            // Act
            boolean isValid = (boolean) isValidLuhnMethod.invoke(null, validNumber);

            // Assert
            assertTrue("Credit card number " + validNumber + " should be valid.", isValid);
        }
    }

    @Test
    public void whenCreditCardNumberIsInvalidThenLuhnValidationFails() throws Exception {

        // Arrange
        Method isValidLuhnMethod = OrderValidator.class.getDeclaredMethod("isValidLuhn", String.class);
        isValidLuhnMethod.setAccessible(true); // Make the method accessible

        // Invalid credit card numbers for testing (these should fail Luhn's algorithm)
        String[] invalidNumbers = {"4539578763621487", "4716347184862962", "4929778869082406"};

        for (String invalidNumber : invalidNumbers) {
            // Act
            boolean isValid = (boolean) isValidLuhnMethod.invoke(null, invalidNumber);

            // Assert
            assertFalse("Credit card number " + invalidNumber + " should be invalid.", isValid);
        }
    }

    @Test
    public void whenStringIsNumeric_thenValidationSucceeds() {

        // Valid numeric strings for testing
        String[] numericStrings = {"123", "0042", "-123456789", "0", "9876543210"};

        for (String numericString : numericStrings) {
            // Act
            boolean isNumeric = OrderValidator.isNumeric(numericString);

            // Assert
            assertTrue("String " + numericString + " should be numeric.", isNumeric);
        }
    }

    @Test
    public void whenStringIsNotNumeric_thenValidationFails() {

        // Invalid numeric strings for testing
        String[] nonNumericStrings = {"abc", "12.34", "1e10", "123abc456", "", " ", null};

        for (String nonNumericString : nonNumericStrings) {
            // Act
            boolean isNumeric = OrderValidator.isNumeric(nonNumericString);

            // Assert
            assertFalse("String " + nonNumericString + " should not be numeric.", isNumeric);
        }
    }

    @Test
    public void whenExpiryDateIsInvalidThenValidationFails() {

        // Arrange
        order = new Order();
            order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
            order.setOrderDate(LocalDate.of(2023, 9, 1));
            order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", 13 , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
            order.setOrderStatus(OrderStatus.UNDEFINED);
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setPizzasInOrder(new Pizza[]{new Pizza("K", 1212), new Pizza("D", 1212), new Pizza("D", 1212), new Pizza("D", 1212)});
            order.setPriceTotalInPence(4848 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void whenMaxPizzaCountExceededThenValidationFails() {

        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(2, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("K", 1212), new Pizza("D", 1212) , new Pizza("D", 1212), new Pizza("D", 1212), new Pizza("D", 1212)});
        order.setPriceTotalInPence(4848 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void whenPizzaNameIsInvalidThenValidationFails() {

        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(2, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("NotPizza", 1212) , new Pizza("D", 1212), new Pizza("D", 1212), new Pizza("D", 1212)});
        order.setPriceTotalInPence(4848 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void whenTotalIsIncorrectThenValidationFails() {

        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(2, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("K", 1212) , new Pizza("D", 1212), new Pizza("D", 1212), new Pizza("D", 1212)});
        order.setPriceTotalInPence(484800 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void whenPizzasFromMultipleRestaurantsThenValidationFails() {

        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(2, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("D", 1212), new Pizza("D", 1212), new Pizza("D", 1212)});
        order.setPriceTotalInPence(4848 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void whenRestaurantIsClosedThenValidationFails() {

        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(2, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("T", 1212)});
        order.setPriceTotalInPence(1212 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void testGetPizzaPrices() throws Exception {
        // Arrange
        Method getPizzaPricesMethod = OrderValidator.class.getDeclaredMethod("getPizzaPrices", Restaurant[].class);
        getPizzaPricesMethod.setAccessible(true);

        // Act
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> pizzaPrices = (HashMap<String, Integer>) getPizzaPricesMethod.invoke(orderValidator, (Object) restaurants);

        // Assert
        assertNotNull("The returned HashMap should not be null", pizzaPrices);
        assertTrue("The HashMap should contain a key for pizza K", pizzaPrices.containsKey("K"));
        assertEquals("The price for K should be 1212", (Integer)1212, pizzaPrices.get("K"));
    }

    @Test
    public void testGetOpenedDays() throws Exception {
        // Arrange
        Method getOpenedDaysMethod = OrderValidator.class.getDeclaredMethod("getOpenedDays", Restaurant[].class);
        getOpenedDaysMethod.setAccessible(true); // Make the method accessible

        // Act
        @SuppressWarnings("unchecked")
        HashSet<String> openedDays = (HashSet<String>) getOpenedDaysMethod.invoke(orderValidator, (Object) restaurants);

        // Assert
        assertNotNull("The returned HashSet should not be null", openedDays);

        // Assert that the opened days are correct based on your definedRestaurants
        assertTrue("The HashSet should contain Monday", openedDays.contains(DayOfWeek.MONDAY.name()));
        assertEquals("The HashSet should contain the correct number of days", 3, openedDays.size());
    }
}