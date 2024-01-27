package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.geojson.*;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static uk.ac.ed.inf.LngLatHandler.HOVER;

/**
 * Class responsible for path finding and generating related data for drone deliveries.
 *
 *  @author B209981
 */
public class PathFinding {

    private static final LngLat appletonTower = new LngLat(-3.186874, 55.944494);

    private static final ArrayList<Restaurant> visitedRestaurants = new ArrayList<>();

    /**
     * Creates a list of flight moves for each order based on the provided paths.
     *
     * @param orders The list of orders.
     * @param paths The list of calculated path for each order.
     * @return A list of SingleMove objects representing the flight path for each order.
     */
    public static List<SingleMove> createFlightPaths(List<Order> orders, List<List<Node>> paths) {
        List<SingleMove> flightMoves = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            List<Node> path = paths.get(i);
            if (path != null) {
                addFlightMovesForPath(flightMoves, orders.get(i).getOrderNo(), path);
            }
        }
        return flightMoves;
    }

    /**
     * Adds flight moves for a single path to the overall list of flight moves.
     *
     * @param flightMoves The list of SingleMove objects to add to.
     * @param orderNo     The order number associated with the path.
     * @param path       The path to create flight moves for.
     */
    private static void addFlightMovesForPath(List<SingleMove> flightMoves, String orderNo, List<Node> path) {
        for (int j = 0; j < path.size() - 1; j++) {
            Node fromNode = path.get(j);
            Node toNode = path.get(j + 1);
            double angle = calculateAngle(fromNode, toNode);
            flightMoves.add(new SingleMove(orderNo, fromNode.coordinates.lng(), fromNode.coordinates.lat(), angle, toNode.coordinates.lng(), toNode.coordinates.lat()));
        }
        addHoverMove(flightMoves, orderNo, path.get(path.size() - 1));
    }

    /**
     * Calculates the angle (bearing) for movement from one node to another.
     *
     * @param fromNode The starting node.
     * @param toNode   The destination node.
     * @return The calculated angle in degrees.
     */
    static double calculateAngle(Node fromNode, Node toNode) {
        // Convert latitude and longitude from degrees to radians.
        double lat1 = Math.toRadians(fromNode.coordinates.lat());
        double lon1 = Math.toRadians(fromNode.coordinates.lng());
        double lat2 = Math.toRadians(toNode.coordinates.lat());
        double lon2 = Math.toRadians(toNode.coordinates.lng());

        // Calculate the change in coordinates.
        double dLon = lon2 - lon1;

        // Calculate the bearing.
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearingRadians = Math.atan2(y, x);

        // Convert bearing from radians to degrees and normalize it to 0-360.
        double bearingDegrees = Math.toDegrees(bearingRadians);
        bearingDegrees = (bearingDegrees + 360) % 360;

        return bearingDegrees;
    }

    /**
     * Adds a hover move at the end of a path.
     *
     * @param flightMoves The list of SingleMove objects to add to.
     * @param orderNo     The order number associated with the hover move.
     * @param lastNode    The last node in the path where the drone will hover.
     */
    private static void addHoverMove(List<SingleMove> flightMoves, String orderNo, Node lastNode) {
        flightMoves.add(new SingleMove(orderNo, lastNode.coordinates.lng(), lastNode.coordinates.lat(), HOVER, lastNode.coordinates.lng(), lastNode.coordinates.lat()));
    }

    /**
     * Identifies orders without valid paths.
     *
     * @param paths The list of calculated paths for each order.
     * @param orders The list of orders corresponding to the paths.
     * @return A list of orders that do not have valid paths.
     */
    public static List<Order> filterOrdersWithoutValidPaths(List<List<Node>> paths, List<Order> orders) {
        List<Order> ordersWithoutValidPaths = new ArrayList<>();

        for (int i = 0; i < paths.size(); i++) {
            if (isInvalidPath(paths.get(i))) {
                ordersWithoutValidPaths.add(orders.get(i));
            }
        }

        return ordersWithoutValidPaths;
    }

    /**
     * Checks if a path is invalid (null or empty).
     *
     * @param path The path to check.
     * @return true if the path is invalid, false otherwise.
     */
    private static boolean isInvalidPath(List<Node> path) {
        return path == null || path.isEmpty();
    }

    /**
     * Converts a list of drone paths into a GeoJSON string. Each path is represented by a list of nodes.
     * The method processes these nodes to create a continuous path for the drone, covering each path twice
     * (once in each direction) to simulate a round trip.
     *
     * @param paths List of paths for the drone, each path is a list of nodes.
     * @return String in GeoJSON format representing the drone's path.
     */
    public static String droneFile(List<List<Node>> paths){

        List<Point> dronePath = new ArrayList<>();

        for (List<Node> path : paths) {
            if (path != null) {

                // Reversing the path to simulate the forward path
                Collections.reverse(path);

                addPathToDroneFullPath(dronePath, path);
                // Reversing the path to simulate the return path
                Collections.reverse(path);
                addPathToDroneFullPath(dronePath, path);
            }
        }

        // Constructing a LineString geometry from the collected points
        Geometry lineString = LineString.fromLngLats(dronePath);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(lineString));

        return featureCollection.toJson();

    }

    /**
     * Adds the coordinates from a path to the overall drone path.
     *
     * @param dronePath List of points representing the drone path.
     * @param path     Individual path to add to the drone path.
     */
    private static void addPathToDroneFullPath(List<Point> dronePath, List<Node> path) {
        path.forEach(node -> dronePath.add(Point.fromLngLat(node.coordinates.lng(), node.coordinates.lat())));
    }

    /**
     * This method processes a list of orders, computes flight paths to visit restaurants,
     * creates flightpath JSON and drone geoJSON files, and returns orders without valid paths.
     *
     * @param orders   The list of orders to process.
     * @param visits   The list of restaurants to visit.
     * @param url  The base URL for relevant data.
     * @param date     The date for file naming.
     * @return         Orders without valid flight paths.
     */
    public static List<Order> processOrders(List<Order> orders, List<Restaurant> visits, String url, String date) {
        // Build flight paths to restaurants
        List<List<Node>> path = buildPathsForRestaurants(visits, url);

        // Find orders without valid paths
        List<Order> ordersValidNoPath = filterOrdersWithoutValidPaths(path, orders);

        // Define the flightpath JSON file name
        String flightFileName = "flightpath-" + date + ".json";

        // Create flight paths for each order
        List<SingleMove> flights = createFlightPaths(orders, path);

        // Write flightpath JSON to a file
        try (Writer writer = new FileWriter("resultfiles/" + flightFileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(flights, writer);
            System.out.println("[Info]: Flightpath file written.");
        } catch (IOException e) {
            System.err.println("[Error]: Unable to write flightpath file.");
        }

        // Define the drone geoJSON file name
        String droneFileName = "drone-" + date + ".geojson";

        // Generate drone geoJSON data
        String droneJSON = droneFile(path);

        // Write drone geoJSON to a file
        try (Writer writer = new FileWriter("resultfiles/" + droneFileName)) {
            writer.write(droneJSON);
            System.out.println("[Info]: Drone geoJSON file written.");
        } catch (IOException e) {
            System.err.println("[Error]: Unable to write drone geoJSON file.s");
        }
        return ordersValidNoPath;
    }

    /**
     * Iterates through a list of restaurants to visit and builds paths to each restaurant
     * based on the specified base URL. Returns a list of paths to all the restaurants.
     *
     * @param restaurantsToVisit The list of restaurants to visit.
     * @param url            The base URL used to build paths.
     * @return                   A list of paths to all the restaurants.
     */
    public static List<List<Node>> buildPathsForRestaurants(List<Restaurant> restaurantsToVisit, String url) {
        List<List<Node>> pathsToRestaurants = new ArrayList<>();

        for (Restaurant restaurant : restaurantsToVisit) {
            List<Node> pathToAdd = computePathToRestaurant(restaurant, pathsToRestaurants, url);
            pathsToRestaurants.add(pathToAdd);
        }

        return pathsToRestaurants;
    }

    /**
     * Adds a path to a restaurant to the list of paths, considering the restaurant's location
     * and using the A* algorithm to find the shortest path to the restaurant.
     *
     * @param restaurant  The restaurant to visit.
     * @param pathList    The list of paths to restaurants.
     * @param url     The base URL for relevant data.
     * @return            The path to the restaurant, or null if no path is found.
     */
    public static List<Node> computePathToRestaurant(Restaurant restaurant, List<List<Node>> pathList, String url) {
        if (visitedRestaurants.contains(restaurant)) {
            int index = visitedRestaurants.indexOf(restaurant);
            return pathList.get(index); // Return the previously computed path
        } else {
            LngLat restaurantLocation = restaurant.location();
            NamedRegion[] noFlyZones = new Client(url).noFlyZones();
            NamedRegion centralArea = new Client(url).centralArea();

            Node startNode = new Node(restaurantLocation);
            Node goalNode = new Node(appletonTower);

            AStar.openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.total));
            AStar.closedSet = new HashSet<>();

            if (!AStar.findShortestPath(noFlyZones, startNode, goalNode, centralArea)) {
                System.err.println("[Error]: No path found to: " + restaurant.name() + ".");
                visitedRestaurants.add(restaurant);
                return null;
            }

            visitedRestaurants.add(restaurant);
            return AStar.path;
        }
    }
}