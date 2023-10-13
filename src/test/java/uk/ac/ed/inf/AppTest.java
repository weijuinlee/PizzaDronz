package uk.ac.ed.inf;

import static org.junit.Assert.*;
import org.junit.Test;
import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Unit test for PizzaDronz App.
 */
public class AppTest 
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
        System.out.println(calculatedPosition);
        System.out.println(nextPosition);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle20(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 20);
        LngLat calculatedPosition = new LngLat(-3.186733046106882, 55.9445453030215);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle50(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 50);
        LngLat calculatedPosition = new LngLat(-3.186777581858547, 55.94460890666647);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle90(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 90);
        LngLat calculatedPosition = new LngLat(-3.186874, 55.944644);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle140(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 140);
        LngLat calculatedPosition = new LngLat(-3.1869889066664676, 55.94459041814145);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle190(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 190);
        LngLat calculatedPosition = new LngLat(-3.1870217211629517, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle260(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 260);
        LngLat calculatedPosition = new LngLat(-3.18690004722665, 55.944346278837045);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle300(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 300);
        LngLat calculatedPosition = new LngLat(-3.186799, 55.94436409618943);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle350(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 350);
        LngLat calculatedPosition = new LngLat(-3.1867262788370483, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle999(){
        // The special value 999 means "hover and do not change position"
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 999);
        assertTrue(approxEq(nextPosition, appletonTower));
    }
}
