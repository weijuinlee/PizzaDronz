package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import java.util.Objects;

/**
 * Represents a node in the A* pathfinding algorithm.
 * Each node corresponds to a specific location and contains data used by the A* algorithm.
 *
 * @author B209981
 */
class Node {
    // Coordinates of the node in longitude and latitude.
    LngLat coordinates;

    // A* algorithm specific parameters.
    // total = total cost of the node (cost + estimate)
    // cost = cost from the start node to this node
    // estimate = estimated cost from this node to the end node (heuristic)
    double total, cost, estimate;

    // Parent node in the path, used to reconstruct the path once the algorithm finishes.
    Node parent;

    // Angle to the next node in the path, relevant in scenarios like drone navigation.
    double angle;

    /**
     * Constructor for Node.
     * Initializes the node with its coordinates.
     *
     * @param coordinates The coordinates (longitude and latitude) of the node.
     */
    public Node(LngLat coordinates) {
        this.coordinates = coordinates;
        this.parent = null;
        this.total = 0;
        this.cost = 0;
        this.estimate = 0;
    }

    /**
     * Generates a hash code for this node.
     * The hash code is based on the coordinates of the node.
     *
     * @return The hash code of the node.
     */
    @Override
    public int hashCode() {
        return Objects.hash(coordinates);
    }

    /**
     * Compares this node to another object for equality.
     * Two nodes are considered equal if their coordinates are the same.
     *
     * @param object The object to compare this node against.
     * @return true if the given object represents a Node equivalent to this node, false otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // Check for identity comparison - if they are the same instance, they are definitely equal.
        if (this == object) {
            return true;
        }

        // Check for null and compare classes - if 'obj' is null or belongs to a different class, they are not equal.
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        // Cast 'obj' to a Node - since we've checked the class above, this is safe.
        Node other = (Node) object;

        // Compare the relevant fields for equality.
        // Use Objects.equals to handle potential nulls in coordinates.
        return Objects.equals(coordinates, other.coordinates);
    }
}
