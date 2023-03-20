package uk.ac.ed.inf.UnitTests;

import junit.framework.TestSuite;
import junit.framework.Test;
import junit.framework.TestCase;
import uk.ac.ed.inf.AppTest;
import uk.ac.ed.inf.Order;
import uk.ac.ed.inf.OrderOutcome;
import uk.ac.ed.inf.Restaurant;

import java.io.IOException;

public class ValidateOrdersUnitTests extends TestCase {
    public ValidateOrdersUnitTests( String testName ) throws IOException {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ValidateOrdersUnitTests.class );
    }


    Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer("https://ilp-rest.azurewebsites.net/", "restaurants");
    String[][] boundaryOrderItems = new String[][]{{"Super Cheese"},{"All Shrooms"},{"Margarita"},{"Calzone"},{"Super Cheese","All Shrooms","Super Cheese","All Shrooms"}};
    String[][] normalOrderItems = new String[][]{{"Super Cheese","All Shrooms"},{"Margarita","Calzone","Calzone"},{"Meat Lover","Vegan Delight"}};

    String[][] extremeOrderItems = new String[][]{{},{"Super Cheese","Super Cheese","Super Cheese","Super Cheese","Super Cheese"},{"Super Cheese","All Shrooms","Super Cheese","All Shrooms","Super Cheese"}};

    public void testNumberOfPizzasBoundary() throws IOException {
        for (int i = 0; i< boundaryOrderItems.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("04/28");
            order.setCvv("922");
            order.setOrderItems(boundaryOrderItems[i]);
            order.setPriceTotalInPence(order.getCost(restaurants));

            System.out.println(order.getOrderNo());
            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.Valid);
        }}

        public void testNumberOfPizzasNormal() throws IOException {
            for (int i = 0; i< normalOrderItems.length; i++){
                Order order = new Order();
                order.setOrderNo("1AFFE082");
                order.setOrderDate("2023-01-01");
                order.setCustomer("Gilberto Handshoe");
                order.setCreditCardNumber("5307769113183262");
                order.setCreditCardExpiry("04/28");
                order.setCvv("922");
                order.setOrderItems(normalOrderItems[i]);
                order.setPriceTotalInPence(order.getCost(restaurants));

                System.out.println(order.getOrderNo());
                //order.setOrderItems(boundaryOrders[i]);
                OrderOutcome outcome = order.getOrderOutcome(restaurants);
                System.out.println(outcome);
                assertTrue(outcome==OrderOutcome.Valid);
            }
    }
    public void testNumberOfPizzasExtreme() throws IOException {
        for (int i = 0; i< extremeOrderItems.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("04/28");
            order.setCvv("922");
            order.setOrderItems(extremeOrderItems[i]);
            order.setPriceTotalInPence(order.getCost(restaurants));

            System.out.println(order.getOrderNo());
            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.InvalidPizzaCount);
        }
    }

    String[][] validOrderSuppliers = new String[][]{{"Super Cheese"},{"All Shrooms"},{"Margarita"},{"Calzone"},{"Super Cheese","All Shrooms","Super Cheese","All Shrooms"},
            {"Super Cheese","All Shrooms"},{"Margarita","Calzone","Calzone"},{"Meat Lover","Vegan Delight"}};

    String[][] invalidOrderSuppliers = new String[][]{{"Super Cheese","Margarita","Super Cheese","All Shrooms"},
            {"Super Cheese","Calzone"},{"Margarita","Super Cheese","Calzone"},{"Meat Lover","Super Cheese"}};

    public void testSuppliersOfPizzasValid() throws IOException {
        for (int i = 0; i< validOrderSuppliers.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("04/28");
            order.setCvv("922");
            order.setOrderItems(validOrderSuppliers[i]);
            order.setPriceTotalInPence(order.getCost(restaurants));

            System.out.println(order.getOrderNo());
            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.Valid);
        }
    }

    public void testSuppliersOfPizzasInvalid() throws IOException {
        for (int i = 0; i< invalidOrderSuppliers.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("04/28");
            order.setCvv("922");
            order.setOrderItems(invalidOrderSuppliers[i]);
            order.setPriceTotalInPence(order.getCost(restaurants));

            System.out.println(order.getOrderNo());
            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.InvalidPizzaCombinationMultipleSuppliers);
        }
    }

    String[][] invalidPizzas = new String[][]{{"Super Chese"},{"Lot's Shrooms"},{"Pepperoni"},{"Very Calzone"},{"Very Cheese","All Shrooms","Super Cheese","All Shrooms"},
            {"Super Cheese","A Shrooms"},{"Meat Hater","Vegan Delight"}};

    public void testPizzasInvalid() throws IOException {
        for (int i = 0; i< invalidPizzas.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("04/28");
            order.setCvv("922");
            order.setOrderItems(invalidPizzas[i]);
            order.setPriceTotalInPence(0);

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.InvalidPizzaNotDefined);
        }
    }

    String[] invalidCardNumber = new String[]{"","4444","42536258796157867","686676871494167946294917324914696136491","0525362587961578",
    "42536258796157867","4424444424442444 ","5122-2368-7954 -3214 ","44244x4424442444"};
    String[] validCardNumber = new String[]{"5307769113183262","4121196586961320","4821177626277433","5156303003597456","4534800539956151",
    "4747053402122849","4401317543597467"};

    public void testInvalidCardNumber() throws IOException {
        for (int i = 0; i< invalidCardNumber.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber(invalidCardNumber[i]);
            order.setCreditCardExpiry("04/28");
            order.setCvv("922");
            order.setOrderItems(new String[]{"Margarita","Calzone","Calzone"});
            order.setPriceTotalInPence(order.getCost(restaurants));

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.InvalidCardNumber);
        }
    }

    public void testValidCardNumber() throws IOException {
        for (int i = 0; i< validCardNumber.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber(validCardNumber[i]);
            order.setCreditCardExpiry("04/28");
            order.setCvv("922");
            order.setOrderItems(new String[]{"Margarita","Calzone","Calzone"});
            order.setPriceTotalInPence(order.getCost(restaurants));

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.Valid);
        }
    }

    String[] invalidExpiryDates = new String[]{"07/13","51/7","05/22","04/19","1222","-1/24","0"};
    String[] validExpiryDates = new String[]{"07/23","04/24","02,29","12/29"};

    public void testInvalidExpiryDate() throws IOException {
        for (int i = 0; i< invalidExpiryDates.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry(invalidExpiryDates[i]);
            order.setCvv("922");
            order.setOrderItems(new String[]{"Margarita","Calzone","Calzone"});
            order.setPriceTotalInPence(order.getCost(restaurants));

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.InvalidExpiryDate);
        }
    }

    public void testValidExpiryDate() throws IOException {
        for (int i = 0; i< validExpiryDates.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry(validExpiryDates[i]);
            order.setCvv("922");
            order.setOrderItems(new String[]{"Margarita","Calzone","Calzone"});
            order.setPriceTotalInPence(order.getCost(restaurants));

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.Valid);
        }
    }

    String[] validCvvs = new String[]{"819","123","222","383","145","873","878"};
    String[] invalidCvvs = new String[]{"81965","12134","11222","38","1","7","7"};
    public void testValidCvv() throws IOException {
        for (int i = 0; i< validCvvs.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("07/23");
            order.setCvv(validCvvs[i]);
            order.setOrderItems(new String[]{"Margarita","Calzone","Calzone"});
            order.setPriceTotalInPence(order.getCost(restaurants));

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.Valid);
        }
    }

    public void testInvalidCvv() throws IOException {
        for (int i = 0; i< invalidCvvs.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate("2023-01-01");
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("07/23");
            order.setCvv(invalidCvvs[i]);
            order.setOrderItems(new String[]{"Margarita","Calzone","Calzone"});
            order.setPriceTotalInPence(order.getCost(restaurants));

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.InvalidCvv);
        }
    }

    String[] validDates = new String[]{"2023-01-01","2023-02-23","2023-02-14","2023-03-17","2023-04-15",
            "2023-01-01","2023-01-01","2023-01-01","2023-05-31"};
    String[] invalidDates = new String[]{"81965","12134","11222","38","1","7","7"};

    public void testValidDate() throws IOException {
        for (int i = 0; i< validDates.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate(validDates[i]);
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("07/23");
            order.setCvv("123");
            order.setOrderItems(new String[]{"Margarita","Calzone","Calzone"});
            order.setPriceTotalInPence(order.getCost(restaurants));

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.Valid);
        }
    }

    //String[][] validOrderSuppliers = new String[][]{{"Super Cheese"},{"All Shrooms"},{"Margarita"},{"Calzone"},{"Super Cheese","All Shrooms","Super Cheese","All Shrooms"},
            //{"Super Cheese","All Shrooms"},{"Margarita","Calzone","Calzone"},{"Meat Lover","Vegan Delight"}};

    String[][] items = new String[][] {{"Margarita","Calzone"},{"Meat Lover","Vegan Delight"},{"Super Cheese","All Shrooms"}};
    int[] costs = new int[]{2500,2600,2400};

    public void testCorrectCost() throws IOException {
        for (int i = 0; i< items.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate(validDates[i]);
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("07/23");
            order.setCvv("123");
            order.setOrderItems(items[i]);
            order.setPriceTotalInPence(costs[i]);

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.Valid);
        }
    }

    int[] incorrectCosts = new int[]{2300,2900,1900};
    public void testIncorrectCost() throws IOException {
        for (int i = 0; i< items.length; i++){
            Order order = new Order();
            order.setOrderNo("1AFFE082");
            order.setOrderDate(validDates[i]);
            order.setCustomer("Gilberto Handshoe");
            order.setCreditCardNumber("5307769113183262");
            order.setCreditCardExpiry("07/23");
            order.setCvv("123");
            order.setOrderItems(items[i]);
            order.setPriceTotalInPence(incorrectCosts[i]);

            //order.setOrderItems(boundaryOrders[i]);
            OrderOutcome outcome = order.getOrderOutcome(restaurants);
            System.out.println(outcome);
            assertTrue(outcome==OrderOutcome.InvalidTotal);
        }
    }




    }


