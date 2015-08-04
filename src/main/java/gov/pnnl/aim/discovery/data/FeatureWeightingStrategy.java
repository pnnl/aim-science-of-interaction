/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import gov.pnnl.aim.discovery.util.MatrixUtil;
import gov.pnnl.aim.discovery.util.StatsUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.RealVector;

/**
 * Class that keeps track of the feature weighting for vectors.
 *
 * @author hamp645
 *
 */
public class FeatureWeightingStrategy extends BaseStrategy implements Serializable {

  // For Serializable
  private static final long serialVersionUID = -3420311203740271172L;

  /** The boost factor */
  private double boostFactor = .05;

  /** Threshold in number of standard deviations above the mean. */
  private final double numStdDevs = 1.5;

  /** The neighbor distance */
  private double neighborDistance = .25;

  /** If true, should a union operation */
  private final boolean doUnion = true;

  /** The feature weights */
  private double[] featureWeights = null;
  private double[] previousBoost = null;

  /** Document weights: Doc ID -> Weight (0 to 3). */
  private final Map<String, Integer> documentWeights = new HashMap<String, Integer>();


  public FeatureWeightingStrategy(final MatrixProperties properties) {
    super(properties);
  }

  /**
   * @return the featureWeights
   */
  public double[] getFeatureWeights() {
    return featureWeights;
  }

  /**
   * @param featureWeights
   *          the featureWeights to set
   */
  public void setFeatureWeights(final double[] featureWeights) {
    this.featureWeights = featureWeights;
  }

  /**
   * @param neighborDistance
   *          the neighborDistance to set
   */
  public void setNeighborDistance(final double neighborDistance) {
    this.neighborDistance = neighborDistance;
  }

  /**
   * @return the neighborDistance
   */
  public double getNeighborDistance() {
    return neighborDistance;
  }

  /**
   * @return the doUnion
   */
  public boolean isDoUnion() {
    return doUnion;
  }

  /**
   * @return Threshold in number of standard deviations above mean.
   */
  public double getNumStdDevs() {
    return numStdDevs;
  }

  /**
   * @return the boostFactor
   */
  public double getBoostFactor() {
    return boostFactor;
  }

  /**
   * @param boostFactor
   *          the boostFactor to set
   */
  public void setBoostFactor(final double boostFactor) {
    this.boostFactor = boostFactor;
  }

  /** Gets L2 norms for the weighted vectors. */
  public double[] getWeightedVectorNorms() {
    int rowCount = properties.getRowCount();
    double[] norms = new double[rowCount];

    for (int row = 0; row < rowCount; row++) {
      double[] vector = getWeightedRow(row);
      norms[row] = getL2Norm(vector);
    }

    return norms;
  }

  /**
   * Gets the vector for the given matrix row. The result is not guaranteed to
   * be a copy.
   */
  private double[] getRow(final int row) {
    double[] rowVector = properties.getVector(row);
    return rowVector;
  }

  /** Gets a new feature-weighted vector for the given matrix row. */
  private double[] getWeightedRow(final int row) {
    double[] rowVector = getRow(row);
    rowVector = getWeightedVector(rowVector);
    return rowVector;
  }

  /** Gets a new feature-weighted version of a vector. */
  public double[] getWeightedVector(double[] vector) {
    vector = vector.clone();
    for (int i = 0; i < vector.length; i++) {
      vector[i] *= featureWeights[i];
    }
    return vector;
  }

  /** Computes the L2 norm of a vector. */
  private static double getL2Norm(final double[] vector) {
    double sum = 0;
    for (double x : vector) {
      sum += x * x;
    }

    double norm = Math.sqrt(sum);
    return norm;
  }

  /**
   * @param columnCount
   */
  public void init(final int columnCount) {
    featureWeights = new double[columnCount];
    previousBoost = new double[columnCount];
    Arrays.fill(featureWeights, 1.0);
    Arrays.fill(previousBoost, 0.0);
  }

  @Override
  public void reset() {
    properties = null;
    featureWeights = null;
  }

  /** Boosts top features of a document. */
  public void boost(final String docId) {
    int matrixIndex = toMatrixRowIndex(docId);
    boost(matrixIndex);
  }

