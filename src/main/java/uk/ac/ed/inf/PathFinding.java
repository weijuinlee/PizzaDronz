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

public class PathFinding {

    private static final LngLat appletonTower = new LngLat(-3.186874, 55.944494);
    public static ArrayList<Restaurant> visitedList = new ArrayList<>();

    /**
     * Generates a GeoJSON string representing the drone flight path.
     *
     * @param paths List of paths, where each path is a list of nodes.
     * @return GeoJSON string representation of the drone flight path.
     */
    public static String createDronePathGeoJSON(List<List<Node>> paths) {
        List<Point> dronePathNodes = new ArrayList<>();

        // Process each route to build the drone path
        paths.forEach(path -> {
            if (path != null) {
                // Process the route in both directions: Appleton -> Restaurant and back
                addPathNodes(dronePathNodes, path);
                Collections.reverse(path); // Reverse for return trip
                addPathNodes(dronePathNodes, path);
            }
        });

        // Create a LineString feature from the collected points
        Geometry lineString = LineString.fromLngLats(dronePathNodes);
        Feature pathFeature = Feature.fromGeometry(lineString);

        // Create a FeatureCollection containing the path feature
        FeatureCollection pathFeatureCollection = FeatureCollection.fromFeature(pathFeature);

        // Convert the FeatureCollection to GeoJSON format
        return pathFeatureCollection.toJson();
    }

    /**
     * Adds points from a route to the drone path points list.
     *
     * @param dronePathNodes List of points representing the drone path.
     * @param route           The route to process.
     */
    private static void addPathNodes(List<Point> dronePathNodes, List<Node> route) {
        route.forEach(node -> {
            Point point = Point.fromLngLat(node.coordinates.lng(), node.coordinates.lat());
            dronePathNodes.add(point);
        });
    }

    /**
     * Identifies orders that do not have a valid path.
     *
     * @param paths    List of calculated paths for each order.
     * @param orders    List of orders corresponding to the paths.
     * @return List of orders for which no valid path exists.
     */
    public static List<Order> filterOrdersWithoutPath(List<List<Node>> paths, List<Order> orders) {
        List<Order> ordersWithoutPath = new ArrayList<>();

        for (int i = 0; i < paths.size(); i++) {
            List<Node> path = paths.get(i);

            // Check if the path is null, indicating no valid path
            if (path == null) {
                ordersWithoutPath.add(orders.get(i));
            }
        }

        return ordersWithoutPath;
    }

    /**
     * Creates a list of SingleMove objects representing the flight path based on paths.
     *
     * @param orders The list of orders.
     * @param paths The list of paths for each order.
     * @return List of SingleMove objects representing the flight path.
     */
    public static List<SingleMove> createFlightPath(List<Order> orders, List<List<Node>> paths) {
        List<SingleMove> flightMoves = new ArrayList<>();

        for (int orderIndex = 0; orderIndex < paths.size(); orderIndex++) {
            List<Node> path = paths.get(orderIndex);
            if (path != null) {
                addPathMoves(flightMoves, path, orders.get(orderIndex).getOrderNo(), false); // Appleton to Restaurant
                addPathMoves(flightMoves, path, orders.get(orderIndex).getOrderNo(), true);  // Restaurant to Appleton
            }
        }
        return flightMoves;
    }

    /**
     * Adds moves to the flight path based on the provided nodes in a path.
     *
     * @param flightMoves List of SingleMove objects to add to.
     * @param path The path containing nodes.
     * @param orderNo The order number associated with the path.
     * @param reversePath Indicates whether the path should be processed in reverse.
     */
    private static void addPathMoves(List<SingleMove> flightMoves, List<Node> path, String orderNo, boolean reversePath) {
        if (reversePath) {
            Collections.reverse(path);
        }

        for (int i = 0; i < path.size() - 1; i++) {
            Node currentNode = path.get(i);
            Node nextNode = path.get(i + 1);
            double angle = reversePath ? (180 - currentNode.angle) % 360 : currentNode.angle;
            flightMoves.add(new SingleMove(orderNo, currentNode.coordinates.lng(), currentNode.coordinates.lat(), angle, nextNode.coordinates.lng(), nextNode.coordinates.lat()));
        }

        // Add hover move at the end of the path
        Node lastNode = path.get(path.size() - 1);
        flightMoves.add(new SingleMove(orderNo, lastNode.coordinates.lng(), lastNode.coordinates.lat(), 999, lastNode.coordinates.lng(), lastNode.coordinates.lat()));

        if (reversePath) {
            Collections.reverse(path); // Revert the path to its original order
        }
    }

    public static List<Order> processOrders(List<Order> Orders, List<Restaurant> visits, String BASEURL, String date) {
        List<List<Node>> route = iterat(visits, BASEURL);

        // Any empty routes will need validation changing so append these to a list for processing in App
        List<Order> ordersValidNoPath = filterOrdersWithoutPath(route,Orders);

        //Directory will already exist from createDir

        // Define the path for the new JSON file
        String flightFileName = "flightpath-"+date+".json";

        // For each order, create a new SingleMove instance to be added to the JSON
        List<SingleMove> flights = createFlightPath(Orders,route);

        // Build the new JSON file using the SingleMove class and write to relevant file
        try (Writer writer = new FileWriter("resultfiles/"+flightFileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(flights, writer);
            System.out.println("[Info]: Flightpath file written.");
        } catch (IOException e) {
            System.err.println("[Error]: Unable to write flight.");
        }

        // Define the path for the new JSON file
        String droneFileName = "drone-"+date+".geojson";

        // Run droneFile to get a geoJSON string of the relevant feature collection
        String droneJSON = createDronePathGeoJSON(route);

        // Write the new geoJSON file to relevant file
        try (Writer writer = new FileWriter("resultfiles/"+droneFileName)) {
            writer.write(droneJSON);
            System.out.println("[Info]: Drone file written.");
        } catch (IOException e) {
            System.err.println("[Error]: Unable to write drone.");
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
        if (visitedList.contains(restrnt)){
            // If it has, find the first occurrence in the list of paths and return
            int index = visitedList.indexOf(restrnt);
            visitedList.add(restrnt);
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
                System.err.println("[Info]: No path found to: " + restrnt.name() + ".");
                visitedList.add(restrnt);
                return null;
            }

            // Update the cache array of visited restaurants
            visitedList.add(restrnt);

            return AStar.path;
        }
    }
}