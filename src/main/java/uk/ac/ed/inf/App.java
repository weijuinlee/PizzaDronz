package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the pizzaDronz, this is the entry point of the application.
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println("[Info]: Main Application started.");
        if (!argsValidator(args)) {
            return;
        }

        String date = args[0];
        String url = args[1];

        if (!url.endsWith("/")) {
            url += "/";
        }

        boolean clientIsAlive = new Client(url).isAlive();

        if (clientIsAlive){
            Client client = new Client(url);

            // Get responses from REST service.
            System.out.println("[Info]: Accessing ILP REST Service.");

            Order[] orderList = client.orders(date);
            Restaurant[] restaurantList = client.restaurants();

            if (orderList != null && restaurantList != null) {

                List<Order> validOrderList = new ArrayList<>();
                List<Restaurant> restaurantDetailsList = new ArrayList<>();
                List<Order> updatedOrderList = new ArrayList<>();

                NamedRegion[] noFlyZone = client.noFlyZones();

                if (orderList.length > 0) {

                    FileHandler.resultFiles();

                    for (Order order : orderList) {

                        Order validatedOrder = new OrderValidator().validateOrder(order, restaurantList);

                        if (validatedOrder != null) {

                            if (validatedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                                validOrderList.add(order);
                                restaurantDetailsList.add(RestaurantHandler.getRestaurantDetails(restaurantList, validatedOrder));
                            }
                            updatedOrderList.add(validatedOrder);
                        }
                    }

                    // Create Flightpath and Drone files from only the valid orders and corresponding restaurants
                    List<Order> ordersValidNoPath = pathGEO.main(validOrderList, restaurantDetailsList, url, date);


                    // Take any orders that are in the list of orders with no paths and update their validity status
                    for (Order valid:ordersValidNoPath) {
                        valid.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
                    }

                    // Create the Order JSON file from list of all validated orders
                    Delivery.writeToFile(updatedOrderList, date);

                } else {
                    System.out.println("[Info]: No orders for selected date.");
                    FileHandler.resultFiles();
                    Delivery.writeToFile(updatedOrderList, date);
                    pathGEO.main(validOrderList, restaurantDetailsList, url, date);
                }
            }
        }
    }

    /**
     * Validates the cli input arguments that comprises a valid date in YYYY-MM-DD, an url address and any word
     * @param args cli input arguments
     * @return true if arguments are valid and false if invalid
     */
    public static boolean argsValidator(String[] args) {

        if (args == null) {
            System.err.println("[Error]: Invalid input of null, please input date, url and any word to initialise the " +
                    "random-number generator.");
            return false;
        } else if (args.length != 3) {
            System.err.println("[Error]: Invalid number of inputs in arguments, please input date, url and any word to initialise the " +
                    "random-number generator.");
            return false;
        }
        // parsing arguments into date and url
        String date = args[0];
        String url = args[1];

        try {
            // Verify the date format
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, format);

        } catch (DateTimeParseException e) {
            System.err.println("[Error]: Please provide a valid date in YYYY-MM-DD format.");
            return false;
        }
        try {
            // Verify if url malformed and has wrong URI syntax
            URL obj = new URL(url);
            obj.toURI();
            return true;
        } catch (MalformedURLException e) {
            System.err.println("[Error]: Please provide a URL not malformed.");
            return false;
        } catch (URISyntaxException e) {
            System.err.println("[Error]: Please provide a URL with a valid URI Syntax.");
            return false;
        }
    }


//    public static Restaurant getRestrnt(Restaurant[] restrnts,Order validOrder){
//        // This looks at the first pizza in a valid order and returns its restaurant of origin
//        for (Restaurant definedRestaurant : restrnts) {
//            if (Arrays.asList(definedRestaurant.menu()).contains(validOrder.getPizzasInOrder()[0])) {
//                return definedRestaurant;
//            }
//        }
//        return null; //this would never be reached as only valid pizzas are passed to this function
//    }

}
