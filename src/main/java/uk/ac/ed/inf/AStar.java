package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class AStar {
    // Define the 8 possible directions of movement
    private static final double[] DIRS = {0.0, 45.0, 90, 135.0, 180.0, 225.0, 270.0, 315.0};

    // Global defined variables for the search
    static PriorityQueue<Node> openSet;     // frontier
    static HashSet<Node> closedSet;         // visited
    static List<Node> path;                 // resulting path

    // A* search algorithm
    public static boolean findShortestPath(NamedRegion[] noFlyZones, Node start, Node goal, NamedRegion Central) {
        long startTime = System.nanoTime();

        // Add start to the queue first
        openSet.add(start);

        // Once there is element in the queue, then keep running
        while (!openSet.isEmpty()) {

            // If the fail-safe checks do not work and program runs for more than 30 secs...
            if((System.nanoTime() - startTime)> 30000000000L){
                // ...this ensures that the program returns NO PATH for this route
                return false;
            }


            // Get the Node with the smallest cost
            Node current = openSet.poll();


            // Mark the Node to be visited
            closedSet.add(current);

            // Find the goal: early exit
            assert current != null;
            boolean close = new LngLatHandler().isCloseTo(current.coordinates,goal.coordinates);
            if (close) {

                // Reconstruct the path: trace by find the parent Node
                path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);

                return true;
            }

            // Search neighbors
            for (double dir : DIRS) {    //Loop through each direction of movement (N,NNE,NE,NEE,E,...,NW,NNW)
                LngLat nextCoords = new LngLatHandler().nextPosition(current.coordinates, dir);

                // Neighbour Node location
                Node next = new Node(nextCoords);

                // Check it is not going to enter a No-Fly zone
                boolean noFly = false;

                for (NamedRegion noFlyZone: noFlyZones){
                    if (new LngLatHandler().isInRegion(nextCoords,noFlyZone)){
                        noFly = true;
                    }
                }

                // Check if currently in central area, and, if so, that it does not leave central
                // As we find only the route Restaurant -> Appleton, this condition always applies
                if (new LngLatHandler().isInRegion(current.coordinates,Central)){
                    if (!(new LngLatHandler().isInRegion(nextCoords,Central))){
                        noFly = true;
                    }
                }

                // If we have a valid move
                if (!noFly  && !closedSet.contains(next)) {

                    // New movement is always 1 cost
                    double tentativeG = current.cost + SystemConstants.DRONE_MOVE_DISTANCE; //CHANGE TO DOUBLE?

                    // Find the Node if it is in the frontier but not visited to see if cost updating is needed
                    Node existing_neighbor = findNeighbor(nextCoords);

                    if(existing_neighbor != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(tentativeG < existing_neighbor.cost){
                            existing_neighbor.angle = dir;

                            // Update cost, parent information
                            existing_neighbor.parent = current;
                            existing_neighbor.cost = tentativeG;
                            existing_neighbor.estimate = 2*heuristic(existing_neighbor, goal);
                            existing_neighbor.total = existing_neighbor.cost + existing_neighbor.estimate;
                        }
                    }
                    else{
                        // Or directly add this Node to the frontier
                        Node neighbor = new Node(nextCoords);
                        neighbor.angle = dir;
                        neighbor.parent = current;
                        neighbor.cost = tentativeG;
                        neighbor.estimate = 2*heuristic(neighbor, goal);
                        neighbor.total = neighbor.cost + neighbor.estimate;

                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path found
        return false;
    }

    // Helper function to find and return the neighbor Node
    // Java priority queue cannot return a specific element
    public static Node findNeighbor(LngLat coords){
        if(openSet.isEmpty()){
            return null;
        }

        Iterator<Node> iterator = openSet.iterator();

        Node find = null;
        while (iterator.hasNext()) {
            Node next = iterator.next();
            if(next.coordinates.lng() == coords.lng() && next.coordinates.lat() == coords.lat()){
                find = next;
                break;
            }
        }
        return find;
    }

    public static double heuristic(Node a, Node b) {
        // A simple heuristic: Manhattan distance
        return new LngLatHandler().distanceTo(a.coordinates,b.coordinates);
    }
}