  /**
   * Boosts top features of a document.
   *
   * @param rowIndex  Row index of the document.
   */
  private void boost(final int rowIndex) {

    // Get the document's top features
    RealVector rowVector = properties.getRowVector(rowIndex);
    Set<Integer> topFeatures = getFeaturesAboveStdDevThreshold(rowVector);

    MatrixUtil.print(topFeatures);

    // Boost the top features
    boostFeatures(topFeatures);
  }

  /**
   * Boosts some feature weights in a group of documents (such as a cluster),
   * based on a document being added to the group. The boosted features are
   * those deemed significant to both the group and to the added document.
   *
   * @param newRowIndex
   * Row index of a document being added to a group (such as a cluster)
   *
   * @param groupRowIndexes
   * Row indexes of documents already in the group. Order doesn't matter.
   */
  public void boost(final int newRowIndex, final List<Integer> groupRowIndexes) {

    // Get the group members
    List<RealVector> groupVectors = new ArrayList<RealVector>();
    for (int i : groupRowIndexes) {
      groupVectors.add(properties.getRowVector(i));
    }

    // Get the vector being added to this group
    RealVector newVector = properties.getRowVector(newRowIndex);

    // Figure out which features are important

    //    Set<Integer> topFeatures = getTopFeatures(newVector, groupVectors, numStdDevs, doUnion);
    //    Set<Integer> topFeatures = getFeaturesAboveStdDev(newVector, numStdDevs);

    // Find features significant for the new document
    Set<Integer> featuresForNewDoc = getFeaturesAboveStdDevThreshold(newVector); //getNonZeroFeatures(newVector);

    // Find features significant for any document already in the group
    Set<Integer> featuresForGroup = new HashSet<Integer>();
    for (RealVector tgtVector : groupVectors) {
      featuresForGroup.addAll(getFeaturesAboveStdDevThreshold(tgtVector));
    }

    //    Set<Integer> topFeatures = new HashSet<Integer>();
    //    for (int index : featuresForNewDoc) {
    //      if (featuresForGroup.contains(index)) {
    //        topFeatures.add(index);
    //      }
    //    }
    //
    ////    Set<Integer> topFeatures = getTopFeatures(newVector, groupVectors, numStdDevs, doUnion);
    ////    Set<Integer> topFeatures = getFeaturesAboveStdDev(newVector, numStdDevs);
    //

    // Intersection
    Set<Integer> topFeatures = featuresForGroup;
    topFeatures.retainAll(featuresForNewDoc);

    // Boost the top features
    boostFeatures(topFeatures);
  }

