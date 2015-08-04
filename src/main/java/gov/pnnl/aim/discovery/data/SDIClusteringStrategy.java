/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import gov.pnnl.aim.discovery.util.MatrixUtil;
import gov.pnnl.jac.cluster.Cluster;
import gov.pnnl.jac.cluster.ClusterList;
import gov.pnnl.jac.cluster.ClusterTask;
import gov.pnnl.jac.cluster.KMeansPlusPlusSeeder;
import gov.pnnl.jac.cluster.XMeansClusterTask;
import gov.pnnl.jac.cluster.XMeansClusterTaskParams;
import gov.pnnl.jac.geom.CoordinateList;
import gov.pnnl.jac.geom.RealMatrixCoordinateList;
import gov.pnnl.jac.geom.distance.DistanceFunc;
import gov.pnnl.jac.geom.distance.EuclideanNoNaN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import mdsj.MDSJ;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * @author hamp645
 *
 */
public class SDIClusteringStrategy extends ClusterStrategy implements Serializable {

  // For Serializable
  private static final long serialVersionUID = -4765524720160047372L;

  /** The location to store the seeds */
  private File dataFolder;

  /** If true, the cluster has been run before */
  private boolean hasClusterSeeds = false;

  /** The current cluster count */
  private int clusterCount;

  /** If true, uses Euclidean distancing */
  private final boolean useEuclidean = true;

  /** The clusters */
  private ClusterList clusters;

  /** The 2-space matrix */
  private RealMatrix twoSpace;


  private int[] rowMapper = null;

  /**
   * @param properties
   */
  public SDIClusteringStrategy(final MatrixProperties properties) {
    super(properties);
  }

  /**
   *
   */
  public SDIClusteringStrategy() {
    super();
  }

  @Override
  public void reset() {
    properties = null;
    clusterCount = -1;
    clusters = null;
    twoSpace = null;
  }

  @Override
  public ClusterList cluster(final Set<Integer> rows) {
    if (rows == null || rows.size() == 0 || rows.size() == properties.getMatrix().getRowDimension()) {
      clusters = runClustering();
    } else {
      clusters = runSubsetClustering(rows);
    }
    twoSpace = generateTwoSpace(clusters);
    return clusters;
  }

  @Override
  public ClusterList cluster() {
    clusters = runClustering();
    twoSpace = generateTwoSpace(clusters);

    return clusters;
  }

  @Override
  public RealMatrix getTwoSpaceMatrix() {
    return twoSpace;
  }

  @Override
  public ClusterList getClusters() {
    return clusters;
  }

  @Override
  public int getRepresentativeMember(final Cluster c) {
    int[] membership = c.getMembership();
    int repVertexID = membership[(int) (membership.length * Math.random())];
    return repVertexID;
  }

  @Override
  public int mapRowID(final int rowID) {
    int mappedRowID = rowID;
    if (rowMapper != null) {
      mappedRowID = rowMapper[rowID];
    }
    return mappedRowID;
  }

  private RealMatrix generateTwoSpace(final ClusterList clusters) {
    // Project cluster centroids
    RealMatrix centroidMatrix = clusters.createCentroidMatrix();
    MatrixUtil.normalizeDimensions(centroidMatrix);
    RealMatrix centroidsDistMatrix = MatrixUtil.computeDistanceMatrix(centroidMatrix);
    RealMatrix centroidsProjMatrix = new Array2DRowRealMatrix(MDSJ.stressMinimization(centroidsDistMatrix.getData(), 2));
    MatrixUtil.normalize2d(centroidsProjMatrix);

    return centroidsProjMatrix;
  }

  /**
   *
   * @param normalizeDims
   * @return a cluster list
   */
  private ClusterList runClustering() {

    RealMatrix nSpace = properties.getMatrix().copy();
    ClusterList clusters = runClustering(nSpace);

    // Save away this state
    setClusterResults(clusters.getClusterCount(), clusters.createCentroidMatrix());

    return clusters;
  }

