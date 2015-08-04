/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import gov.pnnl.jac.collections.TwoWayHashMap;
import gov.pnnl.jac.collections.TwoWayMap;
import gov.pnnl.jac.io.EncodingDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * @author hamp645
 *
 */
public class InspireTextMatrixInitializer implements MatrixInitializer {

  /** The location of the text features */
  private final File textFeaturesDirectory;

  /** The vertex type for these things */
  private VertexType vertexType;

  /**
   * @param textFeaturesDirectory
   * @param type
   */
  public InspireTextMatrixInitializer(final File textFeaturesDirectory, final VertexType type) {
    this.textFeaturesDirectory = textFeaturesDirectory;
    vertexType = type;
  }

  @Override
  public MatrixProperties initialize() {
    List<String> rowIdentifiers = loadDocIdentifiers();
    RealMatrix matrix = loadDocMatrix();

    List<String> featureIdentifiers = loadFeatureLabels(matrix);
    TwoWayMap<Integer, String> rowIdentifierMap = buildIdentiferMap(rowIdentifiers);
    TwoWayMap<Integer, String> columnIdentifierMap = buildIdentiferMap(featureIdentifiers);
    Map<Integer, VertexType> typeMap = buildTypeMap(rowIdentifiers);

    MatrixProperties properties = new MatrixProperties();
    properties.setMatrix(matrix);
    properties.setRowIdentifierMap(rowIdentifierMap);
    properties.setColumnIdentifierMap(columnIdentifierMap);
    properties.setVertexTypeMap(typeMap);

    return properties;
  }

  /**
   * @param vertexType
   *          the vertexType to set
   */
  public void setVertexType(final VertexType vertexType) {
    this.vertexType = vertexType;
  }

  /**
   * @param identifiers
   * @return the type
   */
  private Map<Integer, VertexType> buildTypeMap(final List<String> identifiers) {
    Map<Integer, VertexType> map = new HashMap<>();
    for (int i = 0; i < identifiers.size(); i++) {
      map.put(i, vertexType);
    }
    return map;
  }

  /**
   * @param docIdentifiers
   * @return
   */
  private TwoWayMap<Integer, String> buildIdentiferMap(final List<String> docIdentifiers) {
  	TwoWayMap<Integer, String> identifierMap = new TwoWayHashMap<Integer, String>();
    int id = 0;
    for (String identifier : docIdentifiers) {
      identifierMap.associate(id, identifier);
      id++;
    }
    return identifierMap;
  }

  private List<String> loadDocIdentifiers() {
    File[] uidFiles = textFeaturesDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(".coll");
      }
    });

    if (uidFiles.length == 1) {
      return loadCollectionFileDocIdentifiers(uidFiles[0]);
    }

    uidFiles = textFeaturesDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(".uid");
      }
    });

    if (uidFiles.length == 1) {
      return loadUIDFileDocIdentifiers(uidFiles[0]);
    }

    return null;
  }

  private List<String> loadUIDFileDocIdentifiers(final File recordsFile) {
    List<String> docIdentifiers = new ArrayList<String>();

    try {
      String line = "";
      BufferedReader reader = new BufferedReader(new FileReader(recordsFile));
      while ((line = reader.readLine()) != null) {
        docIdentifiers.add(line);
      }
      reader.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return docIdentifiers;
  }

  public static List<String> loadCollectionFileDocIdentifiers(final File recordsFile) {
    List<String> docIdentifiers = new ArrayList<String>();

    try {
    	EncodingDetector det = new EncodingDetector(recordsFile);
      String sEncoding = det.getEncoding();

      // Create a vertex for each document in the file and maps records to file locations
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(recordsFile), sEncoding));
      String line = null;
      reader.readLine();
      reader.readLine();
      reader.readLine();
      reader.readLine();
      reader.readLine();
      reader.readLine();
      while ((line = reader.readLine()) != null) {
        line = reader.readLine();
        reader.readLine();
        reader.readLine();
        reader.readLine();

        String values[] = line.split("\\\\");
        String newValue = values[values.length - 2] + "/" + values[values.length - 1];
        docIdentifiers.add(newValue);
      }
      reader.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return docIdentifiers;
  }

  private RealMatrix loadDocMatrix() {
    RealMatrix docMatrix = null;
    try {
      File[] vectFiles = textFeaturesDirectory.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
          return name.endsWith(".vect.txt");
        }
      });

      File vectFile = null;
      if (vectFiles.length == 1) {
        vectFile = vectFiles[0];
      }

      if (vectFile != null && vectFile.exists()) {

        // Read in the document vectors
        BufferedReader reader = new BufferedReader(new FileReader(vectFile));

        reader.readLine();
        reader.readLine();
        String[] dims = reader.readLine().split(" ");

        int rowCount = Integer.parseInt(dims[0]);
        int colCount = Integer.parseInt(dims[1]);

        docMatrix = new Array2DRowRealMatrix(rowCount, colCount);

        String line = null;
        int row = 0;
        while ((line = reader.readLine()) != null) {
          String[] values = line.split(" ");
          for (int col = 1; col < values.length; col++) {
            docMatrix.setEntry(row, col - 1, Double.parseDouble(values[col]));
          }
          row++;
        }
        reader.close();
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return docMatrix;
  }

  private List<String> loadFeatureLabels(final RealMatrix matrix) {
    List<String> featuresList = new ArrayList<String>();

    File[] majorsFile = textFeaturesDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(".major");
      }
    });

    File[] vocabFile = textFeaturesDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(".vocab");
      }
    });

    try {
      if (majorsFile.length == 1 && vocabFile.length == 1) {
        // Load vocab
        Map<String, String> vocab = new HashMap<String, String>();

      	EncodingDetector det = new EncodingDetector(vocabFile[0]);
        String sEncoding = det.getEncoding();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(vocabFile[0]), sEncoding));
        reader.readLine();
        reader.readLine();
        reader.readLine(); // Skip the header
        String line = "";
        while ((line = reader.readLine()) != null) {
          String[] vals = line.split(" ", 2);
          vocab.put(vals[0], vals[1]);
        }
        reader.close();

        // Load majors
        reader = new BufferedReader(new FileReader(majorsFile[0]));
        reader.readLine();
        reader.readLine();
        reader.readLine(); // Skip the header
        while ((line = reader.readLine()) != null) {
          String[] vals = line.split("\\s+");
          if (vals[3].equals("T")) {
            String topic = vocab.get(vals[0]);
            featuresList.add(topic);
          }
        }
        reader.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // If no file, just make the label the columnNumber
    if ( featuresList.isEmpty()) {
      for (int i = 0; i < matrix.getColumnDimension(); i++) {
        featuresList.add("t-" + Integer.toString(i));
      }
    }
    return featuresList;
  }
}