  //  public Map<GraphVertex, GraphVertex> getNearestVertex(final Collection<GraphVertex> verticies) {
  //
  //    Map<GraphVertex, GraphVertex> childNeighbor = new HashMap<GraphVertex, GraphVertex>();
  //
  //    Set<Integer> vertexIndicies = graphStrategy.getIndicesForVertices(verticies);
  //
  //    for (GraphVertex vertex : verticies) {
  //
  //      // Get this vertex's row id
  //      int vertexRowID = graphStrategy.getRowIdForVertex(vertex);
  //
  //      // Track nearest vertex and distance
  //      int nearestvertexRowID = -1;
  //      double minDist = Double.MAX_VALUE;
  //
  //      // Get this row's vector
  //      double[] vector = weightedVectorSpaceMatrix.getRow(vertexRowID);
  //
  //      // Look for the nearest neighbor not in the incoming vertex list
  //      for (int i = 0; i < weightedVectorSpaceMatrix.getRowDimension(); i++) {
  //        if (i != vertexRowID && vertexIndicies.contains(i) == false) {
  //
  //          // Track nearest vertex and distance
  //          double dist = distFunc.distanceBetween(vector, weightedVectorSpaceMatrix.getRow(i));
  //          if (dist < minDist) {
  //            nearestvertexRowID = i;
  //            minDist = dist;
  //          }
  //        }
  //      }
  //
  //      childNeighbor.put(vertex, graphStrategy.getVertexForRowId(nearestvertexRowID));
  //    }
  //
  //    return childNeighbor;
  //  }
  //
  //  /**
  //   * @param vertex
  //   * @return the nearest neighbors of the vertex
  //   */
  //  public Set<GraphVertex> getNeighbors(final GraphVertex vertex) {
  //    Set<Integer> set = getNeighbors(graphStrategy.getRowIdForVertex(vertex));
  //    return graphStrategy.getVerticesForIndices(set);
  //  }
  //
  //  private Set<Integer> getNeighbors(final int vertIndex) {
  //    GraphVertex centroid = groupStrategy.getCentroid(graphStrategy.getVertexForRowId(vertIndex));
  //
  //    // This can be null if the item is not in a group at all
  //    if ( centroid == null )
  //      return new HashSet<>();
  //
  //    Collection<GraphVertex> neighborVertices = graphStrategy.getGraph().getNeighbors(centroid);
  //    Set<Integer> indicies = graphStrategy.getIndicesForVertices(neighborVertices);
  //
  //    double[] vector = weightedVectorSpaceMatrix.getRow(vertIndex);
  //
  //    double maxDist = Double.MIN_VALUE;
  //    double minDist = Double.MAX_VALUE;
  //    for (int index : indicies) {
  //      if (index != vertIndex) {
  //        double dist = distFunc.distanceBetween(vector, weightedVectorSpaceMatrix.getRow(index));
  //        if (dist > maxDist) {
  //          maxDist = dist;
  //        }
  //        if (dist < minDist) {
  //          minDist = dist;
  //        }
  //      }
  //    }
  //
  //    neighborDistance = minDist + ((maxDist - minDist) / 50);
  //    LOG.debug("max dist = " + maxDist);
  //    LOG.debug("min dist = " + minDist);
  //    LOG.debug("neighborDistance = " + neighborDistance);
  //
  //    Set<Integer> neighbors = new HashSet<Integer>();
  //    for (int i = 0; i < weightedVectorSpaceMatrix.getRowDimension(); i++) {
  //      if (i != vertIndex) {
  //
  //        double dist = distFunc.distanceBetween(vector, weightedVectorSpaceMatrix.getRow(i));
  //        if (dist <= neighborDistance) {
  //          neighbors.add(i);
  //        }
  //      }
  //    }
  //    return neighbors;
  //  }

  /** Increases the feature weights for the given column indexes. */
  private void boostFeatures(final Set<Integer> indicies) {

    //    double count = indicies.size();
    //    double wu = .5 / count;
    //    double wd = .5 / (double) (featureWeights.length - count);

    List<String> columnIdentifiers = this.properties.getColumnIdentifiers();
    for (int index = 0; index < featureWeights.length; index++) {
      if (indicies.contains(index)) {
        double wu = Math.max(previousBoost[index] + boostFactor, boostFactor);
        featureWeights[index] += wu; //boostFactor;
        previousBoost[index] = wu;
        //        System.out.println("Boosted: " + columnIdentifiers.get(index) + " = " + featureWeights[index]);
      } else {
        double wd = Math.min(previousBoost[index] - boostFactor, -boostFactor);
        featureWeights[index] = Math.max(0, featureWeights[index] + wd); //boostFactor);
        previousBoost[index] = wd;
      }
    }

    //    eventBus.post(new FeatureWeightingEvent(featureWeights, columnIdentifiers));
    //    System.out.println("Posted event to bus: " + eventBus);
  }

  private Set<Integer> getFeaturesAboveStdDevThreshold(final RealVector vector) {
    return getFeaturesAboveStdDev(vector, this.numStdDevs);
  }

  /**
   * Gets a vector's features that exceed a computed threshold.
   *
   * @param vector      Vector being analyzed
   * @param numStdDevs  Threshold as number of standard deviations above mean
   *
   * @return Feature (column) indexes
   */
  private Set<Integer> getFeaturesAboveStdDev(final RealVector vector, final double numStdDevs) {
    Set<Integer> topIndicies = new HashSet<Integer>();

    double[] srcArray = vector.toArray();
    double vectMean = StatsUtil.getMean(srcArray);
    double vectSD = StatsUtil.getStandardDeviation(srcArray);
    double vectThreshold = vectMean + (numStdDevs * vectSD);

    //    System.out.println("Mean = " + vectMean + " 1sd = " + vectSD);

    for (int i = 0; i < srcArray.length; i++) {
      if (srcArray[i] >= vectThreshold) {
        topIndicies.add(i);
      }
    }
    return topIndicies;
  }

