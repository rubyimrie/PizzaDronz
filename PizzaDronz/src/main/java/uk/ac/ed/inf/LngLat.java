package uk.ac.ed.inf;


import java.io.FileNotFoundException;
import java . io . IOException ;
import java . net .URL;

/**
 * This LngLat class is used to represent the position of the drone (longitude,latitude)
 * It contains the public paraments lng and lat, that can be manipulated outside this class.
 * It contains methods inCentralArea, getAngleFromDirection, distanceTo, closeTo and nextPosition
 * that are used when calculated the flightpath in the App class.
 *
 * @param lng - the longitude of the position of the drone
 * @param lat - the latitude of the position of the drone
 */
public record LngLat(double lng, double lat)  {


    /**
     * this method determines whether a point is in the central area that has been retrieved from the REST
     * server by using a standard point in polygon algorithm
     *
     * @param currentCA the central area points that have been retrieved from the REST server
     * @return - a boolean representing if a point is in the cental area, true if it is false if its not
     */
    public boolean inCentralArea(CentralAreaPoint[] currentCA){
        int i;
        int j;

        boolean result = false;

        //loops for every point in the central area
        for (i = 0, j = currentCA.length - 1; i < currentCA.length; j = i++) {
            if ((currentCA[i].latitude >= lat) != (currentCA[j].latitude >= lat) &&
                    (lng < (currentCA[j].longitude - currentCA[i].longitude) * (lat - currentCA[i].latitude) / (currentCA[j].latitude-currentCA[i].latitude) + currentCA[i].longitude)) {
                result = !result;
            }
        }
        return result;

        // code is based on method from this stack overflow page
        //https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon#:~:text=You%20can%20use%20the%20Polygon%20and%20Point%20in,%5BInteger%5D%20%E2%80%93%20Seraf%20Aug%2031%2C%202018%20at%2013%3A01
    }


    /**
     *
     * this method takes the given direction and calculates the corresponding angle
     *
     * @param direction - an instance of the Direction enum that is to be coverted
     * @return - the coresponding angle to the direction passed in
     */
    public static double getAngleFromDirection(Direction direction){
        //for instance of hover move the angle doesn't matter so returns junk value
        double angle =0;
        if (direction==null){
            angle = 999;
        }
        else {
            int count = 0;

            for (double i = 0; i < 360; i += 22.5) {
                if (direction == Direction.values()[count]) {
                    angle = i;
                }
                count++;
            }
        }
        return angle;}





    /**
     * this method calculates the distance between the location of the drone and the and the given
     * point using the distance formula  distance = √ [ (x₂ - x₁)² + (y₂ - y₁)²]
     *
     * @param position - position of point (i.e. restaurant) to calculate
     * @return the distance between the location of the drone and position of point(i.e. restaurant)
     */
    public double distanceTo(LngLat position) {
        //to implement
        double result = Math.sqrt(Math.pow((position.lat - this.lat), 2) + Math.pow((position.lng - this.lng), 2));

        return result;
    }


    /**
     * This method determines if 2 points are closeTo each other, to be used to check whether the drone
     * has reached its destination. It first calculates the distance between the 2 points and then determine
     * whether the distance is less than the CLOSE constant
     *
     * @param position - point used to check if current LongLat is within 0.00015 degrees
     * @return boolean representing if to 2 points are within close proximity,
     * true if within threshold, false otherwise
     */
    public boolean closeTo(LngLat position) {
        //setting the distance taking into consideration unavoidable rounding errors
        LngLat point = new LngLat(this.lng, this.lat);
        Double dist = point.distanceTo(position); // calculates the distance between the points
        return dist <= App.CLOSE;
        }


    /**
     * This method calculates the position of the next move in the given direction. It first
     * gets the value of the angle in decimal form and then calculates the change in the positions
     * longitude and latitude. And finishes the calculation by adding these to the original position.
     *
     * @param compDirection- the angle at which the drone will move
     * @return final position of LongLat after rotation and movement
     */
    public LngLat nextPosition(Direction compDirection) {

        //null compass direction represents a hover move so position remains the same
        if (compDirection == null){
            LngLat nextPos = new LngLat(lng+0,lat+0);
            return nextPos;
        }

        else {

        //starts  at East is 0
        //work out the angle based on direction passed
            int index = compDirection.ordinal();
            double angle = Math.toRadians(index * 22.5);;
            double changeLng=0;
            double changeLat=0;


        //works out the change in longitude and latitude
            changeLng = App.MOVE * Math.cos(angle);
            changeLat = App.MOVE * Math.sin(angle);


            LngLat nextPos = new LngLat(lng + changeLng,lat +changeLat);

            return nextPos;
        }
    }

}

