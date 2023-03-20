package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.swing.text.Position;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */

    public void testPerformanceTime() throws SQLException, IOException {
        LocalDate start= LocalDate.of(2023,01,01);
        LocalDate end= LocalDate.of(2023,05,31);
        ArrayList<Long> times = new ArrayList<>();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)){
            long startTime = System.nanoTime();
            App.main(new String[]{date.toString(), "https://ilp-rest.azurewebsites.net", "cabbage"});
            long endTime = System.nanoTime();
            Long time = endTime - startTime;
            times.add(time);
        }

        OptionalDouble average = times
                .stream()
                .mapToDouble(a -> a)
                .average();

        System.out.println(average);
}

    public void testValidateOrders() throws SQLException, IOException {
        LocalDate start= LocalDate.of(2023,01,01);
        LocalDate end= LocalDate.of(2023,05,31);
        ArrayList<Long> times = new ArrayList<>();
        Order[] orders = Order.getOrdersFromRestServer("https://ilp-rest.azurewebsites.net", "orders");
        Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer("https://ilp-rest.azurewebsites.net", "restaurants");

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)){
            System.out.println(date);
            ArrayList<Order> todaysOrders = new ArrayList<>();

            //pick the orders for the selected day
            for (Order ord : orders) {
                if (date.toString().equals(ord.orderDate)) {
                    todaysOrders.add(ord);
                }
            }
            System.out.println(orders.length);
            System.out.println(todaysOrders.size());
            ArrayList<OrderOutcome> outcomes = new ArrayList<>();

            for (int x = 1; x < todaysOrders.size(); x++){
                outcomes.add(todaysOrders.get(x).getOrderOutcome(restaurants)) ;
            }
        }}

        public void testValidateAllOrders() throws SQLException, IOException {

            Order[] orders = Order.getOrdersFromRestServer("https://ilp-rest.azurewebsites.net", "orders");
            Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer("https://ilp-rest.azurewebsites.net", "restaurants");


                ArrayList<OrderOutcome> outcomes = new ArrayList<>();

                for (int x = 1; x < orders.length; x++){
                    outcomes.add(orders[x].getOrderOutcome(restaurants)) ;
                }
                long valid = outcomes.stream().filter( c -> c == OrderOutcome.Valid ).count();
                long cvv = outcomes.stream().filter( c -> c == OrderOutcome.InvalidCvv ).count();
                long pizza = outcomes.stream().filter( c -> c == OrderOutcome.InvalidPizzaNotDefined ).count();
                long supplier = outcomes.stream().filter( c -> c == OrderOutcome.InvalidPizzaCombinationMultipleSuppliers ).count();
                long card = outcomes.stream().filter( c -> c == OrderOutcome.InvalidCardNumber ).count();
                long date = outcomes.stream().filter( c -> c == OrderOutcome.InvalidExpiryDate ).count();
                long total = outcomes.stream().filter( c -> c == OrderOutcome.InvalidTotal ).count();
                long count = outcomes.stream().filter( c -> c == OrderOutcome.InvalidPizzaCount ).count();

                System.out.println("Valid:" + valid + " Cvv:" + cvv + " Pizza: " + pizza + " Multiple Suppliers: " + supplier + " Card: " + card + " Date: " + date + " Total: " + total + " Count: " + count);

            }


    }








