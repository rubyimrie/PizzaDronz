package uk.ac.ed.inf;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


/**
 * <p>
     * This class will calculate a flight path for the drone on a specific day to deliver all the orders
     * possible. The user will input a string containing the chose date in the YYYY-MM-DD format, the baseURL
     * to reach the REST-server, and a random seed.
     * Using this information, the application will retrieve all the orders for the given date
     * and the restaurant, central area and no fly zone information.
     * It then generates the path to deliver as many of the orders as possible before the Drone
     * runs out of moves.
 * </p>
 * <p>
 *     It will then generate 3 files, one for the flightpath, drone movements, and deliveries with
 *     the information for the chosen date.
 * </p>
 * */
public class App 
{
    //the constants used across all classes
    public static final LngLat APPLETON = new LngLat(-3.186874,55.944494);
    public static final double CLOSE = 0.00015;
    public static final double MOVE = 0.00015;

    public static final int TOTALMOVES = 2000;


    /**
     * This provides the main functionality of the application. It reads in the input arguments.
     * Calls the method that checks that they are valid. Retrieves the data from the REST-Server
     * Selects the order for the chosen dates. And calls the methods to work out the path to and from
     * each restaurant from appleton.
     * Then calculated the overall flightpath whilst trying to deliver the most orders possible and
     * only using the drones available 2000 moves.
     * It then generates the output .geojson and .json files.
     *
     * @param args - the user inputs a string containing the chose date in the YYYY-MM-DD format, the baseURL,
     *             and a random seed
     *
     * @throws IOException
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException, IOException {
        int startTime =(int) System.nanoTime();

        //check that the correct number of arguments have been entered
        if (args.length != 3) {
            System.err.println("Incorrect number of input arguments");
            System.exit(0);
        }

        //read in arguements
        String date = args[0];
        String baseURL = args[1];
        String random = args[2];

        //check all the arguments are valid
        checkArgsValid(date,baseURL,random);

        //read all the information in

        Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer(baseURL, "restaurants");
        Order[] orders = Order.getOrdersFromRestServer(baseURL, "orders");
        NoFlyZones[] NFZ = NoFlyZones.getNoFlyZonesFromRESTServer(baseURL, "noflyzones");
        CentralAreaPoint[] currentCA = CentralAreaPoint.getCentralAreaFromRESTServer(baseURL, "centralArea");



        ArrayList<Order> todaysOrders = new ArrayList<>();

        //pick the orders for the selected day
        for (Order ord : orders) {
            if (date.equals(ord.orderDate)) {
                todaysOrders.add(ord);
            }
        }

        //find the flightpath from appleton to each restaurant and from the restaurant to appleton
        for(Restaurant r: restaurants){
            r.movesTo = pickUpOrder(r, APPLETON,currentCA,NFZ);
            r.movesFrom = returnAppleton(r, APPLETON,currentCA,NFZ);
        }


        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        ArrayList<Order> todaysValidOrders = new ArrayList<Order>();

        //select the orders valid for delivery
        for (int x = 1; x < todaysOrders.size(); x++){
            if (todaysOrders.get(x).getOrderOutcome(restaurants)==OrderOutcome.Valid){
                todaysValidOrders.add(todaysOrders.get(x));
            }
            //add the invalid deliveries to the deliveries array
            else{
                Delivery delivery = new Delivery(todaysOrders.get(x).orderNo,
                        todaysOrders.get(x).getOrderOutcome(restaurants),
                        todaysOrders.get(x).priceTotalInPence+100
                        );
                //add this undelivered invalid delivery to the deliveries arraylist
                deliveries.add(delivery);
            }
        }

        if (todaysValidOrders.size()==0){
            System.err.println("There is no valid orders on this date");
            System.exit(0);
        }



        //selection sort to put orders in order of which restaurant is closer
        int pos;
        Order temp;
        for (int i = 0; i < todaysValidOrders.size(); i++)
        {
            pos = i;
            for (int j = i+1; j < todaysValidOrders.size(); j++)
            {
                if (todaysValidOrders.get(j).getRestaurant(restaurants).movesTo.movesMade() < todaysValidOrders.get(pos).getRestaurant(restaurants).movesTo.movesMade())                  //find the index of the minimum element
                {
                    pos = j;
                }
            }

            temp = todaysValidOrders.get(pos);//swap the current element with the minimum element
            todaysValidOrders.set(pos,todaysValidOrders.get(i));
            todaysValidOrders.set(i,temp);
        }



        //work the way through the new ordered list of today's orders
        //make sure have enough moves left before making the moves
        //add the journey to the flightpath

        boolean movesLeft = true; //whether there is enough moves left to deliver next order
        int numMovesLeft = TOTALMOVES;
        int i = 0; //order number

        //will store the full flightpath for the delivery of each order
        ArrayList<Flightpath> fullFlightpath = new ArrayList<Flightpath>();




        while (movesLeft) {
            //check has enough moves left to deliver this order
            int orderNumMoves = todaysValidOrders.get(i).getRestaurant(restaurants).movesTo.movesMade() +
                    todaysValidOrders.get(i).getRestaurant(restaurants).movesFrom.movesMade();
            if (numMovesLeft >= orderNumMoves) {

                //for every move that the current order need
                for (int x = 0; x < todaysValidOrders.get(i).getRestaurant(restaurants).movesTo.moves().size(); x++) {
                    //flightpath to get to the restaurant

                    Move moveTo = todaysValidOrders.get(i).getRestaurant(restaurants).movesTo.moves().get(x);

                    Flightpath flightpathTo = new Flightpath(todaysOrders.get(i).orderNo,
                            moveTo.fromPosition().lng(),
                            moveTo.fromPosition().lat(),
                            LngLat.getAngleFromDirection(moveTo.direction()),
                            moveTo.toPosition().lng(),
                            moveTo.toPosition().lat(),
                            (int) (System.nanoTime()-startTime)
                    );
                    fullFlightpath.add(flightpathTo);


                }


                for (int x = 0; x < todaysValidOrders.get(i).getRestaurant(restaurants).movesFrom.moves().size(); x++){
                    //flightpath to get back to appleton

                    Move moveFrom = todaysValidOrders.get(i).getRestaurant(restaurants).movesFrom.moves().get(x);
                    Flightpath flightpathFrom = new Flightpath(todaysValidOrders.get(i).orderNo,
                            moveFrom.fromPosition().lng(),
                            moveFrom.fromPosition().lat(),
                            LngLat.getAngleFromDirection(moveFrom.direction()),
                            moveFrom.toPosition().lng(),
                            moveFrom.toPosition().lat(),
                            (int) (System.nanoTime()-startTime)
                    );
                fullFlightpath.add(flightpathFrom);
            }


                //work out the number of moves left after delivering this order
                numMovesLeft -= todaysValidOrders.get(i).getRestaurant(restaurants).movesTo.movesMade();
                numMovesLeft -= todaysValidOrders.get(i).getRestaurant(restaurants).movesFrom.movesMade();

                //add this delivered order to the deliveries arraylist with the outcome delivered
                Delivery deliveryD= new Delivery(todaysValidOrders.get(i).orderNo,
                        OrderOutcome.Delivered,
                        todaysValidOrders.get(i).priceTotalInPence);
                deliveries.add(deliveryD);
                //System.out.println("Order Delivered" + i);


            }
            else {
                movesLeft=false;
                //System.out.println(numMovesLeft);
                break;
            }
            i++; //add 1 to i to move on to next order




            }
        System.out.println("Number delivered: "+i);
        //mark all undelivered flights has undelivered
        for (int j = i ; j < todaysValidOrders.size(); j++){
            Delivery deliveryU = new Delivery(todaysValidOrders.get(j).orderNo,
                    OrderOutcome.Undelivered,
                    todaysValidOrders.get(j).priceTotalInPence);
            deliveries.add(deliveryU);}


        ArrayList<LngLat> droneFlightpath = new ArrayList<>();
        droneFlightpath.add(APPLETON);

        //from the flightpath add all the to positions
        for (int p =0; p < fullFlightpath.size();p++){
            LngLat dronePos = new LngLat(fullFlightpath.get(p).toLongitude,fullFlightpath.get(p).toLatitude);
            droneFlightpath.add(dronePos);
        }

        String currentPath = new java.io.File(".").getCanonicalPath();


        String fileNameDeliveries =  currentPath+ "/resultfiles/deliveries-" + date + ".json";
        String fileNameFlightpath = currentPath+ "/resultfiles/flightpath-" + date + ".json";
        String fileNameDrone =  currentPath+ "/resultfiles/drone-" + date + ".geojson";






        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(fileNameFlightpath),fullFlightpath);
        writer.writeValue(new File(fileNameDeliveries),deliveries);

        FeatureCollection finalFeatureCollection = FeatureCollection.fromFeature(displayPath(droneFlightpath));
        writeToFile(fileNameDrone, finalFeatureCollection.toJson());


        }

    /**
     * This method checks whether the user inputs are valid. By checking the overall
     * length and that the month and date are both integers within the correct range.
     * It checks that the date is within the range that is within the current Rest Server.
     * Also checks that the baseURL is correct and reachable.
     * If anything is incorrect then the system exits with a corresponding explanatory
     * message.
     *
     * @param date - the given date inputted by user
     * @param baseURL - the baseURL to access the REST Server
     * @param random - the random seed
     */
    private static void checkArgsValid(String date,String baseURL, String random){
        //the date should be 10 digits long if its in the correct format YYYY-MM-DD
        if(date.length()!=10){
            System.err.println("Invalid length of Date. It should be of the format YYYY-MM-DD");
        }

        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);


