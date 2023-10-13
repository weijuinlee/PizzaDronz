package uk.ac.ed.inf;

import static org.junit.Assert.*;
import org.junit.Test;
import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Unit test for LngLatHandler
 */
public class LngLatHandlerTest
{
    private static final LngLat appletonTower = new LngLat(-3.186874, 55.944494);
    private final LngLat businessSchool = new LngLat(-3.1873,55.9430);
    LngLatHandler lngLatHandler = new LngLatHandler();

    private boolean approxEq(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-12;
    }

    @Test
    public void testDistanceTo(){
        double calculatedDistance = 0.0015535481968716011;
        assertTrue(approxEq(lngLatHandler.distanceTo(appletonTower, businessSchool), calculatedDistance));
    }

    @Test
    public void testIsCloseTo(){
        LngLat alsoAppletonTower = new LngLat(-3.186767933982822, 55.94460006601717);
        assertTrue(lngLatHandler.isCloseTo(appletonTower, alsoAppletonTower));
    }

    @Test
    public void testNotCloseTo(){
        assertFalse(lngLatHandler.isCloseTo(appletonTower, businessSchool));    }

    private boolean approxEq(LngLat l1, LngLat l2) {
        return approxEq(l1.lng(), l2.lng()) &&
                approxEq(l1.lat(), l2.lat());
    }

    @Test
    public void testAngle0(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 0);
        LngLat calculatedPosition = new LngLat(-3.186724, 55.944494);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle999(){
        // The special value 999 means "hover and do not change position"
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 999);
        assertTrue(approxEq(nextPosition, appletonTower));
    }
}
