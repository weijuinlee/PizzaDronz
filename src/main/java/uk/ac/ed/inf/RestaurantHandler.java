package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import java.util.Arrays;

/**
 * Handler for restaurant-related operations.
 * @author B209981
 *
 */
public class RestaurantHandler {

    /**
     * Retrieves the details of the restaurant associated with a valid order.
     * It checks which restaurant offers the first pizza in the order.
     *
     * @param restaurants Array of available restaurants.
     * @param validOrder The order for which to find the corresponding restaurant.
     * @return The restaurant that offers the first pizza in the order, or null if not found.
     */
    public static Restaurant getRestaurantDetails(Restaurant[] restaurants, Order validOrder) {

        for (Restaurant restaurantDetails : restaurants) {
            if (Arrays.asList(restaurantDetails.menu()).contains(validOrder.getPizzasInOrder()[0])) {
                return restaurantDetails;
            }
        }
        return null;
    }

}


