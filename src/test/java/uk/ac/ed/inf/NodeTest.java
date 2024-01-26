package uk.ac.ed.inf;

import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.ed.inf.ilp.data.*;
import java.util.Objects;

/**
 * Unit test for a single node
 *
 *  @author B209981
 */

public class NodeTest {

    @Test
    public void whenConstructedThenFieldsAreCorrectlyInitialized() {
        // Arrange
        LngLat coordinates = new LngLat(-3.192473, 55.946233);

        // Act
        Node node = new Node(coordinates);

        // Assert
        assertEquals("Coordinates should match", coordinates, node.coordinates);
        assertNull("Parent should be null", node.parent);
        assertEquals("Total should be 0", 0.0, node.total, 0.000001);
        assertEquals("Cost should be 0", 0.0, node.cost, 0.000001);
        assertEquals("Estimate should be 0", 0.0, node.estimate, 0.000001);
    }

    @Test
    public void whenHashCodeCalledThenConsistentHashCodeIsReturned() {
        // Arrange
        LngLat coordinates = new LngLat(-3.192473, 55.946233);
        Node node = new Node(coordinates);

        // Act
        int hashCode = node.hashCode();

        // Assert
        assertEquals("Hash code should be consistent", Objects.hash(coordinates), hashCode);
    }

    @Test
    public void whenEqualsCalledThenCorrectlyComparesNodes() {
        // Arrange
        LngLat coordinates1 = new LngLat(-3.192473, 55.946233);
        Node node1 = new Node(coordinates1);

        LngLat coordinates2 = new LngLat(-3.192473, 55.946233);
        Node node2 = new Node(coordinates2);

        LngLat coordinates3 = new LngLat(-3.191812, 55.945994);
        Node node3 = new Node(coordinates3);

        // Act and Assert
        assertEquals("Nodes with the same coordinates should be equal", node1, node2);
        assertNotEquals("Nodes with different coordinates should not be equal", node1, node3);
        assertNotEquals("Node should not be equal to null", null, node1);
        assertNotEquals("Node should not be equal to an object of a different class", node1, new Object());
    }
}