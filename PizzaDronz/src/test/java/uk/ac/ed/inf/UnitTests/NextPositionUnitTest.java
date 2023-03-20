package uk.ac.ed.inf.UnitTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.AppTest;
import uk.ac.ed.inf.Direction;
import uk.ac.ed.inf.LngLat;

import java.util.Random;

public class NextPositionUnitTest extends TestCase {

    public NextPositionUnitTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( NextPositionUnitTest.class );
    }

    public void testPositionConstructor() {
        assertTrue( new LngLat( -3.192472,55.944233) != null );
    }



    final LngLat p0 = new LngLat(-3.192472, 55.942622);

    public void testPositionLongitude() {
        assertTrue(p0.lng() == -3.192472);
    }

    public void testPositionLatitude() {
        assertTrue(p0.lat() == 55.942622);
    }



    public void testCloseTo1(){
        LngLat pos = new LngLat(-3.192473,55.942619);
        assertTrue(p0.closeTo(pos));}

    public void testCloseTo2(){
        LngLat pos = new LngLat(-3.188394,56.944426);
        assertFalse(p0.closeTo(pos));}



    boolean approxEq(double d0, double d1) {
        final double epsilon = 1.0E-12d;
        return Math.abs(d0 - d1) < epsilon;
    }

    boolean approxEq(LngLat p0, LngLat p1) {
        return approxEq(p0.lat(), p1.lat()) && approxEq(p0.lng(), p1.lng());
    }



    public void testNextPositionNotIdentity() {
        System.out.println(p0);
        LngLat p1 = p0.nextPosition(Direction.N);
        System.out.println(p1);
        assertFalse(approxEq(p0, p1));
    }

    public void testNorthThenSouth() {
        System.out.println(p0);
        LngLat p1 = p0.nextPosition(Direction.N);
        System.out.println(p1);
        LngLat p2 = p1.nextPosition(Direction.S);
        assertTrue(approxEq(p0, p2));
    }

    public void testHover() {
        LngLat p1 = p0.nextPosition(null);
        System.out.println(p1);
        assertTrue(approxEq(p0, p1));
    }

    public void testGetAngleFromDirection(){
        double angle = LngLat.getAngleFromDirection(Direction.WNW);
        System.out.println(angle);

    }

    public void testNextPositionRandomNorthThenSouth(){
        //random testing/blackbox
        for (int i =0; i<100;i++){
            Random rand = new Random();

            double lng = rand.nextDouble(4);
            double lat = rand.nextDouble(4);
            int dir = rand.nextInt(16);

            LngLat pos = new LngLat(lng,lat);
            LngLat pos2 = pos.nextPosition(Direction.values()[dir]);
            LngLat pos3 = pos2.nextPosition(Direction.values()[dir].getOppositeDirection());
            assertTrue(approxEq(pos, pos3));
        }

    }

}
