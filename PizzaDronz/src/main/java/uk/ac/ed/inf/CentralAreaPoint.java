package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * The purpose of this class is to hold the information about a point that makes up the central area.
 * It holds its name, longitude and latitude and contains methods to get them and set them.It will hold
 * data retrieved from the REST Server.
 */

public class CentralAreaPoint {


    @JsonProperty("name")
    /**
     * this method returns the name of the CentralAreaPoint
     */
    public String getName () {
        return this.name;
    }
    /**
    this method allows the name of CentralAreaPoint to be set
     */
    public void setName(String name){
        this.name=name;
    }
    String name;

    @JsonProperty("longitude")
    /**
     * this method returns the longitude of the CentralAreaPoint
     */
    public double getLongitude() {
        return this.longitude;
    }
    /**
     this method allows the longitude of CentralAreaPoint to be set
     */
    public void setLongitude(double longitude){
        this.longitude=longitude;
    }
    double longitude;

    @JsonProperty("latitude")
    /**
     * this method returns the latitude of the CentralAreaPoint
     */
    public double getLatitude()  {
        return this.latitude;
    }
    /**
     this method allows the latitude of CentralAreaPoint to be set
     */
    public void setLatitude(double latitude){
        this.latitude=latitude;
    }
    double latitude;

    /**
     *
     * Reads in the  JSON data from a given URL. It checks that the baseURL ends with a /
     * to make sure that the complete URL will be of the correct format
     *
     * @param baseURL - the baseURL to access the REST Server
     * @param relativeURL - the endpoint, to complete the URL to receive the data on central area
     * @return an array containing all the central area points that were received from the REST Server
     * @throws IOException
     */

    public static CentralAreaPoint[] getCentralAreaFromRESTServer(String baseURL,String relativeURL) throws IOException {

        if (!baseURL.endsWith("/")){
            baseURL += "/";
        }

        URL url = new URL( baseURL + relativeURL );

        CentralAreaPoint[] coordinates = new ObjectMapper().readValue(url, CentralAreaPoint[].class);


        return coordinates;

    }


    }
