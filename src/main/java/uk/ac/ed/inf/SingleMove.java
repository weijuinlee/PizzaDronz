package uk.ac.ed.inf;

/**
 * Represents a single movement in a flight path, designed to be serialized into JSON format.
 *
 * @author B209981
 */

class SingleMove {
    private final String orderNo;        // Order number associated with the move
    private final double fromLongitude;  // Starting longitude of the move
    private final double fromLatitude;   // Starting latitude of the move
    private final double angle;          // Angle of movement in degrees
    private final double toLongitude;    // Ending longitude of the move
    private final double toLatitude;     // Ending latitude of the move

    /**
     * Constructs a FlightPathMove instance.
     *
     * @param orderNo        The order number associated with this move.
     * @param fromLongitude  The starting longitude of the move.
     * @param fromLatitude   The starting latitude of the move.
     * @param angle          The angle of movement in degrees.
     * @param toLongitude    The ending longitude of the move.
     * @param toLatitude     The ending latitude of the move.
     */
    public SingleMove(String orderNo, double fromLongitude, double fromLatitude, double angle, double toLongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
    }

    // Getters for all fields can be added here if needed.
}