  //  private Set<Integer> getNonZeroFeatures(final RealVector vector) {
  //
  //    double[] srcArray = vector.toArray();
  //    double vectMean = StatsUtil.getMean(srcArray);
  //    double vectSD = StatsUtil.getStandardDeviation(srcArray);
  //    System.out.println("Mean = " + vectMean + " 1sd = " + vectSD);
  //
  //    return getAboveFeatures(srcArray, vectMean);
  //  }
  //
  //  private Set<Integer> getAboveMeanFeatures(final RealVector vector) {
  //
  //    double[] srcArray = vector.toArray();
  //    double vectMean = StatsUtil.getMean(srcArray);
  //    double vectSD = StatsUtil.getStandardDeviation(srcArray);
  //    System.out.println("Mean = " + vectMean + " 1sd = " + vectSD);
  //
  //    return getAboveFeatures(srcArray, vectMean);
  //  }
  //
  //  private Set<Integer> getAboveFeatures(final double[] srcArray, final double threshold) {
  //    Set<Integer> indicies = new HashSet<Integer>();
  //    for (int i = 0; i < srcArray.length; i++) {
  //      if (srcArray[i] > threshold) {
  //        indicies.add(i);
  //      }
  //    }
  //    return indicies;
  //  }
  //
  //  private Set<Integer> getTopFeatures(final RealVector srcVector, final List<RealVector> group, final double stddev, final boolean doUnion) {
  //    // Get the source vectors top features
  //    Set<Integer> srcTopFeats = getFeaturesAboveStdDev(srcVector, stddev);
  //
  //    // Get the group's top features
  //    Set<Integer> tgtTopFeats = new HashSet<Integer>();
  //    for (RealVector vect : group) {
  //      tgtTopFeats.addAll(getFeaturesAboveStdDev(vect, stddev));
  //    }
  //
  //    Set<Integer> comboFeats = new HashSet<Integer>();
  //    if (doUnion) {
  //      // Union the two sets
  //      comboFeats.addAll(srcTopFeats);
  //      comboFeats.addAll(tgtTopFeats);
  //    } else {
  //      // Intersect the two sets
  //      for (Integer index : srcTopFeats) {
  //        if (tgtTopFeats.contains(index)) {
  //          comboFeats.add(index);
  //        }
  //      }
  //    }
  //
  //    return comboFeats;
  //  }

  /**
   * Computes the weights for all documents.
   *
   * @return Map of doc ID to weight (0 to 3).
   */
  public Map<String, Integer> computeDocumentWeights() {
    // Get the boosted norms for all documents
    double[] weightedVectorNorms = getWeightedVectorNorms();
    int docCount = weightedVectorNorms.length;

    // Compute the quartiles for the boosted norms
    double[] quartiles = StatsUtil.getQuantiles(weightedVectorNorms, 4);

    // For each document
    documentWeights.clear();
    for (int i = 0; i < docCount; i++) {
      String docId = toDocId(i);

      // Use the quartile of the boosted norm
      double norm = weightedVectorNorms[i];
      int weight = findQuartile(norm, quartiles);
      documentWeights.put(docId, weight);
    }

    return documentWeights;
  }

  /** Converts a matrix row index to a doc ID. */
  private String toDocId(final int rowIndex) {
    String docId = properties.getRowIdentifier(rowIndex);
    return docId;
  }

  /** Converts a doc ID to a matrix row index. */
  private int toMatrixRowIndex(final String docId) {
    int rowIndex = properties.getRowIndex(docId);
    return rowIndex;
  }

  /**
   * Gets the previously computed weights for all documents.
   *
   * @return Unmodifiable map of doc ID to weight (0 to 3).
   */
  public Map<String, Integer> getDocumentWeights() {
    return Collections.unmodifiableMap(documentWeights);
  }

  /**
   * Finds the quartile (0 to 3) that a score falls into.
   *
   * <p> In the degenerate case where the quartiles contain a duplicate that
   * the score matches, the first of the duplicates will be reported.
   *
   * @param score      Score to assign to a quartile
   * @param quartiles  Quartiles obtained from StatsUtil.getQuantiles()
   */
  private int findQuartile(final double score, final double[] quartiles) {
    for (int i = 1; i <= 3; i++) {
      if (score < quartiles[i]) {
        return i - 1;
      }
    }
    return 3;
  }
}


































