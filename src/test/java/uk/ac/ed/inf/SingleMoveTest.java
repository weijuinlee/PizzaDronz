package uk.ac.ed.inf;

import org.junit.Test;
import static org.junit.Assert.*;

public class SingleMoveTest {

    @Test
    public void whenConstructedThenFieldsAreCorrectlyAssigned() {
        // Arrange
        String orderNo = "12345678";
        double fromLongitude = -3.192473;
        double fromLatitude = 55.946233;
        double angle = 90.0;
        double toLongitude = -3.191812;
        double toLatitude = 55.945994;

        // Act
        SingleMove singleMove = new SingleMove(orderNo, fromLongitude, fromLatitude, angle, toLongitude, toLatitude);

        // Assert
        assertEquals("Order number should match", orderNo, singleMove.getOrderNo());
        assertEquals("From longitude should match", fromLongitude, singleMove.getFromLongitude(), 0.000001);
        assertEquals("From latitude should match", fromLatitude, singleMove.getFromLatitude(), 0.000001);
        assertEquals("Angle should match", angle, singleMove.getAngle(), 0.000001);
        assertEquals("To longitude should match", toLongitude, singleMove.getToLongitude(), 0.000001);
        assertEquals("To latitude should match", toLatitude, singleMove.getToLatitude(), 0.000001);
    }
}