  private ClusterList runSubsetClustering(final Set<Integer> vectorIDs) {
    System.out.println(vectorIDs);
    int rowCount = vectorIDs.size();
    RealMatrix parentNSpace = properties.getMatrix().copy();

    // Define a new matrix for the subset of vectors
    RealMatrix nSpace = new Array2DRowRealMatrix(rowCount, parentNSpace.getColumnDimension());

    rowMapper = new int[parentNSpace.getRowDimension()];
    Arrays.fill(rowMapper, -1);

    // Populate the subset matrix
    int index = 0;
    for (int rowID : vectorIDs) {
      nSpace.setRow(index, parentNSpace.getRow(rowID));
      rowMapper[index] = rowID;
      index++;
    }

    ClusterList clusters = runClustering(nSpace);

    setClusterResults(clusters.getClusterCount(), clusters.createCentroidMatrix());

    return clusters;
  }

  private ClusterList runClustering(final RealMatrix nSpace) {
    // Do clustering
    RealMatrixCoordinateList nSpaceCoords = new RealMatrixCoordinateList(nSpace);
    DistanceFunc distFunc = new EuclideanNoNaN();
    XMeansClusterTaskParams taskParams = new XMeansClusterTaskParams(Runtime.getRuntime().availableProcessors(), distFunc, new KMeansPlusPlusSeeder(1970), 3, -1, true);
    ClusterTask clusterTask = new XMeansClusterTask(nSpaceCoords, taskParams);
    clusterTask.run();

    return clusterTask.getClusterList();
  }

  /**
   *
   * @param clusterCount
   * @param centroidMatrix
   */
  private void setClusterResults(final int clusterCount, final RealMatrix centroidMatrix) {
    this.hasClusterSeeds = true;
    this.clusterCount = clusterCount;

    try {
      int rowCount = centroidMatrix.getRowDimension();
      int colCount = centroidMatrix.getColumnDimension();

      PrintWriter writer = new PrintWriter(new FileWriter(new File(dataFolder, "seeds.txt")));

      writer.println(Integer.toString(clusterCount));
      writer.println(Integer.toString(colCount));

      for (int i = 0; i < rowCount; i++) {
        for (int j = 0; j < colCount; j++) {
          writer.print(centroidMatrix.getEntry(i, j));
          if (j < colCount - 1) {
            writer.print(",");
          }
        }
        writer.println();
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /** Gets the row index corresponding to a row identifier String. */
  public int getRowIndex(String rowId) {
    int rowIndex = properties.getRowIndex(rowId);
    return rowIndex;
  }
  
  private void recallClusterResults() {
    File seedFile = new File(this.dataFolder, "seeds.txt");
    if (seedFile.exists()) {
      try {
        BufferedReader reader = new BufferedReader(new FileReader(seedFile));
        int rowCount = Integer.parseInt(reader.readLine());
        int colCount = Integer.parseInt(reader.readLine());

        double[][] matrix = new double[rowCount][colCount];

        int row = 0;
        String line;
        while ((line = reader.readLine()) != null) {

          String[] vals = line.split(",");

          for (int col = 0; col < vals.length; col++) {
            matrix[row][col] = Double.parseDouble(vals[col]);
          }

          row++;
        }
        reader.close();

        hasClusterSeeds = true;
        clusterCount = rowCount;

      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (NumberFormatException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void printMatrix(final RealMatrix matrix) {
    for (int i = 0; i < matrix.getRowDimension(); i++) {
      String row = "";
      for (int j = 0; j < matrix.getColumnDimension(); j++) {
        row += matrix.getEntry(i, j) + " ";
      }
    }
  }

  private void printCoords(final CoordinateList matrix) {
    for (int i = 0; i < matrix.getCoordinateCount(); i++) {
      String row = "";
      for (int j = 0; j < matrix.getDimensionCount(); j++) {
        row += matrix.getCoordinate(i, j) + " ";
      }
    }
  }

}
