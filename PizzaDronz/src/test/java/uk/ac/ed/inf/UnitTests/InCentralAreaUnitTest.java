package uk.ac.ed.inf.UnitTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.AppTest;
import uk.ac.ed.inf.CentralAreaPoint;
import uk.ac.ed.inf.Direction;
import uk.ac.ed.inf.LngLat;

import java.io.IOException;
import java.util.Random;
public class InCentralAreaUnitTest extends TestCase {

    public InCentralAreaUnitTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( InCentralAreaUnitTest.class );
    }

    public void testInCentralArea() throws IOException {
        for (int i =0; i<100;i++){
        CentralAreaPoint[] ca = CentralAreaPoint.getCentralAreaFromRESTServer("https://ilp-rest.azurewebsites.net/","centralArea");
        Random rand = new Random();

        double rangeLng = (-3.184319-(-3.192473));
        double lng = rand.nextDouble()*rangeLng+(-3.192473);
        double rangeLat = 55.946233-55.942617;
        double lat = rand.nextDouble()*rangeLat+55.942617;

        LngLat position = new LngLat(lng,lat);
        assertTrue(position.inCentralArea(ca));}
    }

    public void testOutCentralArea() throws IOException {
        for (int i =0; i<100;i++){
            CentralAreaPoint[] ca = CentralAreaPoint.getCentralAreaFromRESTServer("https://ilp-rest.azurewebsites.net/","centralArea");
            Random rand = new Random();

            double rangeLng = (5-(4));
            double lng = rand.nextDouble()*rangeLng+(4);
            double rangeLat = 56.946233-55.946231;
            double lat = rand.nextDouble()*rangeLat+55.946231;

            LngLat position = new LngLat(lng,lat);
            assertFalse(position.inCentralArea(ca));}
    }

}
