package uk.ac.ed.inf;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import uk.ac.ed.inf.ilp.data.*;
import java.time.DayOfWeek;


/**
 * Unit test for Main App
 *
 *  @author B209981
 */

public class AppTest
{
    @Test
    public void validateArgumentsNullTest() {
        assertFalse(App.validateCommandLineArgs(null));
    }

    @Test
    public void lessThanNumberOfArgumentsRequiredTest() {
        String[] args = new String[2];
        args[0] = "2023-09-09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        assertFalse(App.validateCommandLineArgs(args));
    }

    @Test
    public void moreThanNumberOfArgumentsRequiredTest() {
        String[] args = new String[4];
        args[0] = "2023-09-09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        args[3] = "cabbage";
        assertFalse(App.validateCommandLineArgs(args));
    }

    @Test
    public void exactNumberOfArgumentsRequiredTest() {
        String[] args = new String[3];
        args[0] = "2023-09-09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertTrue(App.validateCommandLineArgs(args));
    }

    @Test
    public void invalidDateTest() {
        String[] args = new String[3];
        args[0] = "2023-13-12";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertFalse(App.validateCommandLineArgs(args));
    }

    @Test
    public void invalidDateFormatTest() {
        String[] args = new String[3];
        args[0] = "2023/09/09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertFalse(App.validateCommandLineArgs(args));
    }

    @Test
    public void invalidMalformedUrlTest() {
        String[] args = new String[3];
        args[0] = "2023-11-21";
        args[1] = "htts://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertFalse(App.validateCommandLineArgs(args));
    }

    @Test
    public void invalidUriSyntaxTest() {
        String[] args = new String[3];
        args[0] = "2023-11-21";
        args[1] = "https://ilp-rest.azurewebsites.net w3r4fregf3e";
        args[2] = "cabbage";
        assertFalse(App.validateCommandLineArgs(args));
    }

    @Test
    public void testMain() {
        // Mock the command-line arguments
        String[] args = {"2023-11-11", "https://ilp-rest.azurewebsites.net"};

        // Mock the Client class
        Client client = Mockito.mock(Client.class);
        when(client.isAlive()).thenReturn(true);

        // Mock the responses from the REST service
        Order[] mockOrders = {};

        Restaurant[] mockRestaurants = new Restaurant[]{
                new Restaurant("myRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY}, new Pizza[]{new Pizza("A", 1212)}),
                new Restaurant("otherRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY}, new Pizza[]{new Pizza("K", 1212), new Pizza("D", 1212)}),
                new Restaurant("anotherRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("T", 1212), new Pizza("E", 1212)})
        };
        when(client.orders(args[0])).thenReturn(mockOrders);
        when(client.restaurants()).thenReturn(mockRestaurants);
        App.main(args);
    }
}