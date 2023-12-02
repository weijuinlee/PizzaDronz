package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

// Cell class to help record status of coordinates and facing angle
// Since we are using priority queue and hashset, implement some functions, e.g., hashCode, equals are needed

class Cell {
    LngLat coords;   // The position in LngLat coordinate form
    double f, g, h;    // A* algorithm heuristic value parameters
    Cell parent;    // Parent record: come from
    double angle; // Angle to next node

    public Cell(LngLat coords) {
        this.coords = coords;
        parent = null;
        f = 0;
        g = 0;
        h = 0;
    }

    @Override
    public int hashCode(){
        return Objects.hash(coords);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        Cell other = (Cell)obj;
        return other.coords == coords;
    }

}

public class AStar {
    // Define the 16 possible directions of movement
    private static final double[] DIRS = {0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5}; // REPLACE WITH 16 POSSIBLE NEXT MOVES FROM LAT-LNG-HANDLER

    // Global defined variables for the search
    static PriorityQueue<Cell> openSet;     // frontier
    static HashSet<Cell> closedSet;         // visited
    static List<Cell> path;                 // resulting path

    // A* search algorithm
    public static boolean findShortestPath(NamedRegion[] noFlyZones, Cell start, Cell goal, NamedRegion Central) {
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


            // Get the cell with the smallest cost
            Cell current = openSet.poll();


            // Mark the cell to be visited
            closedSet.add(current);

            // Find the goal: early exit
            assert current != null;
            boolean close = new LngLatHandler().isCloseTo(current.coords,goal.coords);
            if (close) {

                // Reconstruct the path: trace by find the parent cell
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
                LngLat nextCoords = new LngLatHandler().nextPosition(current.coords, dir);

                // Neighbour cell location
                Cell next = new Cell(nextCoords);

                // Check it is not going to enter a No-Fly zone
                boolean noFly = false;

                for (NamedRegion noFlyZone: noFlyZones){
                    if (new LngLatHandler().isInRegion(nextCoords,noFlyZone)){
                        noFly = true;
                    }
                }

                // Check if currently in central area, and, if so, that it does not leave central
                // As we find only the route Restaurant -> Appleton, this condition always applies
                if (new LngLatHandler().isInRegion(current.coords,Central)){
                    if (!(new LngLatHandler().isInRegion(nextCoords,Central))){
                        noFly = true;
                    }
                }

                // If we have a valid move
                if (!noFly  && !closedSet.contains(next)) {

                    // New movement is always 1 cost
                    double tentativeG = current.g + SystemConstants.DRONE_MOVE_DISTANCE; //CHANGE TO DOUBLE?

                    // Find the cell if it is in the frontier but not visited to see if cost updating is needed
                    Cell existing_neighbor = findNeighbor(nextCoords);

                    if(existing_neighbor != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(tentativeG < existing_neighbor.g){
                            existing_neighbor.angle = dir;

                            // Update cost, parent information
                            existing_neighbor.parent = current;
                            existing_neighbor.g = tentativeG;
                            existing_neighbor.h = 2*heuristic(existing_neighbor, goal);
                            existing_neighbor.f = existing_neighbor.g + existing_neighbor.h;
                        }
                    }
                    else{
                        // Or directly add this cell to the frontier
                        Cell neighbor = new Cell(nextCoords);
                        neighbor.angle = dir;
                        neighbor.parent = current;
                        neighbor.g = tentativeG;
                        neighbor.h = 2*heuristic(neighbor, goal);
                        neighbor.f = neighbor.g + neighbor.h;

                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path found
        return false;
    }

    // Helper function to find and return the neighbor cell
    // Java priority queue cannot return a specific element
    public static Cell findNeighbor(LngLat coords){
        if(openSet.isEmpty()){
            return null;
        }

        Iterator<Cell> iterator = openSet.iterator();

        Cell find = null;
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            if(next.coords.lng() == coords.lng() && next.coords.lat() == coords.lat()){
                find = next;
                break;
            }
        }
        return find;
    }

    public static double heuristic(Cell a, Cell b) {
        // A simple heuristic: Manhattan distance
        return new LngLatHandler().distanceTo(a.coords,b.coords);
    }
}