        // Check if the input month is valid
        try {
            if (Integer.parseInt(month) > 12 || Integer.parseInt(month)  < 1) {
                System.err.println("Invalid month input. Should be between 1 and 12");
                System.exit(0);}
        }
        catch (Exception e){
            System.err.println("Invalid month input.Should just be integer");
            System.exit(0);
        }

        // Check if the input day is valid
        try {
            if (Integer.parseInt(day) > 31 || Integer.parseInt(day) < 1) {
                System.err.println("Invalid day input!");
                System.exit(0);
            }
        }
        catch(Exception e){
            System.err.println("Invalid month input.Should just be integer");
            System.exit(0);
        }

        try {
            LocalDate dateF = LocalDate.parse(date);
        }
        catch(Exception e){
            System.err.println("Invalid date input. Should be of format YYYY-MM-DD. " +
                    "With Y,M,D all being numbers");
            System.exit(0);
        }

        LocalDate dateF = LocalDate.parse(date);
        LocalDate startDate = LocalDate.parse("2023-01-01");
        LocalDate endDate = LocalDate.parse("2023-05-31");

        if(dateF.isBefore(startDate) || dateF.isAfter(endDate)){
            System.err.println("This date is not valid as it is not within our system");
            System.exit(0);
        }


        try{
            Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer(baseURL,"restaurants");
        } catch (Exception e){
            System.err.println("Unable to connect to server with URL provided. Make sure the URL provided is correct");
            System.exit(0);
        }

    }

    /**
     *This method calculates the flight path for the drone from Appleton tower to restaurant
     * passed in. It first retrieves the location of the restaurant that it is calculating the path to
     * From the Drones current position it will calculate all possible moves in each possible direction
     * and choose the move so that the next move is the one with the closest distance
     * to the restaurant location while staying while not going into any
     * no-fly zones and not reentering the central area if has already left.
     * If there are no valid move, a LngLat  with junk values (-999.0, -999.0) will be added
     *
     * It records each move and direction, and the number of moves made in instance of Moves class.
     *
     *
     * @param restaurant -  the restaurant object that an order will be collected from
     * @param current_pos - the current position of the drone before starting the journey
     * @param CA - the central area that the drone operates in
     * @param NFZ - the no-fly zone areas that the drone is not allowed to enter
     * @return - each move and direction, and the number of moves made in instance of Moves class
     * @throws IOException
     */
    private static Moves pickUpOrder(Restaurant restaurant, LngLat current_pos,CentralAreaPoint[] CA,NoFlyZones[] NFZ) throws  IOException {

        //find the pick-up locations
        LngLat pickUpLocation = new LngLat(restaurant.getLongitude(), restaurant.getLatitude());
        int movesMade = 0;

        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<LngLat> bestMoves = new ArrayList<>();
        bestMoves.add(current_pos);//to keep track of moves already made


        //while the drone has not yet reached close to the restaurant
        while (!current_pos.closeTo(pickUpLocation)) {


            SortedMap<Double, LngLat> validMoves = new TreeMap<>(); // sortedmaps to hold the best move as the first key
            SortedMap<Double, Direction> validDirections = new TreeMap<>();
            //work out which valid direction moves the drone closest to the pick-up location
            for (Direction direction : Direction.values()){
                LngLat moveTo = current_pos.nextPosition(direction);
                //check that if drone has left the central area it doesn't go back in
                if (!current_pos.inCentralArea(CA)){
                // and doesn't intersect the no-fly zones
                    //and checks that the drone hasnt already been at that position to avoid it getting stuck
                    //between 2 positions
                    if (!(moveTo.inCentralArea(CA)) && !checkForIntersection(current_pos, moveTo,NFZ) && (Collections.frequency(bestMoves,moveTo)<2)) {
                        validMoves.put(moveTo.distanceTo(pickUpLocation), moveTo);
                        validDirections.put(moveTo.distanceTo(pickUpLocation), direction);
                    }
                }
                else {
                    //if it is still in the central area doesn't matter if leaves or if next point is still in
                    //checks doesn't intersect no-fly zone
                    //and checks that the drone hasn't already been at that position to avoid it getting stuck
                    //between 2 positions
                    if (!checkForIntersection(current_pos, moveTo,NFZ) && !bestMoves.contains(moveTo)) {
                        validMoves.put(moveTo.distanceTo(pickUpLocation), moveTo);
                        validDirections.put(moveTo.distanceTo(pickUpLocation), direction);
                    }
                }
            }

            //if there are no valid moves
            if (validMoves.isEmpty()) {
                // turn the current position into a junk value if there is no valid move
                current_pos = new LngLat(-999.0, -999.0);
                break;
            } else {

                //validMoves.get(validMoves.firstkey())
                // will have the co-ordinates of the best move as it will have the smallest distance.
                LngLat bestMove = validMoves.get(validMoves.firstKey());


                //gets the angle corresponding with the best move
                Direction bestDirection = validDirections.get(validDirections.firstKey());

                Move move = new Move(current_pos,bestMove,bestDirection);
                moves.add(move);
                //add the flightpath to the database
                movesMade = movesMade + 1;
                current_pos = bestMove; //update the current position of the drone
                bestMoves.add(bestMove);
            }
        }

        if (current_pos.closeTo(pickUpLocation)) {
            //hover move
            current_pos = current_pos.nextPosition(null);
            Move move = new Move(current_pos,current_pos,null);
            moves.add(move);
            //adds the hover position for the Drone to the flightpath table in the database
            movesMade = movesMade + 1;
        }
        Moves allMoves = new Moves(moves,movesMade);
        return allMoves;
    }


    /**
     *This method calculates the flight path for the drone from a restaurant
     * passed in to appleton tower. It first retrieves the location of the restaurant that it is calculating the path from
     * From the Drones current position it will calculate all possible moves in each possible direction
     * and choose the move so that the next move is the one with the closest distance
     * to appleton location while not going into any
     * no-fly zones and not leaving the central area if has already entered.
     * If there are no valid move, a LngLat  with junk values (-999.0, -999.0) will be added
     *
     * It records each move and direction, and the number of moves made in instance of Moves class.
     *
     *
     * @param restaurant -  the restaurant object that an order will be delivered from
     * @param appleton- the current position of the drone before starting the journey from appleton
     * @param CA - the central area that the drone operates in
     * @param NFZ - the no-fly zone areas that the drone is not allowed to enter
     * @return - each move and direction, and the number of moves made in instance of Moves class
     * @throws IOException
     */
    private static Moves returnAppleton(Restaurant restaurant,LngLat appleton,CentralAreaPoint[] CA, NoFlyZones[] NFZ) throws SQLException, IOException {
        //find the pick-up locations
        LngLat current_pos = new LngLat(restaurant.getLongitude(), restaurant.getLatitude());

        int movesMade = 0;

        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<LngLat> bestMoves = new ArrayList<>();
        bestMoves.add(current_pos);//to keep track of moves already made



        //if there are no valid moves, and therefore quit the pickup
        while (!current_pos.closeTo(appleton)) {


            SortedMap<Double, LngLat> validMoves = new TreeMap<>(); // sortedmaps to hold the best move as the first key
            SortedMap<Double, Direction> validDirections = new TreeMap<>();
            for (Direction direction : Direction.values()){
                LngLat moveTo = current_pos.nextPosition(direction);
                // makes sure if the move is already in central area then it doesn't leave
                // and doesn't intersect the no-fly zones
                if (current_pos.inCentralArea(CA)){
                    //and checks that the drone hasnt already been at that position to avoid it getting stuck
                    //between 2 positions
                    if (moveTo.inCentralArea(CA) && !checkForIntersection(current_pos, moveTo,NFZ) && (Collections.frequency(bestMoves,moveTo)<2)) {
                        validMoves.put(moveTo.distanceTo(appleton), moveTo);
                        validDirections.put(moveTo.distanceTo(appleton), direction);
                    }
                }

                //if the previous move wasn't in the central area then the new position doesn't need to be
                //and checks that the drone hasn't already been at that position to avoid it getting stuck
                //between 2 positions
                else if(!checkForIntersection(current_pos, moveTo,NFZ) && !bestMoves.contains(moveTo)){
                    validMoves.put(moveTo.distanceTo(appleton), moveTo);
                    validDirections.put(moveTo.distanceTo(appleton), direction);
                }
            };

            //if there are no valid moves
            if (validMoves.isEmpty()) {
                // turn the current position into a junk value if there is valid move
                current_pos = new LngLat(-999.0, -999.0);
                break;
            } else {
                //create a junk value Drone that will be used later to hold the co-ordinates for the best move
                LngLat bestMove = new LngLat(0.0, 0.0);


                //to get the co-ordinates of the best move for the Drone to take.
                bestMove = validMoves.get(validMoves.firstKey());


                //gets the angle associated with the best move
                Direction bestDirection = validDirections.get(validDirections.firstKey());

                Move move = new Move(current_pos,bestMove,bestDirection);
                moves.add(move);
                //add the flightpath to the database
                movesMade = movesMade + 1;
                current_pos = bestMove; //update the current position of the drone
                bestMoves.add(bestMove);
            }
        }

        if (current_pos.closeTo(appleton)) {
            //hover move
            current_pos = current_pos.nextPosition(null);
            Move move = new Move(current_pos,current_pos,null);
            moves.add(move);
            //adds the hover position for the Drone to the flightpath table in the database
            movesMade = movesMade + 1;
        }
        Moves allMoves = new Moves(moves,movesMade);
        return allMoves;
    }

    /**
     * This method takes the current position of the Drone and the position it wants to
     * go to. It will check if the line between these two points intersects any
     * line contained within the no-fly zone.
     *
     * @param currentPos - current position of drone
     * @param moveToPos - the position the drone is trying to go to
     * @param NFZ - the no-fly zones that the drone isn't allowed to enter
     * @return
     * @throws IOException
     */

    private static Boolean checkForIntersection(LngLat currentPos, LngLat moveToPos,NoFlyZones[] NFZ) throws IOException {
        Boolean doesIntersect = false;
        ArrayList<ArrayList<LngLat>> doNotCrossZones = NoFlyZones.getZoneLines(NFZ);

        for (ArrayList<LngLat> zone : doNotCrossZones) { // for each zone in the no-fly-zones
            for (int i = 0; i < zone.size(); i++) {
                if (i == zone.size() - 1) { //if at the last element of the zone list
                    //check if there is an intersection from the Drone move and the final co-ordinate and the first co-ordinate line in the zone.
                    if ((intersect(currentPos, moveToPos, zone.get(i), zone.get(0)))) {
                        doesIntersect = true;
                        break;
                    }
                } else {
                    //check if there is an intersect between the Drone move and the line between at the given co-ordinates in the no-fly zone.
                    if (intersect(currentPos, moveToPos, zone.get(i), zone.get(i + 1))) {
                        doesIntersect = true;
                        break;
                    }
                }
            }
        }
        return doesIntersect;
    }

    /**
     * This method checks if the line between point1 & point2 intersects with the line between
     * point 3 & 4
     *
     * @param point1 - the co-ordinates of point1
     * @param point2 - the co-ordinates of point2
     * @param point3 - the co-ordinates of point3
     * @param point4 - the co-ordinates of point4
     * @return boolean of whether the line between point 1 & 2 intersects with the line between
     * point 3 & 4, true if it does false if it doesn't

     */
    private static Boolean intersect(LngLat point1, LngLat point2, LngLat point3, LngLat point4) {
        //check if the line between point1 and point2 intersects with the line between point 3 and 4
        LngLat a = new LngLat(point2.lng() - point1.lng(), point2.lat() - point1.lat());
        LngLat b = new LngLat(point4.lng() - point3.lng(), point4.lat() - point3.lat());
        LngLat c = new LngLat(-a.lat(), a.lng());
        double d = (((point1.lng() - point3.lng()) * c.lng()) + ((point1.lat() - point3.lat()) * c.lat())) / ((b.lng() * c.lng()) + (b.lat() * c.lat()));
        if (0 <= d && d <= 1) {
            return true;
        } else return false;
    }

    /**
     * This method converts an arraylist of LngLat to a Feature of the type
     * LineString which illustrates the flightpath of the drone
     *
     * @param path - each LngLat position that the drone moves to
     * @return a Feature of type LineString. The LineString contains
     * a list of coordinates which illustrate the flightpath of the drone
     */
    private static Feature displayPath(ArrayList<LngLat> path) {

        List<Point> points = new ArrayList<Point>();
        for (LngLat position : path) {
            points.add(Point.fromLngLat(position.lng(), position.lat()));
        }
        LineString lineString = LineString.fromLngLats(points);
        Feature feature = Feature.fromGeometry(lineString);
        return feature;
    }

    /**
     * writes given string to given filename
     *
     * @param fileName - file to be written to
     * @param str - string to be written to file
     * @throws IOException
     */
    private static void writeToFile(String fileName, String str) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);

        writer.close();
    }

}
