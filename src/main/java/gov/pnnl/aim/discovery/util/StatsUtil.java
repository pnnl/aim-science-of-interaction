package gov.pnnl.aim.discovery.util;

import java.util.Arrays;

public class StatsUtil {

  /** Gets the mean of an array. */
  public static double getMean(double[] x) {
    double sum = 0;
    for (int i = 0; i < x.length; i++)
      sum += x[i];
    double mean = sum / x.length;
    return mean;
  }

  /** Gets the variance of an array. This is without Bessel's correction. */
  public static double getVariance(double[] x) {
    double mean = getMean(x);
    double sum = 0;
    for (int i = 0; i < x.length; i++) {
      double dx = x[i] - mean;
      sum += dx * dx;
    }
    double variance = sum / x.length;
    return variance;
  }

  /** Gets the standard deviation of an array. This is without Bessel's correction. */
  public static double getStandardDeviation(double[] x) {
    double variance = getVariance(x);
    double standardDeviation = Math.sqrt(variance);
    return standardDeviation;
  }

  /**
   * Computes the quantiles for an array.
   * 
   * @param x
   *          Array of values
   * @param n
   *          By example, 4 => quartiles and 100 => percentiles.
   * 
   * @return Array of quantile values sorted from min (index 0) to max (index n). Note that the array length is n + 1.
   */
  public static double[] getQuantiles(double[] x, int n) {
    if (n < 1)
      throw new IllegalArgumentException();

    double[] quantileSpec = new double[n + 1];
    for (int i = 0; i <= n; i++)
      quantileSpec[i] = (double) i / (double) n;

    double[] quantileValues = getQuantiles(x, quantileSpec);
    return quantileValues;
  }

  /**
   * Computes the quantiles for an array.
   * 
   * <p>
   * Note: This method is adapted from one provided by Amanda White.
   * 
   * 
   * @param x
   *          Array of values
   * @param quantileSpec
   *          Specifications for the quantiles to compute, as decimal values in the range [0, 1].
   * 
   * @return Array of quantile values, in same order as the specifications
   * 
   * @throws IllegalArgumentException
   *           if the value array is empty or if any quantile specification is outside [0, 1]
   */
  public static double[] getQuantiles(double[] x, double[] quantileSpec) {
    if (x.length == 0)
      throw new IllegalArgumentException();

    x = (double[]) x.clone();
    Arrays.sort(x);
    final int LAST = x.length - 1;

    double[] quantileValue = new double[quantileSpec.length];

    for (int i = 0; i < quantileSpec.length; i++) {
      if (quantileSpec[i] < 0 || quantileSpec[i] > 1)
        throw new IllegalArgumentException();

      // Compute the hypothetical index for the quantile
      double index = quantileSpec[i] * LAST;

      int floor = (int) Math.floor(index);
      if (floor == index) {
        // Don't have to interpolate
        quantileValue[i] = x[floor];
      } else {
        // Interpolate between the floor and ceiling values
        int ceiling = floor + 1;
        double wtFloor = ceiling - index;
        double wtCeiling = 1 - wtFloor;
        quantileValue[i] = wtFloor * x[floor] + wtCeiling * x[ceiling];
      }
    }
    return quantileValue;
  }

  /**
   * Computes a quantile for an array.
   * 
   * @param x
   *          Array of values
   * @param quantileSpec
   *          Specification for the quantile to compute, as decimal value in the range [0, 1].
   * 
   * @return Quantile value
   * 
   * @throws IllegalArgumentException
   *           if the value array is empty or if the quantile specification is outside [0, 1]
   */
  public static double getQuantile(double[] x, double quantileSpec) {
    double[] quantiles = getQuantiles(x, new double[] { quantileSpec });
    return quantiles[0];
  }

  /** Computes the median for an array. */
  public static double getMedian(double[] x) {
    double median = getQuantile(x, 0.5);
    return median;
  }

  /** Computes the sum for an array. */
  public static double getSum(double[] x) {
    double sum = 0.0;
    for (double v : x) {
      sum += v;
    }
    return sum;
  }

  /** Finds the minimum value in an array. */
  public static double getMin(double[] selValues) {
    double min = Double.MAX_VALUE;
    for (double v : selValues) {
      min = Math.min(min, v);
    }
    return min;
  }

  /** Finds the maximum value in an array. */
  public static double getMax(double[] selValues) {
    double max = Double.MIN_VALUE;
    for (double v : selValues) {
      max = Math.max(max, v);
    }
    return max;
  }

  /** Computes the range (max - min) for an array. */
  public static double getRange(double[] selValues) {
    return getMax(selValues) - getMin(selValues);
  }

}
