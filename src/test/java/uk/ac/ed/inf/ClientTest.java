package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for Client
 *
 *  @author B209981
 */

public class ClientTest {

    private static final String TEST_URL = "https://ilp-rest.azurewebsites.net/";

    @Test
    public void testIsAlive() {
        Client client = new Client(TEST_URL);
        assertTrue("Service should be alive", client.isAlive());
    }

    @Test
    public void testOrders() {
        Client client = new Client(TEST_URL);
        Order[] orders = client.orders("2023-11-11");
    }

    @Test
    public void testRestaurants() {
        Client client = new Client(TEST_URL);
        Restaurant[] restaurants = client.restaurants();
    }

    @Test
    public void testCentralArea() {
        Client client = new Client(TEST_URL);
        NamedRegion centralArea = client.centralArea();
    }

    @Test
    public void testNoFlyZones() {
        Client client = new Client(TEST_URL);
        NamedRegion[] noFlyZones = client.noFlyZones();
    }
}

