package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.ConstructorProperties;

/**
 * The purpose of this class is to store the information on the  individual menu items, including their
 * name and their priceInPence
 */
public class Menu {

    @JsonProperty("name")
    public String getName () {
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    String name;

    @JsonProperty("priceInPence")
    public int getPriceInPence () {
        return this.priceInPence;
    }
    public void setPriceInPence(int priceInPence){
        this.priceInPence=priceInPence;
    }
    int priceInPence;

}
