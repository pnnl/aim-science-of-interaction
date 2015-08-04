package gov.pnnl.aim.discovery.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class ClusterUtil {

  Map<Integer, String> docIdMap;

  public ClusterUtil(final Map<Integer, String> dim) {
    docIdMap = dim;
  }

  public ClusterUtil() {
  }

  public enum DataType {
    TEXT, IMAGE
  };

  public enum MatrixMergeStrategy {
    ZEROED, PARENTCHILD, SIMILARITY, DUAL_CLUSTERING;

    @Override
    public String toString() {
      String msg = "";
      switch (this) {
        case ZEROED:
          msg = "Non-type features are zeroed out";
          break;
        case PARENTCHILD:
          msg = "Non-type features are drawn from sibling elements, if available";
          break;
        case SIMILARITY:
          msg = "Non-type features are based on sibling elements and per-element similarity";
          break;
        case DUAL_CLUSTERING:
          msg = "Images and Text are clustered separately, then merged based on parent-child relationships";
          break;
        default:
          msg = "Unknown strategy selected!";
      }
      return msg;
    }

    public String getName() {
      String msg = "";
      switch (this) {
        case ZEROED:
          msg = "Zeroed";
          break;
        case PARENTCHILD:
          msg = "Parent-Child";
          break;
        case SIMILARITY:
          msg = "Similarity";
          break;
        case DUAL_CLUSTERING:
          msg = "Dual-Clustering";
          break;
        default:
          msg = "Unknown";
      }
      return msg;
    }

  };

  private DataType getFileType(final String fname) {
    DataType type = null;

    int ext_pos = fname.lastIndexOf('.') + 1;
    String ext = fname.substring(ext_pos);

    if (ext.equals("txt")) {
      type = DataType.TEXT;
    } else if (ext.equals("jpg") || ext.equals("png") || ext.equals("jpeg")) {
      type = DataType.IMAGE;
    } else {
      System.out.println("Weird extension: " + ext);
    }

    return type;
  }

  private RealVector extractImageVector(final RealVector v) {
    return v.getSubVector(200, 500);
  }

  private RealVector extractTextVector(final RealVector v) {
    return v.getSubVector(0, 200);
  }

  // create an index: String (parent) --> int[] (list of child elements)
  private Map<String, List<Integer>> createParentChildIndex() {
    Map<String, List<Integer>> index = new HashMap<String, List<Integer>>();
    for (Map.Entry<Integer, String> e : docIdMap.entrySet()) {
      String parent = (e.getValue()).split("/")[0];
      // System.out.println("Parent: " + parent);

      List<Integer> ids = new ArrayList<Integer>();
      if (index.containsKey(parent)) {
        ids = index.get(parent);
      }
      ids.add(e.getKey());
      index.put(parent, ids);
    }

    return index;
  }

  // TODO: make this more efficient
  private List<Integer> getTopSimilarElements(final RealVector a, final RealMatrix candidates) {
    List<Integer> results = new ArrayList<Integer>();

    Map<Double, Integer> resultsMap = new HashMap<Double, Integer>();
    for (int i = 0; i < candidates.getRowDimension(); ++i) {
      double sim = a.getDistance(candidates.getRowVector(i));
      resultsMap.put(sim, i);
    }

    SortedSet<Double> keys = new TreeSet<Double>(resultsMap.keySet());
    // for( Double d : keys ) {
    // System.out.println("Similarity: " + d + " for element: " + resultsMap.get(d));
    // }

    SortedSet<Double> top = keys.subSet(0.00000001, 0.25);
    for (Double d : top) {
      results.add(resultsMap.get(d));
    }

    return results;
  }

  private RealVector computeAverageVector(final RealMatrix mat) {
    RealVector res = new ArrayRealVector(mat.getColumnDimension());

    for (int i = 0; i < mat.getRowDimension(); i++) {
      res.add(mat.getRowVector(i));
    }

    res.mapDivide(mat.getColumnDimension());

    return res;
  }

  // Essentially the same as computeParentChildFeatureSpace, but the images that don't have
  // a text sibling get their text features from the images that do which are most closely related
  // (visually) to them
  private RealMatrix computeSimilarityBasedFeatureSpace(final int docRows, final int docCols, final int totalRows, final int totalCols, final RealMatrix docsMatrix, final RealMatrix imagesMatrix) {
    RealMatrix mat = new Array2DRowRealMatrix(totalRows, totalCols);

    // Copy the docs matrix and images matrix into the unified matrix
    mat.setSubMatrix(docsMatrix.getData(), 0, 0);
    mat.setSubMatrix(imagesMatrix.getData(), docRows, docCols);

    List<Integer> orphanElements = new ArrayList<Integer>();
    Map<String, List<Integer>> index = createParentChildIndex();
    for (Map.Entry<String, List<Integer>> e : index.entrySet()) {
      if (e.getValue().size() == 1) {
        orphanElements.add(e.getValue().get(0));
      }
    }

    System.out.println("Number of orphan elements: " + orphanElements.size());

    for (Integer i : orphanElements) {
      String fname = docIdMap.get(i);
      // System.out.println("Orphan: " + fname);
      DataType entry_type = getFileType(fname);

      RealVector v;
      if (entry_type == DataType.TEXT) {
        v = docsMatrix.getRowVector(i);
        RealVector imgVect = new ArrayRealVector(500);
        System.out.println("Computing similar elements for: " + fname);
        List<Integer> topResults = getTopSimilarElements(v, docsMatrix);

        if (topResults.size() == 0)
          continue;
        // TODO
        // I don't know how to dynamically add to the Matrix, so I'll do this for now
        List<RealVector> composite = new ArrayList<RealVector>();
        for (int r = 0; r < topResults.size(); r++) {
          System.out.println("Similar text element: " + topResults.get(r));
          String similarName = docIdMap.get(topResults.get(r));
          String[] tokens = similarName.split("/");
          String parent = tokens[0];
          List<Integer> siblings = index.get(parent);
          System.out.println("Found " + siblings.size() + " sibling elements");

          for (Integer s : siblings) {
            if (s == topResults.get(r)) {
              System.out.println("Skipping ourselves");
              continue;
            }

            DataType sibling_type = getFileType(docIdMap.get(s));

            if (sibling_type == DataType.IMAGE) {
              RealVector img_v = extractImageVector(mat.getRowVector(s));
              composite.add(img_v);
            }
          }
          for (int idx = 0; idx < composite.size(); idx++) {
            imgVect.add(composite.get(idx));
          }
          imgVect.mapDivideToSelf(composite.size());
          RealVector row = mat.getRowVector(i);
          row.setSubVector(200, imgVect);
          mat.setRowVector(i, row);
        }

      } else if (entry_type == DataType.IMAGE) {
        v = imagesMatrix.getRowVector(i - docRows);
        RealVector txtVect = new ArrayRealVector(200);
        System.out.println("Computing similar elements for: " + fname);
        List<Integer> topResults = getTopSimilarElements(v, imagesMatrix);

        if (topResults.size() == 0)
          continue;
        // TODO
        // I don't know how to dynamically add to the Matrix, so I'll do this for now
        List<RealVector> composite = new ArrayList<RealVector>();
        for (int r = 0; r < topResults.size(); r++) {
          System.out.println("Similar image element: " + topResults.get(r));
          System.out.println("Similar text element: " + topResults.get(r));
          String similarName = docIdMap.get(topResults.get(r));
          String[] tokens = similarName.split("/");
          String parent = tokens[0];
          List<Integer> siblings = index.get(parent);
          System.out.println("Found " + siblings.size() + " sibling elements");

          for (Integer s : siblings) {
            if (s == topResults.get(r)) {
              System.out.println("Skipping ourselves");
              continue;
            }

            DataType sibling_type = getFileType(docIdMap.get(s));

            if (sibling_type == DataType.TEXT) {
              RealVector txt_v = extractTextVector(mat.getRowVector(s));
              composite.add(txt_v);
            }
          }
          for (int idx = 0; idx < composite.size(); idx++) {
            txtVect.add(composite.get(idx));
          }
          txtVect.mapDivideToSelf(composite.size());
          RealVector row = mat.getRowVector(i);
          row.setSubVector(0, txtVect);

          mat.setRowVector(i, row);
        }
      } else {
        System.out.println("????");
      }
    }

    return mat;
  }

  // For images that are siblings with text, assign the text portion of the feature vector
  // to be the same as the sibling document
  private RealMatrix computeParentChildFeatureSpace(final int docRows, final int docCols, final int totalRows, final int totalCols, final RealMatrix docsMatrix, final RealMatrix imagesMatrix) {
    RealMatrix mat = new Array2DRowRealMatrix(totalRows, totalCols);
    System.out.println("Number of documents: " + docRows);
    System.out.println("Number of images: " + (totalRows - docRows));

    // Copy the docs matrix and images matrix into the unified matrix
    mat.setSubMatrix(docsMatrix.getData(), 0, 0);
    mat.setSubMatrix(imagesMatrix.getData(), docRows, docCols);

    System.out.println("Matrix num columns: " + mat.getColumnDimension());
    System.out.println("Matrix num rows: " + mat.getRowDimension());

    Map<String, List<Integer>> index = createParentChildIndex();

    int cntNoSibs = 0;
    RealVector vect;
    for (Map.Entry<Integer, String> e : docIdMap.entrySet()) {
      String name = e.getValue();
      String[] tokens = name.split("/");
      String parent = tokens[0];
      String file_name = tokens[1];

      DataType element_type = getFileType(name);

      if (element_type == DataType.TEXT) {
        vect = new ArrayRealVector(500);
      } else {
        vect = new ArrayRealVector(200);
      }

      List<Integer> siblings = index.get(parent);
      if (siblings.size() == 1) {
        ++cntNoSibs;
        continue;
      }
      int num_sibs = 0;
      for (Integer i : siblings) {
        if (i == e.getKey()) {
          // System.out.println("Skipping ourselves");
          continue;
        }

        DataType sibling_type = getFileType(docIdMap.get(i));

        if (sibling_type == element_type) {
          continue;
        } else {
          if (element_type == DataType.TEXT) {
            RealVector v = extractImageVector(mat.getRowVector(i));
            vect = vect.add(v);
            ++num_sibs;
          } else if (element_type == DataType.IMAGE) {
            RealVector v = extractTextVector(mat.getRowVector(i));
            vect = vect.add(v);
            ++num_sibs;
          }
        }

      }
      vect.mapDivideToSelf(num_sibs);
      RealVector composite = mat.getRowVector(e.getKey());
      if (element_type == DataType.TEXT) {
        composite.setSubVector(200, vect);
      } else if (element_type == DataType.IMAGE) {
        composite.setSubVector(0, vect);
      }
      mat.setRowVector(e.getKey(), composite);

    }

    return mat;
  }

  // Zero out the piece of the feature vector of the other type, ie.
  // an images will have zeros for all the values in the text portion of the
  // feature vector, and visa versa
  private RealMatrix computeZeroedFeatureSpace(final int docRows, final int docCols, final int totalRows, final int totalCols, final RealMatrix docsMatrix, final RealMatrix imagesMatrix) {
    RealMatrix mat = new Array2DRowRealMatrix(totalRows, totalCols);

    // Copy the docs matrix and images matrix into the unified matrix
    if (docsMatrix != null) {
      mat.setSubMatrix(docsMatrix.getData(), 0, 0);
    }
    if (imagesMatrix != null) {
      mat.setSubMatrix(imagesMatrix.getData(), docRows, docCols);
    }

    return mat;
  }

  /**
   * @param strategy
   * @param docsMatrix
   * @param imagesMatrix
   * @return the merged matrix
   */
  public RealMatrix mergeFeatureSpace(final MatrixMergeStrategy strategy, final RealMatrix docsMatrix, final RealMatrix imagesMatrix) {
    int docRows = (docsMatrix != null) ? docsMatrix.getRowDimension() : 0;
    int docCols = (docsMatrix != null) ? docsMatrix.getColumnDimension() : 0;

    int imgRows = (imagesMatrix != null) ? imagesMatrix.getRowDimension() : 0;
    int imgCols = (imagesMatrix != null) ? imagesMatrix.getColumnDimension() : 0;

    int totalRows = docRows + imgRows;
    int totalCols = docCols + imgCols;

    System.out.println("Using strategy: " + strategy);
    RealMatrix mat = new Array2DRowRealMatrix();
    switch (strategy) {
      case ZEROED:
        mat = computeZeroedFeatureSpace(docRows, docCols, totalRows, totalCols, docsMatrix, imagesMatrix);
        break;
      case PARENTCHILD:
        mat = computeParentChildFeatureSpace(docRows, docCols, totalRows, totalCols, docsMatrix, imagesMatrix);
        break;
      case SIMILARITY:
        mat = computeSimilarityBasedFeatureSpace(docRows, docCols, totalRows, totalCols, docsMatrix, imagesMatrix);
        break;
      case DUAL_CLUSTERING:
        break;
      default:
        System.err.println("Unrecognized feature fusion strategy!");
        break;
    }
    return mat;
  }

}
