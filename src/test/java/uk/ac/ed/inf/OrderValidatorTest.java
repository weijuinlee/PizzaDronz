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
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)), "22"));
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
        order.setCreditCardInformation(new CreditCardInformation("516767922379583", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)), "222"));
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
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
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
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
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
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
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
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
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
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
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
    public void whenAllValidationsPassThenOrderIsDelivered() {
        // Arrange
        order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setCreditCardInformation(new CreditCardInformation("5167679223795831", String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12) , ThreadLocalRandom.current().nextInt(24, 29)), "222"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("K", 1212)});
        order.setPriceTotalInPence(1212 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Act
        Order validatedOrder = orderValidator.validateOrder(order, restaurants);

        // Assert
        assertEquals(OrderStatus.DELIVERED, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
    }
}