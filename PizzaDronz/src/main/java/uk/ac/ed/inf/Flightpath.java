package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used to represent the information that will be written to
 * the Flightpath-YYYY-MM-DD.json file.
 * It includes JsonProperty so the class can be serialised.
 */
public class Flightpath {


    @JsonProperty("orderNo")
    final String orderNo;

    @JsonProperty("fromLongitude")
    final double fromLongitude;

    @JsonProperty("fromLatitude")
    final double fromLatitude;

    @JsonProperty("angle")
    final double angle;

    @JsonProperty("toLongitude")
    final double toLongitude;

    @JsonProperty("toLatitude")
    final double toLatitude;

    @JsonProperty("ticksSinceStartOfCalculation")
    final int ticksSinceStartOfCalculation;

    /**
     *
     * this is a constructor method which initialises the flightpath object
     *
     * @param orderNo - the unique order number for the current order being delivered
     * @param fromLongitude - the longitude of the poistion that the drone is moving from
     * @param fromLatitude - the latitude of the poistion that the drone is moving from
     * @param angle - the angle at which the drone is moving to its next position
     * @param toLongitude - the longitude of the poistion that the drone is moving to
     * @param toLatitude - - the latitude of the poistion that the drone is moving from
     * @param ticksSinceStartOfCalculation - the elapsed ticks since the computation
     * started for the day
     */
    Flightpath(String orderNo, double fromLongitude, double fromLatitude,
               double angle, double toLongitude, double toLatitude,int ticksSinceStartOfCalculation) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
        this.ticksSinceStartOfCalculation=ticksSinceStartOfCalculation;
    }

    /**
     * Will turn the object into a String to be displayed.
     * @return a String containing the values of the Deliveries object.
     */
    public String toString() {
        return "Flightpath: " + orderNo+ ", " + fromLongitude + ", " + fromLatitude + ", "  +
                angle + ", " + toLongitude + ", " + toLatitude + ", " + ticksSinceStartOfCalculation;
    }
}
