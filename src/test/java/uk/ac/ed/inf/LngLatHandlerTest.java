package uk.ac.ed.inf;

import static org.junit.Assert.*;
import org.junit.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Pizza;

/**
 * Unit test for LngLatHandler
 */
public class LngLatHandlerTest
{
    private static final LngLat appletonTower = new LngLat(-3.186874, 55.944494);
    private final LngLat businessSchool = new LngLat(-3.1873,55.9430);
    private final LngLat edinburghAirport = new LngLat(-3.3632,55.9485);
    LngLatHandler lngLatHandler = new LngLatHandler();
    private final NamedRegion central = new NamedRegion(
            "central",
            new LngLat[]{
                    new LngLat(-3.192473,55.946233),
                    new LngLat(-3.192473,55.942617),
                    new LngLat(-3.184319,55.942617),
                    new LngLat(-3.184319,55.946233),
            }
    );

    /**
     * Check for margin of error between doubles
     * @param d1 as a double
     * @param d2 as another double
     * @return true is points are close
     */
    private boolean approxEqualValues(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-12;
    }

    @Test
    public void testIsInCentralAreaGivenTrue(){
        assertTrue(lngLatHandler.isInCentralArea(appletonTower, central));
    }

    @Test
    public void testIsInCentralAreaGivenFalse() {
        assertFalse(lngLatHandler.isInCentralArea(edinburghAirport, central));
    }

    @Test
    public void testDistanceTo(){
        double calculatedDistance = 0.0015535481968716011;
        assertTrue(approxEqualValues(lngLatHandler.distanceTo(appletonTower, businessSchool), calculatedDistance));
    }

    @Test
    public void testIsCloseTo(){
        LngLat alsoAppletonTower = new LngLat(-3.186767933982822, 55.94460006601717);
        assertTrue(lngLatHandler.isCloseTo(appletonTower, alsoAppletonTower));
    }

    @Test
    public void testNotCloseTo(){
        assertFalse(lngLatHandler.isCloseTo(appletonTower, businessSchool));
    }

    /**
     * Check for margin of error between points
     * @param p1 as point
     * @param p2 as another point
     * @return true is points are close
     */
    private boolean approxEqualPoints(LngLat p1, LngLat p2) {
        return approxEqualValues(p1.lng(), p2.lng()) &&
                approxEqualValues(p1.lat(), p2.lat());
    }

    @Test
    public void testAngle0(){
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 0);
        LngLat calculatedPosition = new LngLat(-3.186724, 55.944494);
        assertTrue(approxEqualPoints(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle999(){
        // The special value 999 means "hover and do not change position"
        LngLat nextPosition = lngLatHandler.nextPosition(appletonTower, 999);
        assertTrue(approxEqualPoints(nextPosition, appletonTower));
    }
}
