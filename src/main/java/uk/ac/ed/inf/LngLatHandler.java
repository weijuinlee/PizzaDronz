package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.Arrays;
import java.util.Map;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.*;

/**
 * implement the needed computations for a LngLatHandling
 * @author B209981
 */
public class LngLatHandler implements LngLatHandling {

    //constants for angles
    static final int MIN_ANGLE = 0;
    static final int MAX_ANGLE = 360;
    static final int HOVER = 999;
    private final double f = Double.MAX_VALUE;
    private final double g = Double.MAX_VALUE;
    private double lng;
    private double lat;

    /**
     * get the distance between two positions
     * @param startPosition is where the start is
     * @param endPosition is where the end is
     * Return the euclidean distance between the positions
     */
    public double distanceTo(LngLat startPosition, LngLat endPosition){

        //Finds the pythagorean distance between start and end positions
        return Math.hypot(startPosition.lng() - endPosition.lng(),  startPosition.lat() - endPosition.lat());
    }

    /**
     * check if two positions are close (< than SystemConstants.DRONE_IS_CLOSE_DISTANCE)
     * @param startPosition is the starting position
     * @param otherPosition is the position to check
     * Return true if the positions are close to each other
     */
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition){

        //Check if drone position is within margin of error using distanceTo
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * @param x,y         The coordinate of the point to check
     * @param x1,y1,x2,y2 The coordinates of the two points making the edge
     * Return true if the right vertical line that passes through x,y meet the edge
     */
    private boolean passEdge(double x, double y, double x1, double y1, double x2, double y2) {
        double boty = Math.min(y1, y2);
        double topy = Math.max(y1, y2);
        if (y <= boty || y >= topy) {
            return false;
        }
        if (x2 == x1) {
            return x1 > x;
        }
        double a = (y2 - y1) / (x2 - x1);
        double b = y1 - a * x1;
        double p = (y - b) / a;
        return p > x;
    }

    /**
     * check if the position is in the region that includes the border
     * using Basic Ray tracing algorithm to see if the point is in the polygon
     * @param position to check
     * @param region as a closed polygon
     * Return true if the position is inside the region that includes the border
     */
    public boolean isInRegion(LngLat position, NamedRegion region) {

        var corners = region.vertices();
        int n = corners.length;
        if (n >= 3) {
            int edge_passes = 0;
            for (int i = 0; i < n; i++) {
                if (passEdge(position.lng(), position.lat(), corners[i].lng(), corners[i].lat(),
                        corners[(i + 1) % n].lng(), corners[(i + 1) % n].lat())) {
                    edge_passes++;
                }
            }
            return edge_passes % 2 == 1;
        }
        return false;
    }

    /**
     * find the next position if an angle is applied to a startPosition
     * @param startPosition is where the start is
     * @param angle is the angle to use in degrees
     * Return the new position after the angle is used if the angle is valid
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {

        if ((angle >= MIN_ANGLE && angle < MAX_ANGLE) || (angle == HOVER)){

            switch (String.valueOf(angle)) {
                case "0.0":
                    return (new LngLat(startPosition.lng() + DRONE_MOVE_DISTANCE, startPosition.lat()));
                case "22.5":
                    return (new LngLat(startPosition.lng() + DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8), startPosition.lat() + DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8)));
                case "45.0":
                    return (new LngLat(startPosition.lng() + DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 4), startPosition.lat() + DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 4)));
                case "67.5":
                    return (new LngLat(startPosition.lng() + DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8), startPosition.lat() + DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8)));
                case "90.0":
                    return (new LngLat(startPosition.lng(), startPosition.lat() + DRONE_MOVE_DISTANCE));
                case "112.5":
                    return (new LngLat(startPosition.lng() - DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8), startPosition.lat() + DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8)));
                case "135.0":
                    return (new LngLat(startPosition.lng() - DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 4), startPosition.lat() + DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 4)));
                case "157.5":
                    return (new LngLat(startPosition.lng() - DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8), startPosition.lat() + DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8)));
                case "180.0":
                    return (new LngLat(startPosition.lng() - DRONE_MOVE_DISTANCE, startPosition.lat()));
                case "202.5":
                    return (new LngLat(startPosition.lng() - DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8), startPosition.lat() - DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8)));
                case "225.0":
                    return (new LngLat(startPosition.lng() - DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 4), startPosition.lat() - DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 4)));
                case "247.5":
                    return (new LngLat(startPosition.lng() - DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8), startPosition.lat() - DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8)));
                case "270.0":
                    return (new LngLat(startPosition.lng(), startPosition.lat() - DRONE_MOVE_DISTANCE));
                case "292.5":
                    return (new LngLat(startPosition.lng() + DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8), startPosition.lat() - DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8)));
                case "315.0":
                    return (new LngLat(startPosition.lng() + DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 4), startPosition.lat() - DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 4)));
                case "337.5":
                    return (new LngLat(startPosition.lng() + DRONE_MOVE_DISTANCE * Math.cos(Math.PI / 8), startPosition.lat() - DRONE_MOVE_DISTANCE * Math.sin(Math.PI / 8)));
                case "999.0":
                    return (startPosition);
                default:
                    System.err.println("Angle within range but not one of 16 directions");
                    return (startPosition);
            }
        } else {
            System.err.println("Angle is not within range");
            return (startPosition);
        }
    }

    public double distanceTo( LngLat coordinate ){
        return Math.sqrt(Math.pow(this.lng - coordinate.lng(),2) + Math.pow(this.lat - coordinate.lat(),2));
    }

    public double getLng(){
        return lng;
    }
    public double getLat(){
        return lat;
    }
}