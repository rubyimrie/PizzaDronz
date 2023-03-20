package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java . net .URL;
import java.util.List;

/**
 * The purpose of this class is to store information of each restaurant that is retrieved
 * from the REST Server. It contains methods to retrieve the restaurant.
 */
public class Restaurant {

    @JsonProperty("name")
    public String getName () {
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    String name;

    @JsonProperty("longitude")
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude){
        this.longitude=longitude;
    }
    double longitude;

    @JsonProperty("latitude")
    public double getLatitude()  {
        return this.latitude;
    }
    public void setLatitude(double latitude){
        this.latitude=latitude;
    }
    double latitude;

    @JsonProperty("menu")
    public Menu[] getMenu(){return this.menu;}
    public void setMenu(Menu[] menu){this.menu=menu;}

    public Menu[] menu;

    public Moves movesTo;
    public Moves movesFrom;




    /**
     * Reads in the  JSON data from a given URL. It checks that the baseURL ends with a /
     * to make sure that the complete URL will be of the correct format
     *
     * @param baseURL - the baseURL to access the REST Server
     * @return an array containing all the restaurants that have been retrieved
     * @throws IOException
     */
    public static Restaurant[] getRestaurantsFromRestServer(String baseURL, String relativeURL) throws IOException {

        if (!baseURL.endsWith("/")){
            baseURL += "/";
        }

        URL url = new URL( baseURL + relativeURL );

        Restaurant[] restaurants = new ObjectMapper().readValue(url, Restaurant[].class);

        return restaurants;

    }

}
