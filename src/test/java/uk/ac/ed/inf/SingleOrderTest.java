package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import static org.junit.Assert.*;

/**
 * Unit test for single order
 *
 *  @author B209981
 */

public class SingleOrderTest {

    @Test
    public void whenConstructedWithOrderThenFieldsAreCorrectlyAssigned() {

        // Arrange
        Order order = new Order();
        order.setOrderNo("12345678");
        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order.setPriceTotalInPence(1000);

        // Act
        SingleOrder singleOrder = new SingleOrder(order);

        // Assert
        assertEquals("Order number should match", "12345678", singleOrder.getOrderNo());
        assertEquals("Order status should match", String.valueOf(OrderStatus.DELIVERED), singleOrder.getOrderStatus());
        assertEquals("Order validation code should match", String.valueOf(OrderValidationCode.NO_ERROR), singleOrder.getOrderValidationCode());
        assertEquals("Cost in pence should match", 1000, singleOrder.getCostInPence());
    }
}
