package uk.ac.ebi.pride.utilities.util.Math;

/**
 * @author ypriverol
 */
public class MathUtils {

    /**
     *  @return {lowest, highest}
     */
    public static double[] findMinMax(double[] data) {

        double lowest = Double.POSITIVE_INFINITY;
        double highest = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < data.length; i++) {
            if (lowest  > data[i]) lowest = data[i];
            if (highest < data[i]) highest = data[i];
        }
        return new double[]{lowest, highest};
    }

}
