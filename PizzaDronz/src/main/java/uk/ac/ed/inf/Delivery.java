package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used to represent the information that will be written to
 * the Deliveries-YYYY-MM-DD.json file.
 * It includes JsonProperty so the class can be serialised.
 */
public class Delivery {
    @JsonProperty("orderNo")
    String orderNo;

    @JsonProperty("outcome")
    OrderOutcome outcome;

    @JsonProperty("costInPence")
    int costInPence;

    /**
     * this is a constructor method which initialises the deliveries object
     *
     * @param orderNo     - a string of the unique order number for the current order
     * @param outcome     - the eventual orderOutcome of the current delivery
     * @param costInPence - an integer which stores the total price in pence of the current order
     */
    public Delivery(String orderNo, OrderOutcome outcome, int costInPence) {
        this.orderNo = orderNo;
        this.outcome = outcome;
        this.costInPence = costInPence;
    }

    /**
     * Will turn the object into a String to be displayed.
     * @return a String containing the values of the Deliveries object.
     */
}
