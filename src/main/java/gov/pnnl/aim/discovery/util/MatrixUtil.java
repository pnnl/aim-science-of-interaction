package gov.pnnl.aim.discovery.util;

import gov.pnnl.jac.geom.CoordinateList;
import gov.pnnl.jac.geom.RealMatrixCoordinateList;
import gov.pnnl.jac.math.linalg.PCA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.primitives.Doubles;

public class MatrixUtil {

  public static RealMatrix zScaling(final RealMatrix matrix) {
    
    RealMatrix origMatrix = matrix.copy();
    
    int colCount = origMatrix.getColumnDimension();
    int rowCount = origMatrix.getRowDimension();
    for (int col = 0; col < colCount; col++) {
      double [] dist = origMatrix.getColumn(col);
      for (int row = 0; row < rowCount; row++) {
        matrix.setEntry(row,  col, zscore(dist, origMatrix.getEntry(row, col)));
      }
    }
    
    return matrix;
  }
  
  public static double zscore(double [] dist, double value) {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (double v : dist) {
      stats.addValue(v);
    }
    double mean = stats.getMean();
    double stddev = stats.getStandardDeviation();
    
    return (value - mean) / stddev;
  }
  
  public static RealMatrix computeMinMax(final CoordinateList coordList) {

    final int rows = coordList.getCoordinateCount();
    final int cols = coordList.getDimensionCount();

    final double[] dmin = new double[cols];
    final double[] dmax = new double[cols];
    Arrays.fill(dmin, Double.MAX_VALUE);
    Arrays.fill(dmax, -Double.MAX_VALUE);

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        double d = coordList.getCoordinate(i, j);
        if (Double.isNaN(d)) {
          d = 0.0;
        }
        if (d < dmin[j]) {
          dmin[j] = d;
        }
        if (d > dmax[j]) {
          dmax[j] = d;
        }
      }
    }

    return new Array2DRowRealMatrix(new double[][] { dmin, dmax });
  }

  public static RealMatrix computeMinMax(final RealMatrix coordList) {

    final int rows = coordList.getRowDimension();
    final int cols = coordList.getColumnDimension();

    final double[] dmin = new double[cols];
    final double[] dmax = new double[cols];
    Arrays.fill(dmin, Double.MAX_VALUE);
    Arrays.fill(dmax, -Double.MAX_VALUE);

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        double d = coordList.getEntry(i, j);
        if (Double.isNaN(d)) {
          d = 0.0;
        }
        if (d < dmin[j]) {
          dmin[j] = d;
        }
        if (d > dmax[j]) {
          dmax[j] = d;
        }
      }
    }

    return new Array2DRowRealMatrix(new double[][] { dmin, dmax });
  }

  public static RealMatrix computeGlobalMinMax(final RealMatrix coordList) {

    final int rows = coordList.getRowDimension();
    final int cols = coordList.getColumnDimension();

    double dmin = Double.MAX_VALUE;
    double dmax = -Double.MAX_VALUE;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        double d = coordList.getEntry(i, j);
        if (Double.isNaN(d)) {
          d = 0.0;
        }
        if (d < dmin) {
          dmin = d;
        }
        if (d > dmax) {
          dmax = d;
        }
      }
    }

    return new Array2DRowRealMatrix(new double[] { dmin, dmax });
  }

  public static void normalize2d(RealMatrix nspace) {

    double min0 = Double.MAX_VALUE;
    double max0 = -Double.MAX_VALUE;
    double min1 = Double.MAX_VALUE;
    double max1 = -Double.MAX_VALUE;

    for (int i = 0; i < nspace.getColumnDimension(); i++) {
      double v = nspace.getEntry(0, i);
      if (v < min0) {
        min0 = v;
      }
      if (v > max0) {
        max0 = v;
      }
    }

    for (int i = 0; i < nspace.getColumnDimension(); i++) {
      double v = nspace.getEntry(1, i);
      if (v < min1) {
        min1 = v;
      }
      if (v > max1) {
        max1 = v;
      }
    }

    double rang0 = max0 - min0;
    double rang1 = max1 - min1;

    for (int i = 0; i < nspace.getColumnDimension(); i++) {

      double v0 = nspace.getEntry(0, i);
      nspace.setEntry(0, i, (v0 - min0) / rang0);

      double v1 = nspace.getEntry(1, i);
      nspace.setEntry(1, i, (v1 - min1) / rang1);
    }
  }

  public static void normalizeDimensionsGlobal(RealMatrix nspace) {
    normalizeDimensionsGlobal(nspace, MatrixUtil.computeGlobalMinMax(nspace));
  }

  public static void normalizeDimensionsGlobal(RealMatrix nspace, RealMatrix minmax) {

    int rows = nspace.getRowDimension();
    int cols = nspace.getColumnDimension();

    double min = minmax.getEntry(0, 0);
    double range = minmax.getEntry(1, 0) - minmax.getEntry(0, 0);
    double[] coords = new double[cols];
    for (int i = 0; i < rows; i++) {
      coords = nspace.getRow(i);
      for (int j = 0; j < cols; j++) {
        if (range == 0.0) {
          coords[j] = 0.0;
        } else {
          coords[j] = (coords[j] - min) / range;
        }
      }
      nspace.setRow(i, coords);
    }

  }

  public static void normalizeDimensions(RealMatrix nspace) {
    normalizeDimensions(nspace, MatrixUtil.computeMinMax(nspace));
  }

  public static void normalizeDimensions(CoordinateList nspace) {
    normalizeDimensions(nspace, MatrixUtil.computeMinMax(nspace));
  }

  public static void normalizeDimensions(CoordinateList nspace, RealMatrix minmax) {

    int rows = nspace.getCoordinateCount();
    int cols = nspace.getDimensionCount();

    double[] dmin = new double[cols];
    double[] drange = new double[cols];
    for (int i = 0; i < cols; i++) {
      dmin[i] = minmax.getEntry(0, i);
      drange[i] = minmax.getEntry(1, i) - dmin[i];
    }

    double[] coords = new double[cols];
    for (int i = 0; i < rows; i++) {
      nspace.getCoordinates(i, coords);
      for (int j = 0; j < cols; j++) {
        double min = dmin[j];
        double range = drange[j];
        if (range == 0.0) {
          coords[j] = 0.0;
        } else {
          coords[j] = (coords[j] - min) / range;
        }
      }
      nspace.setCoordinates(i, coords);
    }

  }

  public static void normalizeDimensions(RealMatrix nspace, RealMatrix minmax) {

    int rows = nspace.getRowDimension();
    int cols = nspace.getColumnDimension();

    double[] dmin = new double[cols];
    double[] drange = new double[cols];
    for (int i = 0; i < cols; i++) {
      dmin[i] = minmax.getEntry(0, i);
      drange[i] = minmax.getEntry(1, i) - dmin[i];
    }

    double[] coords = new double[cols];
    for (int i = 0; i < rows; i++) {
      coords = nspace.getRow(i);
      for (int j = 0; j < cols; j++) {
        double min = dmin[j];
        double range = drange[j];
        if (range == 0.0) {
          coords[j] = 0.0;
        } else {
          coords[j] = (coords[j] - min) / range;
        }
      }
      nspace.setRow(i, coords);
    }

  }

  static public RealMatrix computeDistanceMatrix(RealMatrix nspace) {

    int size = nspace.getRowDimension();
    RealMatrix distMatrix = new Array2DRowRealMatrix(size, size);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        double dist = ((i == j) ? 0.0 : (MatrixUtil.distance(nspace.getRow(i), nspace.getRow(j))));
        distMatrix.setEntry(i, j, dist);
      }
    }

    MatrixUtil.normalizeDimensions(distMatrix);

    return distMatrix;
  }

  static private double[][] reduce(double[][] matrix) {

    Array2DRowRealMatrix realMatrix = new Array2DRowRealMatrix(matrix);
    PCA pca = new PCA(realMatrix, PCA.CovarianceType.COVARIANCE, 2);
    RealMatrix projMatrix = pca.getPrincipalComponents();

    MatrixUtil.normalizeDimensions(new RealMatrixCoordinateList(projMatrix), MatrixUtil.computeMinMax(projMatrix));

    return projMatrix.getData();

    // The orientation of this needs to be inverted to work with the calling code
    // return MDSJ.classicalScaling(matrix);
  }

  public static double distance(double[] a, double[] b) {
    // return new Cosine().distanceBetween(a, b);
    // return new gov.pnnl.jac.geom.distance.Manhattan().distanceBetween(a, b) / 50.0;
    return new gov.pnnl.jac.geom.distance.Euclidean().distanceBetween(a, b);
  }

  public static void printMatrix(RealMatrix matrix) {

    for (int i = 0; i < matrix.getRowDimension(); i++) {
      for (int j = 0; j < matrix.getColumnDimension(); j++) {
        System.out.print(matrix.getEntry(i, j) + "\t");
      }
      System.out.println();
    }
  }

  public static void printVector(RealVector v1Vector) {
    for (int i = 0; i < v1Vector.getDimension(); i++) {
      System.out.print(v1Vector.getEntry(i) + "\t");
    }
    System.out.println();
  }

  public static void printModelResponse(List<double[]> modelResponseHistory, List<String> features) {
    
    int count = features.size();
    for (int i = 0; i < count; i++) {

      System.out.print(features.get(i));
      
      for (int k = 0; k < modelResponseHistory.size(); k++) {
        double [] weights = modelResponseHistory.get(k);
        System.out.print("\t" + weights[i]);
      }
      System.out.println();
      
//      double [] weights = modelResponseHistory.get(i);
//      for (int j = 0; j < weights.length; j++) {
//        System.out.print("\t" + weights[j]);
//      }
      
    }    
  }

  public static void printCoords(final CoordinateList matrix) {

    for (int i = 0; i < matrix.getCoordinateCount(); i++) {
      String row = "";
      for (int j = 0; j < matrix.getDimensionCount(); j++) {
        row += matrix.getCoordinate(i, j) + " ";
      }
      System.out.println(row);
    }
  }

  public static void printGroups(Map<Integer, Set<Integer>> groups) {
    for (Map.Entry<Integer, Set<Integer>> entry : groups.entrySet()) {
      System.out.print("G" + entry.getKey());
      for (int member : entry.getValue()) {
        System.out.print("\t" + member);
      }
      System.out.println();
    }
  }
  
  public static List<Double> extractImageVector(RealVector row) {
    List<Double> ret = new ArrayList<Double>();
    
    double[] data = new double[500];
    double[] full = row.toArray();
    
    data = Arrays.copyOfRange(full, 201, full.length);
    
    for( int i=0; i<data.length; i++ ) {
      ret.add(data[i]);
    }
    
    return ret;
  }
  
  public static List<Double> extractTextVector(RealVector row) {
    List<Double> ret = new ArrayList<Double>();
    
    double[] data = new double[200];
    double[] full = row.toArray();
    
    data = Arrays.copyOfRange(full, 0, 200);
    
    for( int i=0; i<data.length; i++ ) {
      ret.add(data[i]);
    }
    
    return ret;
  }
  
  public static List<Double> convertToList(RealVector row) {
    List<Double> list = Doubles.asList(row.toArray());
    
    return list;
  }
  
  public static RealVector convertToRealVector(List<Double> list) {
    RealVector rv = new ArrayRealVector(Doubles.toArray(list));
    
    return rv;
  }

  public static void print(Set<Integer> topFeatures) {
    System.out.print("Top Features: ");
    for (int index : topFeatures) {
      System.out.print(index + " ");
    }
    System.out.println();
  }  
  
  /** 
   * Resizes a RealMatrix, copying data where available, zeroing elsewhere. This
   * returns a new matrix except when the requested size matches the existing 
   * size, in which case the original matrix is returned. 
   */
  public static RealMatrix resizeMatrix(RealMatrix original, int rows, int columns) {
      // Get existing size
      int rowsOriginal = original.getRowDimension();
      int columnsOriginal = original.getColumnDimension();
      
      if (rows == rowsOriginal  &&  columns == columnsOriginal) {
          // No change in size, so just use the original
          return original;
      }
      
      // Find how much data overlap there is
      int rowsToCopy = Math.min(rowsOriginal, rows);
      int columnsToCopy = Math.min(columnsOriginal, columns);
      
      // Get the overlapping data
      double[][] data = new double[rowsToCopy][columnsToCopy];
      original.copySubMatrix(0, rowsToCopy - 1, 0, columnsToCopy - 1, data);
      
      // Copy the overlapping data to a new matrix
      RealMatrix resized = new Array2DRowRealMatrix(rows, columns);
      resized.setSubMatrix(data, 0, 0);
      
      return resized;
  }
}
