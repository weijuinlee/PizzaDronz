package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
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
 * Main class for the pizzaDronz application, serving as the entry point.
 *
 * @author B209981
 */
public class App {
    public static void main(String[] args) {
        System.out.println("[Info]: Main Application started.");

        // Validate command-line arguments
        if (!validateCommandLineArgs(args)) {
            return;
        }

        String date = args[0];
        String url = args[1];

        // Ensure URL ends with a trailing slash
        if (!url.endsWith("/")) {
            url += "/";
        }

        boolean clientIsAlive = new Client(url).isAlive();

        if (clientIsAlive) {
            Client client = new Client(url);

            // Get responses from REST service.
            System.out.println("[Info]: Accessing ILP REST Service.");

            Order[] orderList = client.orders(date);
            Restaurant[] restaurantList = client.restaurants();

            if (orderList != null && restaurantList != null) {

                List<Order> validOrderList = new ArrayList<>();
                List<Restaurant> restaurantDetailsList = new ArrayList<>();
                List<Order> updatedOrderList = new ArrayList<>();

                if (orderList.length > 0) {

                    FileHandler.resultFiles();

                    for (Order order : orderList) {

                        Order validatedOrder = new OrderValidator().validateOrder(order, restaurantList);

                        if (validatedOrder != null) {

                            if (validatedOrder.getOrderStatus() == OrderStatus.DELIVERED) {
                                validOrderList.add(order);
                                restaurantDetailsList.add(RestaurantHandler.getRestaurantDetails(restaurantList, validatedOrder));
                            }
                            updatedOrderList.add(validatedOrder);
                        }
                    }

                    // Create Flightpath and Drone files from only the valid orders and corresponding restaurants
                    List<Order> ordersValidNoPath = PathFinding.processOrders(validOrderList, restaurantDetailsList, url, date);
                    // Update the validity status of orders with no paths
                    if (ordersValidNoPath.size() > 0) {
                        for (Order valid : ordersValidNoPath) {
                            valid.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
                        }
                    }
                    // Create the Order JSON file from the list of all validated orders
                    Delivery.writeToFile(updatedOrderList, date);

                } else {
                    System.out.println("[Info]: No orders for the selected date.");
                    FileHandler.resultFiles();
                    Delivery.writeToFile(updatedOrderList, date);
                    PathFinding.processOrders(validOrderList, restaurantDetailsList, url, date);
                }
            }
        }
    }

    /**
     * Validates the command-line input arguments, including a valid date in YYYY-MM-DD format, a URL address,
     * and any word to initialize the random-number generator.
     *
     * @param args Command-line input arguments.
     * @return True if arguments are valid, false if invalid.
     */
    public static boolean validateCommandLineArgs(String[] args) {

        if (args == null) {
            System.err.println("[Error]: Invalid input of null. Please input date, URL, and any word to initialize the " +
                    "random-number generator.");
            return false;
        } else if (args.length != 3) {
            System.err.println("[Error]: Invalid number of inputs in arguments. Please input date, URL, and any word to initialize the " +
                    "random-number generator.");
            return false;
        }

        // Parsing arguments into date and URL
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
            // Verify if URL is malformed and has the correct URI syntax
            URL obj = new URL(url);
            obj.toURI();
            return true;
        } catch (MalformedURLException e) {
            System.err.println("[Error]: Please provide a valid URL.");
            return false;
        } catch (URISyntaxException e) {
            System.err.println("[Error]: Please provide a URL with a valid URI Syntax.");
            return false;
        }
    }
}
