package MovingAverage;

import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Kevin on 9/19/2016.
 */
public class ConsumerTest {

    @Test
    public void tenValues() {
        ConsumerImpl consumer = new ConsumerImpl(false);

        for (int i = 0; i < 10; i++) {
            LinkedList<Double> singleValue = new LinkedList<>();
            singleValue.add(10d);
            consumer.computeAverages(i + 1, singleValue); //The same as doing a consume for 1 second
        }
        MovingAverages averages = consumer.getCurrentAverages();
        assertEquals(averages.tenSecond, 10.0);
        assertEquals(averages.thirtySecond, 10.0);
        assertEquals(averages.sixtySecond, 10.0);
    }

    @Test
    public void elevenValues() {
        ConsumerImpl consumer = new ConsumerImpl(false);

        for (int i = 0; i < 10; i++) {
            LinkedList<Double> singleValue = new LinkedList<>();
            singleValue.add(10d);
            consumer.computeAverages(i + 1, singleValue); //The same as doing a consume for 1 second
        }

        double value = 11;
        LinkedList<Double> extraValue= new LinkedList<>();
        extraValue.add(21d);
        consumer.computeAverages(11, extraValue); //The same as doing a consume for 1 second
        MovingAverages averages = consumer.getCurrentAverages();
        assertTrue(averages.tenSecond > value && averages.tenSecond < 21);
        assertEquals(averages.thirtySecond, value);
        assertEquals(averages.sixtySecond, value);
    }

    @Test
    public void thirtyOneValues() {
        ConsumerImpl consumer = new ConsumerImpl(false);

        for (int i = 0; i < 30; i++) {
            LinkedList<Double> singleValue = new LinkedList<>();
            singleValue.add(10d);
            consumer.computeAverages(i + 1, singleValue); //The same as doing a consume for 1 second
        }

        double value = ((30.0 * 10.0) + 21.0) / 31.0;
        LinkedList<Double> extraValue= new LinkedList<>();
        extraValue.add(21d);
        consumer.computeAverages(31, extraValue); //The same as doing a consume for 1 second
        MovingAverages averages = consumer.getCurrentAverages();
        assertTrue(averages.tenSecond > 10.0 && averages.tenSecond < 21);
        assertTrue(averages.thirtySecond > 10.0 && averages.thirtySecond < 21);
        assertEquals(averages.sixtySecond, value, 0.0001);
    }

    /**
     * This is the only really interesting test.
     * The expected value for all these tests is 21.3114...
     * You can see this calculation in the expectedValue.
     *
     * Now, all of these values should obviously be between 10 and 1000. HOWEVER, the more interesting part,
     * is the tenSecond average should be closer to 1000 than 30, and the 30 second value should be closer to 1000
     * than sixty.
     *
     * This is because the smaller averages put more "weight" on the more recent numbers.
     */
    @Test
    public void sixtyOneValues() {
        ConsumerImpl consumer = new ConsumerImpl(false);
        for (int i = 0; i < 60; i++) {
            LinkedList<Double> singleValue = new LinkedList<>();
            singleValue.add(10d);
            consumer.computeAverages(i + 1, singleValue); //The same as doing a consume for 1 second
        }
        double value = ((30.0 * 10.0) + 1000.0) / 61.0;
        LinkedList<Double> extraValue = new LinkedList<>();
        extraValue.add(1000d);
        consumer.computeAverages(61, extraValue); //The same as doing a consume for 1 second

        // Now they should all be accurate.
        MovingAverages averages = consumer.getCurrentAverages();
        assertTrue(averages.tenSecond > 10 && averages.tenSecond < 1000);
        assertTrue(averages.thirtySecond > 10 && averages.thirtySecond < 1000);
        assertTrue(averages.sixtySecond > 10 && averages.sixtySecond < 1000);
        assertTrue(averages.tenSecond > averages.thirtySecond && averages.thirtySecond > averages.sixtySecond);
        System.out.println("Averages 10:" + averages.tenSecond);
        System.out.println("Averages 30:" + averages.thirtySecond);
        System.out.println("Averages 60:" + averages.sixtySecond);
    }


    /**
     * This tests that, after throwing in the same number a bunch of times, all the averages should asymptotically
     * approach out expected value
     */
    @Test
    public void lotsOfValuesShouldAsymptoticallyApproachMax() {
        ConsumerImpl consumer = new ConsumerImpl(false);
        for (int i = 0; i < 60; i++) {
            LinkedList<Double> singleValue = new LinkedList<>();
            singleValue.add(10d);
            consumer.computeAverages(i + 1, singleValue); //The same as doing a consume for 1 second
        }

        // Now lets throw in a bunch of big numbers
        for(int i = 0; i < 1000; i++) {
            LinkedList<Double> singleValue = new LinkedList<>();
            singleValue.add(1000d);
            consumer.computeAverages(60 + i + 1, singleValue
            );
        }

        double expectedValue = 1000;

        MovingAverages averages = consumer.getCurrentAverages();

        //Basic sanity check
        assertTrue(averages.tenSecond > 10 && averages.tenSecond < 1000);
        assertTrue(averages.thirtySecond > 10 && averages.thirtySecond < 1000);
        assertTrue(averages.sixtySecond > 10 && averages.sixtySecond < 1000);
        //Make sure all the rules are still being followed
        assertTrue(averages.tenSecond >= averages.thirtySecond && averages.thirtySecond >= averages.sixtySecond);

        // Lets check to see if they're close to the average
        assertEquals(averages.tenSecond, expectedValue, 0.01);
        assertEquals(averages.thirtySecond, expectedValue, 0.01);
        assertEquals(averages.sixtySecond, expectedValue, 0.01);

        //Print them out for funsies
        System.out.println("Averages 10:" + averages.tenSecond);
        System.out.println("Averages 30:" + averages.thirtySecond);
        System.out.println("Averages 60:" + averages.sixtySecond);

    }



}
