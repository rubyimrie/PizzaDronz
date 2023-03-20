package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Calendar;

/**
 * The purpose of this class is to store information of each individual order that is retrieved
 * from the REST Server. It contains methods to retrieve the orders, determine the orderOutcome, get
 * the restaurant of the order, and the cost of the Order.
 */
public class Order {
    String orderNo;



    @JsonProperty("orderNo")
    public String getOrderNo() {
        return this.orderNo;
    }
    public void setOrderNo(String orderNo){
        this.orderNo=orderNo;
    }
    String orderDate;
    @JsonProperty("orderDate")
    public String getOrderDate() {
        return this.orderDate;
    }
    public void setOrderDate(String orderDate){
        this.orderDate=orderDate;
    }

    String customer;
    @JsonProperty("customer")
    public String getCustomer() {
        return this.customer;
    }
    public void setCustomer(String customer){
        this.customer=customer ;
    }
    String creditCardNumber;
    @JsonProperty("creditCardNumber")
    public String getCreditCardNumber() {
        return this.creditCardNumber;
    }
    public void setCreditCardNumber(String creditCardNumber ){
        this.creditCardNumber=creditCardNumber ;
    }
    String creditCardExpiry;
    @JsonProperty("creditCardExpiry")
    public String getCreditCardExpiry() {
        return this.creditCardExpiry;
    }
    public void setCreditCardExpiry(String creditCardExpiry){
        this.creditCardExpiry=creditCardExpiry ;
    }
    String cvv;
    @JsonProperty("cvv")
    public String getCvv() {
        return this.cvv;
    }
    public void setCvv(String cvv){
        this.cvv=cvv ;
    }

    int priceTotalInPence;
    @JsonProperty("priceTotalInPence")
    public int getPriceTotalInPence() {
        return this.priceTotalInPence;
    }
    public void setPriceTotalInPence(int priceTotalInPence){
        this.priceTotalInPence=priceTotalInPence ;
    }
    String[] orderItems;
    @JsonProperty("orderItems")
    public String[] getOrderItems() {
        return this.orderItems;
    }
    public void setOrderItems(String[] orderItems){
        this.orderItems=orderItems ;
    }


    /**
     *
     * Reads in the  JSON data from a given URL. It checks that the baseURL ends with a /
     * to make sure that the complete URL will be of the correct format
     *
     * @param baseURL - the baseURL to access the REST Server
     * @param relativeURL - the endpoint, to complete the URL to receive the data the orders
     * @return an array containing all the orders that have been retrieved
     * @throws IOException
     */
    public static Order[] getOrdersFromRestServer(String baseURL,String relativeURL) throws IOException {

        if (!baseURL.endsWith("/")){
            baseURL += "/";
        }

        URL url = new URL( baseURL + relativeURL );

        Order[] orders = new ObjectMapper().readValue(url, Order[].class);

        return orders;

    }


