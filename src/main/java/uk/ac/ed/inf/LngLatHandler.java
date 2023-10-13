package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.awt.*;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;

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
     * find the next position if an @angle is applied to a @startPosition
     * @param startPosition is where the start is
     * @param angle is the angle to use in degrees
     * @return the new position after the angle is used if the angle is valid
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {

        if (angle >= MIN_ANGLE && angle < MAX_ANGLE) {

            //calculate the location after moving 0.00015 degree in the direction of the input degree
            double newLng = startPosition.lng() + (DRONE_MOVE_DISTANCE * Math.cos(Math.toRadians(angle)));
            double newLat = startPosition.lat() + (DRONE_MOVE_DISTANCE * Math.sin(Math.toRadians(angle)));
            return new LngLat(newLng, newLat);
        } else {
            return startPosition;
        }
    }
}