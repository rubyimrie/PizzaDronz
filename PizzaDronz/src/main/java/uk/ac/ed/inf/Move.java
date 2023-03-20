package uk.ac.ed.inf;

/**
 * The purpose of this record is to store an individual move either to or from a restaurant.
 *
 * @param fromPosition - the position the drone will be moving from
 * @param toPosition - the position the drone will be moving to
 * @param direction - the direction in which the drone is moving
 */
public record Move(LngLat fromPosition, LngLat toPosition, Direction direction){


}