    private static boolean validityCheck(long cardNumber) {
        return (theSize(cardNumber) >= 13 && theSize(cardNumber) <= 16) && (prefixMatch(cardNumber, 4)
                || prefixMatch(cardNumber, 5) || prefixMatch(cardNumber, 37) || prefixMatch(cardNumber, 6))
                && ((sumDoubleEven(cardNumber) + sumOdd(cardNumber)) % 10 == 0);
    }
    // Get the result from Step 2
    private static int sumDoubleEven(long cardNumber) {
        int sum = 0;
        String num = cardNumber + "";
        for (int i = theSize(cardNumber) - 2; i >= 0; i -= 2)
            sum += getDigit(Integer.parseInt(num.charAt(i) + "") * 2);
        return sum;
    }
    // Return this cardNumber if it is a single digit, otherwise,
    // return the sum of the two digits
    private static int getDigit(int cardNumber) {
        if (cardNumber < 9)
            return cardNumber;
        return cardNumber / 10 + cardNumber % 10;
    }
    // Return sum of odd-place digits in cnumber
    private static int sumOdd(long cardNumber) {
        int sum = 0;
        String num = cardNumber + "";
        for (int i = theSize(cardNumber) - 1; i >= 0; i -= 2)
            sum += Integer.parseInt(num.charAt(i) + "");
        return sum;
    }
    // Return true if the digit d is a prefix for cardNumber
    private static boolean prefixMatch(long cardNumber, int d) {
        return getPrefix(cardNumber, theSize(d)) == d;
    }
    // Return the number of digits in d
    private static int theSize(long d) {
        String num = d + "";
        return num.length();
    }
    // Return the first k number of digits from
    // number. If the number of digits in number
    // is less than k, return number.
    private static long getPrefix(long cardNumber, int k) {
        if (theSize(cardNumber) > k) {
            String num = cardNumber + "";
            return Long.parseLong(num.substring(0, k));
        }
        return cardNumber;
    }
    /**
     *<p>
     * This purpose of the method is to work out if a order is valid, if not set it to the reason it
     * is invalid. Each order starts with its outcome being pending. It begins
     * by checking that the expiry date is valid, by checking that the month is valid and that the first date
     * of the date is after the date of order. If not true the outcome is InvalidExpiryDate.
     * It then checks there is a valid number of pizzas in the order. It also checks the cvv is valid,
     * by checking its length and checks the creditCardNumber is valid by using Luhn check. Then it checks if the
     * priceInTotalPence is correct by calculating the cost of the order. Then it checks
     * that all the pizzas can come from the same provider and that each pizza exists.
     *</p>
     *
     * @param restaurants vector of the restaurants participating including their menus
     * @return the order outcome
     */
    public OrderOutcome getOrderOutcome(Restaurant[] restaurants) throws  IOException {

        OrderOutcome outcome = OrderOutcome.Pending;
        //delivery.orderNo=this.orderNo;"


        try{
            //convert expiry date to date form with it being the end of the month
            LocalDate dateOrder = LocalDate.parse(this.orderDate);
            String expiryDay = "01";
            String expiryMonth = (this.creditCardExpiry).substring(0,2);
            String expiryYear = "20" + (this.creditCardExpiry).substring(3,5);
            String expiryFullDate = expiryYear + "-" + expiryMonth + "-" + expiryDay;
        if (Integer.parseInt(expiryMonth)>12 || Integer.parseInt(expiryMonth)<1){
            outcome=OrderOutcome.InvalidExpiryDate;
        } else {
            LocalDate expiryDate = LocalDate.parse(expiryFullDate);
            if (dateOrder.compareTo(expiryDate) > 0) {
                outcome = OrderOutcome.InvalidExpiryDate;
            }
        }
        }
        catch(Exception e) {
            outcome = OrderOutcome.InvalidExpiryDate;
        }




        //if there is no pizzas or too many in the order throw exception
        if (this.orderItems.length == 0 || this.orderItems.length > 4){
            outcome=OrderOutcome.InvalidPizzaCount;

            //throw new IllegalArgumentException("Invalid order as it contains no items.");
        }
        else{


        //checks if the cvv id valid by checking no longer than 4 digits and is only digits
        try {
            Integer.parseInt(this.cvv);
        }
        catch(Exception e){
            outcome= OrderOutcome.InvalidCvv;
        }

        if(this.cvv.length()>4 || this.cvv.length()<3 ){
            //check only digits
            outcome=OrderOutcome.InvalidCvv;
        }
            boolean isNum = true;
            //convert card number to Long so can be checked is valid
            try {
                Long cardNumber = Long.parseLong(this.creditCardNumber);
                //if unable to do this mustnt contain just integers so invalid credit card number
            }
            catch(Exception e){
                outcome=OrderOutcome.InvalidCardNumber;
                isNum=false;
            }
            if (isNum){
                Long cardNumber = Long.parseLong(this.creditCardNumber);
                if(!validityCheck(cardNumber)){
                    outcome=OrderOutcome.InvalidCardNumber;}
                }
            }

        //checks the card number is valid
        if(!validPizza(restaurants,this.orderItems)) {
            outcome= OrderOutcome.InvalidPizzaNotDefined;
        }

        else if (!sameSupplier(restaurants,this.orderItems)){
            outcome= OrderOutcome.InvalidPizzaCombinationMultipleSuppliers;
        }


            else if ((this.priceTotalInPence)!=this.getCost(restaurants)){
                outcome=OrderOutcome.InvalidTotal;

        }





        //if the order outcome hasn't been changed to any of the invalid options then
        //the order is valid
        if(outcome==OrderOutcome.Pending){
            outcome=OrderOutcome.Valid;
        }

        return outcome;

    }

