package uk.ac.ed.inf;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.ed.inf.ilp.data.*;
import java.time.DayOfWeek;

/**
 * Unit test for Restaurant Handler
 *
 *  @author B209981
 */

public class RestaurantHandlerTest {

    private Restaurant[] restaurants;
    private Order validOrder;

    @Before
    public void setUp() {
        restaurants = new Restaurant[]{
                new Restaurant("myRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY}, new Pizza[]{new Pizza("A", 1212)}),
                new Restaurant("otherRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY}, new Pizza[]{new Pizza("K", 1212), new Pizza("D", 1212)}),
                new Restaurant("anotherRestaurant", new LngLat(55.945535152517735, -3.1912869215011597), new DayOfWeek[]{DayOfWeek.MONDAY}, new Pizza[]{new Pizza("T", 1212), new Pizza("E", 1212)})
        };
        // Initialize your validOrder here with some dummy data
        validOrder = new Order(/* parameters for your order */);
    }

    @Test
    public void whenPizzaIsInRestaurantMenuThenCorrectRestaurantIsReturned() {
        // Set the first pizza in the order to match one in the restaurant's menu
        validOrder.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212)});

        // Act
        Restaurant restaurant = RestaurantHandler.getRestaurantDetails(restaurants, validOrder);

        // Assert
        assertNotNull("Restaurant should not be null when pizza is in the menu", restaurant);
        assertEquals("The restaurant should be Restaurant1", "myRestaurant", restaurant.name());
    }

    @Test
    public void whenPizzaIsNotInAnyRestaurantMenuThenNullIsReturned() {
        // Set the first pizza in the order to something not in any restaurant's menu
        validOrder.setPizzasInOrder(new Pizza[]{new Pizza("Z", 1000)});

        // Act
        Restaurant restaurant = RestaurantHandler.getRestaurantDetails(restaurants, validOrder);

        // Assert
        assertNull("Restaurant should be null when pizza is not in any menu", restaurant);
    }
}