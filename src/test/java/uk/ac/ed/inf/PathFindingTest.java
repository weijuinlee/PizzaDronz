package uk.ac.ed.inf;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.LngLat;
import org.junit.Test;

/**
 * Unit test for path finding
 *
 *  @author B209981
 */

public class PathFindingTest {

    @Test
    public void testCreateFlightPaths() {

        // Prepare test data
        List<Order> orders = new ArrayList<>();
        List<List<Node>> paths = new ArrayList<>();

        // Mock data for orders and paths
        Order order1 = new Order();
        order1.setOrderNo("12345678");
        order1.setOrderStatus(OrderStatus.DELIVERED);
        order1.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order1.setPriceTotalInPence(1000);

        Order order2 = new Order();
        order2.setOrderNo("12345678");
        order2.setOrderStatus(OrderStatus.DELIVERED);
        order2.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order2.setPriceTotalInPence(1000);

        orders.add(order1);
        orders.add(order2);

        List<Node> path1 = new ArrayList<>();
        path1.add(new Node(new LngLat(0, 0)));
        path1.add(new Node(new LngLat(1, 1)));

        List<Node> path2 = new ArrayList<>();
        path2.add(new Node(new LngLat(2, 2)));
        path2.add(new Node(new LngLat(3, 3)));

        paths.add(path1);
        paths.add(path2);

        // Call the method
        List<SingleMove> flightPaths = PathFinding.createFlightPaths(orders, paths);

        // Assert the result
        assertNotNull(flightPaths);
    }

    @Test
    public void testFilterOrdersWithoutValidPaths() {
        // Prepare test data
        List<Order> orders = new ArrayList<>();
        List<List<Node>> paths = new ArrayList<>();

        // Mock data for orders and paths
        Order order1 = new Order();
        order1.setOrderNo("12345678");
        order1.setOrderStatus(OrderStatus.DELIVERED);
        order1.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order1.setPriceTotalInPence(1212);

        Order order2 = new Order();
        order2.setOrderNo("12345678");
        order2.setOrderStatus(OrderStatus.DELIVERED);
        order2.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order2.setPriceTotalInPence(1212);

        Order order3 = new Order();
        order3.setOrderNo("12345678");
        order3.setOrderStatus(OrderStatus.DELIVERED);
        order3.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        order3.setPriceTotalInPence(1212);

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        // Valid path for order1
        List<Node> path1 = new ArrayList<>();
        path1.add(new Node(new LngLat(0, 0)));
        path1.add(new Node(new LngLat(1, 1)));
        paths.add(path1);

        // No path for order2
        paths.add(null);

        // Empty path for order3
        paths.add(new ArrayList<>());

        // Call the method
        List<Order> invalidOrders = PathFinding.filterOrdersWithoutValidPaths(paths, orders);

        // Assert the result
        assertNotNull(invalidOrders);
        assertEquals(2, invalidOrders.size());
        assertTrue(invalidOrders.contains(order2));
    }

    @Test
    public void testCalculateAngle() {
        // Create two nodes representing coordinates
        Node node1 = new Node(new LngLat(0, 0));
        Node node2 = new Node(new LngLat(1, 1));

        // Calculate the angle between the two nodes
        double angle = PathFinding.calculateAngle(node1, node2);

        // Assert the result
        assertEquals(45.0, angle, 0.01); // Assuming the expected angle is 45 degrees
    }

    @Test
    public void testDroneFile() {

        // Prepare test data
        List<Node> path1 = Arrays.asList(new Node(new LngLat(0, 0)), new Node(new LngLat(1, 1)));
        List<Node> path2 = Arrays.asList(new Node(new LngLat(2, 2)), new Node(new LngLat(3, 3)));
        List<List<Node>> paths = Arrays.asList(path1, path2);

        // Call the method
        String droneFile = PathFinding.droneFile(paths);

        // Assert the result
        assertNotNull(droneFile);
    }

}
