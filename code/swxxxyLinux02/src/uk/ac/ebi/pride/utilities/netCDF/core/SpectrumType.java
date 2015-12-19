package uk.ac.ebi.pride.utilities.netCDF.core;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 25/09/15
 */
public enum SpectrumType {

    NONE,
    CENTROIDED,
    PROFILE,
    THRESHOLDED;


    public static SpectrumType detectType(Map<Float, Float> scanPoints) {

        // If the spectrum has less than 100 data points, it should be
        // centroided.

        Float finishedPercentage = null;

        boolean canceled = false;
        SortedMap<Float, Float> map = new TreeMap<Float, Float>(scanPoints);

        Float[] mzValues = new Float[map.size()];
        Float[] intensityValues = new Float[map.size()];

        int i = 0;
        for(Map.Entry entry: map.entrySet()){
            mzValues[i] = (Float) entry.getKey();
            intensityValues[i] = (Float) entry.getValue();
            i++;
        }

        if(scanPoints == null){
            return NONE;
        }
        if (scanPoints.size() < 100)
            return SpectrumType.CENTROIDED;

        // Go through the data points and find the highest one
        float maxIntensity = 0f;
        int topDataPointIndex = 0;
        for (i = 0; i < map.size();i++ ) {

            // If the spectrum contains data points of zero intensity, it should
            // be in profile mode
            if (intensityValues[i] == 0.0) {
                return PROFILE;
            }

            // Let's ignore the first and the last data point, because
            // that would complicate our following checks
            if ((i == 0) || (i == map.size() - 1))
                continue;

            // Update the maxDataPointIndex accordingly
            if (intensityValues[i] > maxIntensity) {
                maxIntensity = intensityValues[i];
                topDataPointIndex = i;
            }
        }

        // Check if canceled
        if (canceled)
            return NONE;

        finishedPercentage = 0.3f;

//        // Now we have the index of the top data point (except the first and
//        // the last). We also know the spectrum has at least 5 data points.
//        assert topDataPointIndex > 0;
//        assert topDataPointIndex < dataPoints.getSize() - 1;
//        assert dataPoints.getSize() >= 5;

        // Calculate the m/z difference between the top data point and the
        // previous one
        final double topMzDifference = Math.abs(mzValues[topDataPointIndex] - mzValues[topDataPointIndex - 1]);

        // For 5 data points around the top one (with the top one in the
        // center), we check the distribution of the m/z values. If the spectrum
        // is continuous (thresholded), the distances between data points should
        // be more or less constant. On the other hand, centroided spectra
        // usually have unevenly distributed data points.

        for (i = topDataPointIndex - 2; i < topDataPointIndex + 2; i++) {

            // Check if the index is within acceptable range
            if ((i < 1) || (i > scanPoints.size() - 1))
                continue;

            final double currentMzDifference = Math.abs(mzValues[i] - mzValues[i - 1]);

            // Check if the m/z distance of the pair of consecutive data points
            // falls within 25% tolerance of the distance of the top data point
            // and its neighbor. If not, the spectrum should be centroided.
            if ((currentMzDifference < (0.8 * topMzDifference))
                    || (currentMzDifference > (1.25 * topMzDifference))) {
                return SpectrumType.CENTROIDED;
            }

        }

        // Check if canceled
        if (canceled)
            return NONE;

        finishedPercentage = 0.7f;

        // Finally, we check the data points on the left and on the right of the
        // top one. If the spectrum is continuous (thresholded), their intensity
        // should decrease gradually from the top data point. Let's check if
        // their intensity is above 1/3 of the top data point. If not, the
        // spectrum should be centroided.
        final double thirdMaxIntensity = maxIntensity / 3;
        final double leftDataPointIntensity = intensityValues[topDataPointIndex
                - 1];
        final double rightDataPointIntensity = intensityValues[topDataPointIndex
                + 1];
        if ((leftDataPointIntensity < thirdMaxIntensity)
                || (rightDataPointIntensity < thirdMaxIntensity))
            return SpectrumType.CENTROIDED;

        // Check if canceled
        if (canceled)
            return NONE;

        // If we could not find any sign that the spectrum is centroided, we
        // conclude it should be thresholded.
        return SpectrumType.THRESHOLDED;
    }
}
