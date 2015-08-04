/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import gov.pnnl.jac.collections.TwoWayMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * @author hamp645
 */
public class MatrixProperties implements Serializable {
  // For Serializable
  private static final long serialVersionUID = -3696130913000402962L;

  /** The matrix */
  private RealMatrix matrix;

  /** Map of ids to identifiers for a row */
  private TwoWayMap<Integer, String> rowIdentifierMap;

  /** Map of ids to identifiers for a column */
  private TwoWayMap<Integer, String> columnIdentifierMap;

  /** Maps a row id to the type it should be */
  private Map<Integer, VertexType> vertexTypeMap;
  
  
  public MatrixProperties() {
  }
  
  /**
   * @return the matrix
   */
  public RealMatrix getMatrix() {
    return matrix;
  }

  /**
   * @param matrix
   *          the matrix to set
   */
  public void setMatrix(final RealMatrix matrix) {
    this.matrix = matrix;
  }

  /** Gets the number of rows in the matrix. */
  public int getRowCount() {
    int rows = matrix.getRowDimension();
    return rows;
  }
  
  /** Gets the number of columns in the matrix. */
  public int getColumnCount() {
    int columns = matrix.getColumnDimension();
    return columns;
  }
  
  /**
   * @return the identifiers
   */
  public List<String> getRowIdentifiers() {
    List<Integer> keys = new ArrayList<>(rowIdentifierMap.forwardKeySet());
    Collections.sort(keys);

    List<String> ids = new ArrayList<>();
    for (int key : keys) {
      ids.add(rowIdentifierMap.getForward(key));
    }

    return ids;
  }

  /**
   * @return the identifiers
   */
  public List<String> getColumnIdentifiers() {
    List<Integer> keys = new ArrayList<>(columnIdentifierMap.forwardKeySet());
    Collections.sort(keys);

    List<String> ids = new ArrayList<>();
    for (int key : keys) {
      ids.add(columnIdentifierMap.getForward(key));
    }

    return ids;
  }

  /**
   * @return the identifierMap
   */
  public TwoWayMap<Integer, String> getRowIdentifierMap() {
    return rowIdentifierMap;
  }

  /**
   * @param identifierMap
   *          the identifierMap to set
   */
  public void setRowIdentifierMap(final TwoWayMap<Integer, String> identifierMap) {
    this.rowIdentifierMap = identifierMap;
  }

  /**
   * @return the columnIdentifierMap
   */
  public TwoWayMap<Integer, String> getColumnIdentifierMap() {
    return columnIdentifierMap;
  }

  /**
   * @param columnIdentifierMap
   *          the columnIdentifierMap to set
   */
  public void setColumnIdentifierMap(final TwoWayMap<Integer, String> columnIdentifierMap) {
    this.columnIdentifierMap = columnIdentifierMap;
  }

  /**
   * @return the vertexTypeMap
   */
  public Map<Integer, VertexType> getVertexTypeMap() {
    return vertexTypeMap;
  }

  /**
   * @param vertexTypeMap
   *          the vertexTypeMap to set
   */
  public void setVertexTypeMap(final Map<Integer, VertexType> vertexTypeMap) {
    this.vertexTypeMap = vertexTypeMap;
  }

  /** Gets the row index corresponding to a row (document) identifier String. */
  public int getRowIndex(String rowID) {
    return this.rowIdentifierMap.getReverse(rowID);
  }
  
  /** Gets the row (document) ID corresponding to a row index. */
  public String getRowIdentifier(int rowIndex) {
      return rowIdentifierMap.getForward(rowIndex);
  }
  
  /** 
   * Gets the vector for a given row (document) ID. The result is *not* 
   * guaranteed to be a copy.
   */
  public double[] getVector(String rowID) {
      int index = getRowIndex(rowID);
      double[] vector = getVector(index);
      
      return vector;
  }
  
  /** 
   * Gets the vector for a given row index. The result is *not* guaranteed to
   * be a copy.
   */
  public double[] getVector(int rowIndex) {
      double[] vector = matrix.getRow(rowIndex);
      return vector;
  }
  
  /** 
   * Gets the vector for a given row index. Most callers should call 
   * 
   * @deprecated 
   * Most callers should use {@link #getVector(int)} to get a double[] instead.
   * This method is intended for reduce the need for changes in existing code.
   */
  public RealVector getRowVector(int rowIndex) {
      RealVector vector = matrix.getRowVector(rowIndex);
      return vector;
  }
  
  /** 
   * Adds documents to the matrix, resizing it. 
   * 
   * @param docIdList      List of document ID Strings
   * @param docVectorList  List of document vectors (parallel to IDs)
   * @param vertexType     Type of vertexes (typically AIMDataType.TEXT)
   */
  public void addDocuments(List<String> docIdList, List<double[]> docVectorList, VertexType vertexType) {
      // Check args
      int rowsAdded = docIdList.size();
      if (rowsAdded != docVectorList.size()) {
          throw new IllegalArgumentException("List sizes don't match");
      }
      
      // Allocate bigger matrix
      int rowsBefore = matrix.getRowDimension();
      int columns = matrix.getColumnDimension();
      int rowsAfter = rowsBefore + rowsAdded;
      RealMatrix bigger = new Array2DRowRealMatrix(rowsAfter, columns);
      
      // Copy the existing data
      double[][] data = matrix.getData();
      bigger.setSubMatrix(data, 0, 0);
      
      // For each new document
      for (int i = 0; i < rowsAdded; i++) {
          String docId = docIdList.get(i);
          double[] docVector = docVectorList.get(i);
          if (docVector.length != columns) {
              throw new IllegalArgumentException("Vector and matrix column dimension don't match");
          }
          
          // Add ID and vector
          int index = rowsBefore + i;
          rowIdentifierMap.associate(index, docId);
          bigger.setRow(index, docVector);
          
          // Add type
          vertexTypeMap.put(index, vertexType);
      }
      
      matrix = bigger;      
  }
}


























