package MovingAverage;

/**
 * Created by Kevin on 9/19/2016.
 */
interface Consumer {
    MovingAverages consume(double value);
    MovingAverages getCurrentAverages();


}