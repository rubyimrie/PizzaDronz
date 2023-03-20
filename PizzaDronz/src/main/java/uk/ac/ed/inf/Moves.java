package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * The purpose of this record is to hold all the moves made either to of from a Restaurant and Appleton
 *
 * @param moves - an arraylist of all the movements between a restaurant and appleton
 * @param movesMade - the number of moves made to gt between the restaurant and appleton
 */
public record Moves(ArrayList<Move> moves, int movesMade) {
}
