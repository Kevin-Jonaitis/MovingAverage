package MovingAverage;
/**
 * Created by Kevin on 9/19/2016.
 */

import java.util.LinkedList;

/**
 * This function calculates a weighted average based on past values and current values.
 * For longer period of times, you can think of it as giving more "weight" to older values and less "weight" to
 * newer values.
 *
 * It's an approximate algorithm.
 */
public class ConsumerImpl implements Consumer {

    double tenSecond = 0;
    double thirtySecond = 0;
    double sixtySecond = 0;
    long startTime;
    LinkedList<Double> valuesInOneSecond = new LinkedList<>();

    Thread computeAverageThread;

    /**
     * This allows us to not set a thread, and call computeAverages ourselves.
     */
    public ConsumerImpl(boolean assignDefaultThread) {
        if (assignDefaultThread) {
            assignComputeAverageThreadAndStart();
        }
    }

    /**
     * This might not run 1 second on the dot, but it'll run roughly every 1 second. This *might* cause a slight skew,
     * depending on what bucket each "consume" value falls into(this one or the previous one). However, in the long run,
     * the average wont be that much different.
     */
    public void assignComputeAverageThreadAndStart() {
        computeAverageThread = new Thread() {
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(1000);
                        LinkedList<Double> copy;
                        long secondsThatPassedLocal;
                        synchronized (this) {
                            secondsThatPassedLocal = (System.currentTimeMillis() - startTime) / 1000;
                            copy = (LinkedList<Double>) valuesInOneSecond.clone();
                            valuesInOneSecond.clear();
                        }
                        computeAverages((int) secondsThatPassedLocal, copy); // Yeah I know this can overflow the seconds
                    }
                } catch (InterruptedException e) {
                    System.out.println("Couldn't sleep for some strange reason");
                }
            }
        };
        computeAverageThread.start();


    }

    public MovingAverages consume(double value) {
        valuesInOneSecond.add(value);

        return new MovingAverages(tenSecond, thirtySecond, sixtySecond);
    }

    public void computeAverages(int secondsThatHavePassed, LinkedList<Double> passedInValues) {
        double sum = 0;
        double numofValuesInArray = 0;
        while (!passedInValues.isEmpty()) {
            double value = passedInValues.remove();
            numofValuesInArray++;
            sum = sum + value;
        }
        sum = sum / numofValuesInArray;

        double tenSecondOldAverageWeighted = 0;
        double thirtySecondOldAverageWeighted = 0;
        double sixtySecondOldAverageWeighted = 0;


        double tenDivisor = Math.min(secondsThatHavePassed, 10); //The total number of seconds we're looking at
        double thirtyDivisor = Math.min(secondsThatHavePassed, 30); //The total number of seconds we're looking at
        double sixtyDivisor = Math.min(secondsThatHavePassed, 60); //The total number of seconds we're looking at


        if (secondsThatHavePassed == 0) { //If these are the first values we have, simply return them
            tenSecond = sum;
            thirtySecond = sum;
            sixtySecond = sum;
        } else {

            tenSecondOldAverageWeighted = ((tenDivisor - 1) / tenDivisor) * tenSecond;
            thirtySecondOldAverageWeighted = ((thirtyDivisor - 1) / thirtyDivisor) * thirtySecond;
            sixtySecondOldAverageWeighted = ((sixtyDivisor - 1) / sixtyDivisor) * sixtySecond;
        }
        tenSecond = tenSecondOldAverageWeighted + (1/ tenDivisor) * sum;
        thirtySecond = thirtySecondOldAverageWeighted + (1/ thirtyDivisor) * sum;
        sixtySecond = sixtySecondOldAverageWeighted + (1/ sixtyDivisor) * sum;
    }

    public MovingAverages getCurrentAverages() {
        return new MovingAverages(tenSecond, thirtySecond, sixtySecond);
    }
}