    /**
     * This method is used to calculate the total cost of the order in pence
     * including the delivery cost. It does this by iterating through the restaurants to find
     * which one the first item comes from. And then iterates through this restaurant to get the price of
     * the other items adding them to the total cost.
     *
     * @param restaurants - array of the restaurants retrieved from REST Server
     * @return the total cost of all the items including the delivery cost
     */

    public boolean validPizza(Restaurant[] restaurants,String[] orderItems){
        Restaurant restaurant = null;
        Restaurant currentRestaurant = null;
        boolean valid = true;


        for(String s: orderItems){
            // determine if an item exists in any restaurant from the input restaurant array
            boolean foundInRestaurant = false;

            for(Restaurant r: restaurants){
                for(Menu m: r.getMenu()){
                    if(m.getName().equals((s))){
                        foundInRestaurant = true;
                    }
                }
            }
            if (!foundInRestaurant){
                valid = false;
            }
        }
        return valid;
    }

    public boolean sameSupplier(Restaurant[] restaurants, String[] orderItems) {
        String[] rests = new String[orderItems.length];

        // For each ordered item, find which restaurant and which menu in that restaurant it belongs to,
        // then add its cost to the total cost
        int i = 0;
        for (String s : orderItems) {

            // determine if an item exists in any restaurant from the input restaurant array
            boolean foundInRestaurant = false;

            for (Restaurant r : restaurants) {
                for (Menu m : r.getMenu()) {
                    if (m.getName().equals((s))) {
                        rests[i] = r.name;
                    }
                }
            }
            i++;
        }

        return Arrays.stream(rests).allMatch(s -> s.equals(rests[0]));
    }

    public int getCost(Restaurant[] restaurants){
        //loop for every restaurant in restaurants
        int cost;
        if (this.orderItems.length == 0 ){
            cost = 0;
        }
        else {

            Restaurant currentRestaurant = null;
            cost = 100;
            for (Restaurant r : restaurants) {
                //for every menu item in restaurant menu
                for (Menu m : r.getMenu()) {
                    if (m.getName().equals(this.orderItems[0])) {
                        //breaks if item is found
                        currentRestaurant = r;
                        cost += m.getPriceInPence();
                        break;
                    }
                }
                if (currentRestaurant != null)
                    break;
            }

            if (currentRestaurant != null) {
                for (int i = 1; i < this.orderItems.length; i++) {
                    boolean restaurantFound = false;
                    //for every menu in the current restaurant where the first pizza was found
                    for (Menu m : currentRestaurant.getMenu()) {
                        if (m.getName().equals(this.orderItems[i])) {
                            cost += m.getPriceInPence();
                            restaurantFound = true;
                        }
                    }
                }
            }
            else{
                return 0;
            }
        }
        return cost;
    }

    /**
     * This method is to find which restaurant corresponds to the current order by
     * finding which restaurant has the order items on it's menue
     *
     * @param restaurants - array of the restaurants retrieved from REST Server
     * @return the restaurant that the current order is from
     */
    public Restaurant getRestaurant(Restaurant[] restaurants){

        String firstOrder = this.orderItems[0];
        Restaurant currentRestaurant = null;

        for(Restaurant r: restaurants){
            //for every menu item in restaurant menu
            for(Menu m: r.getMenu()){
                if(m.getName().equals(firstOrder)){
                    //breaks if item is found
                    currentRestaurant  = r;
                    break;
                }
            }
            if(currentRestaurant != null)
                break;
        }
        return currentRestaurant;

    }
}
