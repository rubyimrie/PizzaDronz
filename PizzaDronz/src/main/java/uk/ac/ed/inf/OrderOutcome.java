package uk.ac.ed.inf;

/**
 * An enum that holds constants for all possible order outcomes
 */
public enum OrderOutcome {
    Delivered,//The order has been delivered The order has been delivered
    InvalidCardNumber,//The card number is invalid, checked using Luhn check
    InvalidExpiryDate,//The card exiry date is invalid, either because
    // the month was an invalid (not between 1 and 12) or because the orderDate is the expiry date
    InvalidCvv,//the cvv is invalid because not all integer or is more than 4 digits long
    InvalidTotal,//the provided total doesn’t equal the calculated total
    InvalidPizzaNotDefined,//one of the items is not supplied from any of the restaurants
    InvalidPizzaCount,//there is either no pizzas or over 4
    InvalidPizzaCombinationMultipleSuppliers,//the pizzas cant all be supplied from the same restaurant
    Pending,//this is the initial outcome of the order before it goes through any checks
    Valid,//the order is valid but not yet delivered
    Undelivered; //the order is valid but was not delivered as the drone ran out of moves
}
