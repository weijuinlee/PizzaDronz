package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import static uk.ac.ed.inf.ilp.constant.SystemConstants.*;

/**
 * implement the needed computations for a LngLat
 */
public class LngLatHandler implements LngLatHandling {

    //constants for angles
    static final int MIN_ANGLE = 0;
    static final int MAX_ANGLE = 360;
    static final int HOVER = 999;

    /**
     * get the distance between two positions
     * @param startPosition is where the start is
     * @param endPosition is where the end is
     * @return the euclidean distance between the positions
     */

    public double distanceTo(LngLat startPosition, LngLat endPosition){

        //Finds the pythagorean distance between start and end positions
        return Math.hypot(startPosition.lng() - endPosition.lng(),  startPosition.lat() - endPosition.lat());
    }

    /**
     * check if two positions are close (< than SystemConstants.DRONE_IS_CLOSE_DISTANCE)
     * @param startPosition is the starting position
     * @param otherPosition is the position to check
     * @return if the positions are close
     */
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition){

        //Check if drone position is within margin of error using distanceTo
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * check if the @position is in the @region (includes the border)
     *
     * @param position to check
     * @param region   as a closed polygon
     * @return if the position is inside the region (including the border)
     */
    public boolean isInRegion(LngLat position, NamedRegion region) {
        int intersectCount = 0;

//        for (int i = 0; i < region.vertices().length; i++) {
//            Point vertA = region.vertices[i];
//            Point vertB = polygon.get((i + 1) % region.size()); // Connect the last vertex with the first
//
//            if (rayCastIntersect(x, y, vertA, vertB)) {
//                intersectCount++;
//            }
//
//            // Check if the point lies on the edge
//            if (isPointOnEdge(x, y, vertA, vertB)) {
//                return true;
//            }
//        }

        return intersectCount % 2 == 1;

    }

    /**
     * find the next position if an angle is applied to a startPosition
     * @param startPosition is where the start is
     * @param angle is the angle to use in degrees
     * @return the new position after the angle is used if the angle is valid
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
}