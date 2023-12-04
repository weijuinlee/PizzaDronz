package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;


/**
 * Perform pathfinding using AStar Algorithm
 *
 * @author B209981
 */
public class AStar {
    // Direction angles in degrees
    private static final double[] DIRECTIONS = {0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0};

    // Open set for the nodes to be evaluated
    static PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.total));
    // Closed set for the nodes already evaluated
    static HashSet<Node> closedSet = new HashSet<>();
    // Path found by the algorithm
    static List<Node> path;

    /**
     * Performs A* search to find the shortest path avoiding no-fly zones and staying within the central area.
     *
     * @param noFlyZones Array of no-fly zones
     * @param start Start node
     * @param goal Goal node
     * @param central Central named region
     * @return true if a path is found, false otherwise
     */
    public static boolean findShortestPath(NamedRegion[] noFlyZones, Node start, Node goal, NamedRegion central) {
        long startTime = System.nanoTime();
        openSet.add(start);

        while (!openSet.isEmpty()) {
            if ((System.nanoTime() - startTime) > 30_000_000_000L) {
                // Timeout after 30 seconds
                return false;
            }

            Node current = openSet.poll();
            closedSet.add(current);

            // Check if the goal is reached
            if (current != null) {
                if (!isCloseToGoal(current, goal)) {
                    exploreNeighbors(current, noFlyZones, goal, central);
                } else {
                    reconstructPath(current);
                    return true;
                }
            }

        }

        return false; // No path found
    }

    // Checks if the current node is close to the goal
    private static boolean isCloseToGoal(Node current, Node goal) {
        return new LngLatHandler().isCloseTo(current.coordinates, goal.coordinates);
    }

    // Reconstructs the path from the goal to the start
    private static void reconstructPath(Node current) {
        path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
    }

    // Explores neighbors of the current node, updating or adding them to the open set
    private static void exploreNeighbors(Node current, NamedRegion[] noFlyZones, Node goal, NamedRegion central) {
        for (double direction : DIRECTIONS) {
            LngLat nextCoords = new LngLatHandler().nextPosition(current.coordinates, direction);
            Node next = new Node(nextCoords);

            if (isValidMove(nextCoords, noFlyZones, current, central)) {
                processNeighbor(current, next, direction, goal);
            }
        }
    }

    // Checks if moving to the next coordinates is valid (not entering no-fly zones and stays in central if required)
    private static boolean isValidMove(LngLat nextCoords, NamedRegion[] noFlyZones, Node current, NamedRegion central) {
        for (NamedRegion noFlyZone : noFlyZones) {
            if (new LngLatHandler().isInRegion(nextCoords, noFlyZone)) {
                return false;
            }
        }

        return !new LngLatHandler().isInRegion(current.coordinates, central) || new LngLatHandler().isInRegion(nextCoords, central);
    }

    // Processes a neighbor node during search
    private static void processNeighbor(Node current, Node neighbor, double direction, Node goal) {
        double tentativeG = current.cost + SystemConstants.DRONE_MOVE_DISTANCE;
        Node existingNeighbor = findNeighbor(neighbor.coordinates);

        if (existingNeighbor != null && tentativeG < existingNeighbor.cost) {
            updateNeighbor(existingNeighbor, current, tentativeG, direction, goal);
        } else if (existingNeighbor == null) {
            addNeighborToOpenSet(neighbor, current, tentativeG, direction, goal);
        }
    }

    // Updates an existing neighbor in the open set
    private static void updateNeighbor(Node neighbor, Node current, double newCost, double direction, Node goal) {
        neighbor.parent = current;
        neighbor.cost = newCost;
        neighbor.angle = direction;
        updateEstimates(neighbor, goal);
    }

    // Adds a new neighbor to the open set
    private static void addNeighborToOpenSet(Node neighbor, Node current, double cost, double direction, Node goal) {
        neighbor.parent = current;
        neighbor.cost = cost;
        neighbor.angle = direction;
        updateEstimates(neighbor, goal);
        openSet.add(neighbor);
    }

    // Updates cost estimates for a node
    private static void updateEstimates(Node node, Node goal) {
        node.estimate = 2 * heuristic(node, goal);
        node.total = node.cost + node.estimate;
    }

    // Finds a neighbor node in the open set based on coordinates
    private static Node findNeighbor(LngLat coords) {
        return openSet.stream().filter(n -> n.coordinates.equals(coords)).findFirst().orElse(null);
    }

    // Heuristic function (Manhattan distance)
    private static double heuristic(Node a, Node b) {
        return new LngLatHandler().distanceTo(a.coordinates, b.coordinates);
    }
}
