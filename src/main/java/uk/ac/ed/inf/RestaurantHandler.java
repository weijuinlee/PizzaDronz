package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import java.util.Arrays;

public class RestaurantHandler {

    public static Restaurant getRestaurantDetails(Restaurant[] restaurants, Order validOrder) {

        for (Restaurant restaurantDetails : restaurants) {
            if (Arrays.asList(restaurantDetails.menu()).contains(validOrder.getPizzasInOrder()[0])) {
                return restaurantDetails;
            }
        }
        return null;
    }

}
