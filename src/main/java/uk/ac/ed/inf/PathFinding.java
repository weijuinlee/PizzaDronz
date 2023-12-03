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
    private static double calculateAngle(Node fromNode, Node toNode) {
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
        flightMoves.add(new SingleMove(orderNo, lastNode.coordinates.lng(), lastNode.coordinates.lat(), 999, lastNode.coordinates.lng(), lastNode.coordinates.lat()));
    }

    /**
     * Identifies orders without valid paths.
     *
     * @param routes The list of calculated routes for each order.
     * @param orders The list of orders corresponding to the routes.
     * @return A list of orders that do not have valid paths.
     */
    public static List<Order> filterOrdersWithoutValidPaths(List<List<Node>> routes, List<Order> orders) {
        List<Order> ordersWithoutValidPaths = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++) {
            if (isInvalidRoute(routes.get(i))) {
                ordersWithoutValidPaths.add(orders.get(i));
            }
        }

        return ordersWithoutValidPaths;
    }

    /**
     * Checks if a route is invalid (null or empty).
     *
     * @param route The route to check.
     * @return true if the route is invalid, false otherwise.
     */
    private static boolean isInvalidRoute(List<Node> route) {
        return route == null || route.isEmpty();
    }

    public static String droneFile(List<List<Node>> route){
        // Make use of the list of paths of restaurant to Appleton
        List<Point> dronePath = new ArrayList<>();
        // Loop through each path in the list
        route.forEach(e -> {
            if (e != null){
                // Reverse the path (so that it starts by going Appleton -> restaurant)
                Collections.reverse(e);

                // Add a point to the list storing all points in order
                e.forEach(cell -> dronePath.add(Point.fromLngLat(cell.coordinates.lng(), cell.coordinates.lat())));

                // Reverse the path again (so that it now goes restaurant -> Appleton)
                Collections.reverse(e);

                // And add each coordinate point again
                e.forEach(cell -> dronePath.add(Point.fromLngLat(cell.coordinates.lng(), cell.coordinates.lat())));
            }
        });

        // Create a LineString feature from the points
        Geometry geometry = LineString.fromLngLats(dronePath);
        Feature feature = Feature.fromGeometry(geometry);

        // Add it to its own Feature collection
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);

        // Return a string in a geoJSON format
        return featureCollection.toJson();
    }

    public static List<Order> main(List<Order> Orders, List<Restaurant> visits, String BASEURL, String date) {
        List<List<Node>> route = iterat(visits, BASEURL);

        // Any empty routes will need validation changing so append these to a list for processing in App
        List<Order> ordersValidNoPath = filterOrdersWithoutValidPaths(route,Orders);

        //Directory will already exist from createDir

        // Define the path for the new JSON file
        String flightFileName = "flightpath-"+date+".json";

        // For each order, create a new SingleMove instance to be added to the JSON
        List<SingleMove> flights = createFlightPaths(Orders,route);

        // Build the new JSON file using the SingleMove class and write to relevant file
        try (Writer writer = new FileWriter("resultfiles/"+flightFileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(flights, writer);
            System.out.println("Flightpath file written");
        } catch (IOException e) {
            System.err.println("Unable to write flight");
        }

        // Define the path for the new JSON file
        String droneFileName = "drone-"+date+".geojson";

        // Run droneFile to get a geoJSON string of the relevant feature collection
        String droneJSON = droneFile(route);

        // Write the new geoJSON file to relevant file
        try (Writer writer = new FileWriter("resultfiles/"+droneFileName)) {
            writer.write(droneJSON);
            System.out.println("Drone file written");
        } catch (IOException e) {
            System.err.println("Unable to write drone");
        }

        return ordersValidNoPath;
    }

    public static List<List<Node>> iterat(List<Restaurant> visits,String BASEURL){
        List<List<Node>> toGoTo = new ArrayList<>();
        // Cycle through the restaurant for every order
        for (Restaurant restToGo:visits) {
            // Add the path to the list of paths
            toGoTo.add(addToPath(restToGo,toGoTo,BASEURL));
        }
        return toGoTo;
    }

    public static List<Node> addToPath(Restaurant restrnt,List<List<Node>> toGoTo,String BASEURL) {
        // Check if restaurant has already been put through A* algorithm
        if (visitedRestaurants.contains(restrnt)){
            // If it has, find the first occurrence in the list of paths and return
            int index = visitedRestaurants.indexOf(restrnt);
            visitedRestaurants.add(restrnt);
            return toGoTo.get(index);
        } else {

            // Define restaurant location and instantiate other variables
            LngLat restLoc = restrnt.location();
            NamedRegion[] NoFlyZones = new Client(BASEURL).noFlyZones();
            NamedRegion Central = new Client(BASEURL).centralArea();

            // Find the start and goal positions
            Node start, goal;
            start = new Node(restLoc);
            goal = new Node(appletonTower);

            // Run A* algorithm to find the shortest path
            new AStar();
            AStar.openSet = new PriorityQueue<Node>(Comparator.comparingDouble(c -> c.total));
            AStar.closedSet = new HashSet<>();

            if (!AStar.findShortestPath(NoFlyZones, start, goal, Central)) {
                System.err.println("No path found to: " + restrnt.name());
                visitedRestaurants.add(restrnt);
                return null;
            }

            // Update the cache array of visited restaurants
            visitedRestaurants.add(restrnt);

            return AStar.path;
        }
    }
}