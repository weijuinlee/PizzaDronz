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

// A class that matches the JSON format for the spec
class flightpathMove {
    private final String orderNo;
    private final double fromLongitude;
    private final double fromLatitude;
    private final double angle;
    private final double toLongitude;
    private final double toLatitude;


    public flightpathMove(String orderNo, double fromLongitude, double fromLatitude, double angle, double toLongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
        this.angle = angle;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
    }
}


public class pathGEO {
    public static LngLat apple = new LngLat(-3.186874, 55.944494); //SPECIFIED IN SPEC
    public static ArrayList<Restaurant> visitedList = new ArrayList<>();

    public static List<flightpathMove> flightFile(List<Order> orders, List<List<Node>> route){
        // Make use of the list of paths of restaurant to Appleton
        List<flightpathMove> toReturn = new ArrayList<>();
        final int[] count = {-1};

        // Loop through each path in the list
        route.forEach(path -> {
            if (path != null){
                count[0]++;
                final int[] iter1 = {0};
                final int[] iter2 = {0};
                // Reverse the path (so that it starts by going Appleton -> restaurant)
                Collections.reverse(path);
                path.forEach(Node -> {
                    try {
                        // For each Node in the path, create a new flightPathMove
                        toReturn.add(new flightpathMove(orders.get(count[0]).getOrderNo(),Node.coordinates.lng(),Node.coordinates.lat(), ((180-Node.angle)%360), path.get(iter1[0] +1).coordinates.lng(),path.get(iter1[0] +1).coordinates.lat()));
                        // The angles are reversed as we initially reverse the path
                        iter1[0]++;
                    } catch (Exception e){
                        // The path has reached the restaurant
                    }
                });
                // Add in the hover step
                toReturn.add(new flightpathMove(orders.get(count[0]).getOrderNo(),path.get(path.size()-1).coordinates.lng(),path.get(path.size()-1).coordinates.lng(),999,path.get(path.size()-1).coordinates.lng(),path.get(path.size()-1).coordinates.lng()));
                // Reverse the path again (so that it now goes restaurant -> Appleton)
                Collections.reverse(path);
                path.forEach(Node -> {
                    try {
                        toReturn.add(new flightpathMove(orders.get(count[0]).getOrderNo(),Node.coordinates.lng(),Node.coordinates.lat(), Node.angle, path.get(iter2[0] +1).coordinates.lng(),path.get(iter2[0] +1).coordinates.lat()));
                        // The angles are back to normal as we are using the initial path
                        iter2[0]++;
                    } catch (Exception e){
                        //The path has reached Appleton
                    }
                });
                // Add in another hover step
                toReturn.add(new flightpathMove(orders.get(count[0]).getOrderNo(),path.get(path.size()-1).coordinates.lng(),path.get(path.size()-1).coordinates.lng(),999,path.get(path.size()-1).coordinates.lng(),path.get(path.size()-1).coordinates.lng()));
            }
        });
        return toReturn;
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
                e.forEach(Node -> dronePath.add(Point.fromLngLat(Node.coordinates.lng(), Node.coordinates.lat())));

                // Reverse the path again (so that it now goes restaurant -> Appleton)
                Collections.reverse(e);

                // And add each coordinate point again
                e.forEach(Node -> dronePath.add(Point.fromLngLat(Node.coordinates.lng(), Node.coordinates.lat())));
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

    // A helper function that checks for any empty paths (unable to reach) and add to necessary list of bad orders
    public static List<Order> ordersValidNoPath(List<List<Node>> route, List<Order> orderNums){
        List<Order> ordersValidNoPath = new ArrayList<>();

        //For each path in the route, if it is empty, append corresponding order
        for (int i = 0; i < route.size(); i++) {
            if (Objects.equals(route.get(i), null)) {
                ordersValidNoPath.add(orderNums.get(i));
            }
        }

        return ordersValidNoPath;
    }

    public static List<Order> main(List<Order> Orders, List<Restaurant> visits, String BASEURL, String date) {
        List<List<Node>> route = iterat(visits, BASEURL);

        // Any empty routes will need validation changing so append these to a list for processing in App
        List<Order> ordersValidNoPath = ordersValidNoPath(route,Orders);

        //Directory will already exist from createDir

        // Define the path for the new JSON file
        String flightFileName = "flightpath-"+date+".json";

        // For each order, create a new flightpathMove instance to be added to the JSON
        List<flightpathMove> flights = flightFile(Orders,route);

        // Build the new JSON file using the flightpathMove class and write to relevant file
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
            goal = new Node(apple);

            // Run A* algorithm to find the shortest path
            new AStar();
            AStar.openSet = new PriorityQueue<Node>(Comparator.comparingDouble(c -> c.total));
            AStar.closedSet = new HashSet<>();

            if (!AStar.findShortestPath(NoFlyZones, start, goal, Central)) {
                System.err.println("No path found to: " + restrnt.name());
                visitedList.add(restrnt);
                return null;
            }

            // Update the cache array of visited restaurants
            visitedList.add(restrnt);

            return AStar.path;
        }
    }
}