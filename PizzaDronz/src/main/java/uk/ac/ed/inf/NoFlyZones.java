package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * The purpose of this class is to hold the information about one of the no-fly zones.
 * It holds its name and its coordinates and contains methods to get them and set them.It will hold
 * data retrieved from the REST Server/
 */
public class NoFlyZones {

    @JsonProperty("name")
    public String getName () {
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    String name;

    @JsonProperty("coordinates")
    public double[][] getCoordinates () {
        return this.coordinates;
    }
    public void setCoordinates(double[][] coordinates){
        this.coordinates=coordinates;
    }
    double[][] coordinates;

    /**
     *
     * Reads in the  JSON data from a given URL. It checks that the baseURL ends with a /
     * to make sure that the complete URL will be of the correct format
     *
     * @param baseURL - the baseURL to access the REST Server
     * @param relativeURL - the endpoint, to complete the URL to receive the data the no-fly zones
     * @return an array containing all the no-fly zones that have been retrieved
     * @throws IOException
     */
    public static NoFlyZones[] getNoFlyZonesFromRESTServer(String baseURL,String relativeURL) throws IOException {

        if (!baseURL.endsWith("/")){
            baseURL += "/";
        }

        URL url = new URL( baseURL + relativeURL );

        NoFlyZones[] noFly = new ObjectMapper().readValue(url, NoFlyZones[].class);


        return noFly;}

    /**
     * This method will iterate through all the co-ordinates for each no-fly zone and save them as a LngLat
     * object. Each zones co-ordinates will be saved as a separate ArrayList of LngLat and another arraylist
     * will store all the arraylists of LngLat.
     *
     * @param NFZ - the no-fly zones
     * @return An ArrayList that contains ArrayLists of LngLat objects that store the information of each co-ordinate in the no-fly zone.
     */
    public static ArrayList<ArrayList<LngLat>> getZoneLines(NoFlyZones[] NFZ) throws IOException {

        ArrayList<ArrayList<LngLat>> dontCrossZone = new ArrayList<>(); //create an ArrayList to hold the ArrayLists that will contain the coordinates of all the no-fly zones
        for (NoFlyZones zone : NFZ) { //for each individual zone
            ArrayList<LngLat> dontCrossLines = new ArrayList<>(); //create an ArrayList to hold the co-ordinates of the zone
            for (double[] point: zone.getCoordinates()){
                dontCrossLines.add(new LngLat(point[0], point[1])); //turn the Double[] that stores the co-ordinates into a Drone object and add it to the ArrayList
            }
            dontCrossZone.add(dontCrossLines);
        }
        return dontCrossZone;
    }


}
