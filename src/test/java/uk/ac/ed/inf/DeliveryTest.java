package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeliveryTest {

    @Test
    public void testWriteToFile() throws IOException {

        // Prepare test data
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setOrderNo("12345678");
        order1.setOrderStatus(OrderStatus.DELIVERED);
        order1.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order1.setPriceTotalInPence(1000);
        orders.add(order1);

        Order order2 = new Order();
        order2.setOrderNo("12345678");
        order2.setOrderStatus(OrderStatus.DELIVERED);
        order2.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order2.setPriceTotalInPence(1000);
        orders.add(order2);

        String date = "2023-11-11";

        // Call the method
        Delivery.writeToFile(orders, date);

        // Verify that the JSON file is created
        String fileName = "deliveries-" + date + ".json";
        Path filePath = Path.of("resultfiles", fileName);
        assertTrue(Files.exists(filePath));

        // Verify the content of the JSON file
        String jsonContent = Files.readString(filePath);
        assertEquals("[", jsonContent.substring(0, 1)); // JSON array starts
        assertEquals("]", jsonContent.substring(jsonContent.length() - 1)); // JSON array ends
        assertEquals(2, jsonContent.chars().filter(ch -> ch == '{').count()); // Two JSON objects for two orders

        // Clean up: delete the created file
        Files.deleteIfExists(filePath);
    }
}
