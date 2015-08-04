package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.discovery.CanvasConfig;
import gov.pnnl.aim.discovery.data.AIMDataType;
import gov.pnnl.aim.discovery.data.InspireTextMatrixInitializer;
import gov.pnnl.aim.discovery.data.MatrixProperties;
import gov.pnnl.aim.discovery.data.StatefulProjectLoader;
import gov.pnnl.aim.discovery.pojo.JsonCluster;
import gov.pnnl.aim.discovery.pojo.JsonClusterMember;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/** Class for managing user study data increments. */
public class UserStudyDocumentManager {
  private int incNumber = 0;

  private StatefulProjectLoader loader = null;

  /** Map of document IDs to cluster IDs, for the increment. */
  private Map<String, Integer> clusterAssignments = new HashMap<String, Integer>();
  
  
  public UserStudyDocumentManager(final int incNumber, final StatefulProjectLoader loader) {
    this.incNumber = incNumber;
    this.loader = loader;
  }

  public List<JsonCluster> loadNextIncrement() {
    incNumber++;
    
    final String pathColl = "/data/" + CanvasConfig.PROJECT_NAME + "/results/inc" + incNumber + "/00000000.coll";
    final URL resURLColl = StatefulProjectLoader.class.getResource(pathColl);
    if (resURLColl == null) {
      // Increment doesn't exist; just return current data
      System.out.printf("Couldn't find increment %d file: %s%n", incNumber, pathColl);
      return loader.getClusterMembership();
    }

    loader.setIsChanging(true);
    loader.setCurrentIncrement(incNumber);
    
    final File fileColl = new File(resURLColl.getFile());

    List<String> docIdentifiers = InspireTextMatrixInitializer.loadCollectionFileDocIdentifiers(fileColl);

    final String path = "/data/" + CanvasConfig.PROJECT_NAME + "/results/inc" + incNumber + "/00000000.vect.txt";
    final URL resURL = StatefulProjectLoader.class.getResource(path);
    final File file = new File(resURL.getFile());

    try {
      // Read all the new vectors in to memory
      List<String> lines = IOUtils.readLines(new FileInputStream(file), "UTF-8");

      // Iterate over each vector
      List<double[]> docVectorArrays = new ArrayList<double[]>();
      List<List<Double>> docVectorList = new ArrayList<List<Double>>();

      for (int i = 3; i < lines.size(); i++) {
        // Split out the vector values, first item is doc sequence number
        String[] values = lines.get(i).split(" ");

        // Make the vector a double array
        double[] vect = new double[values.length - 1];
        docVectorArrays.add(vect);

        List<Double> vectList = new ArrayList<Double>();
        for (int j = 1; j < values.length; j++) {
          vect[j - 1] = Double.parseDouble(values[j]);
          vectList.add(vect[j - 1]);
        }
        docVectorList.add(vectList);
      }

      // Extend the matrix to add these vectors
      MatrixProperties matrixProps = loader.getMatrixProperties();
      matrixProps.addDocuments(docIdentifiers, docVectorArrays, AIMDataType.TEXT);

      clusterAssignments.clear();
      
      // Add the new documents to the queue for the UI to pick up
      for (int i = 0; i < docVectorList.size(); i++) {
        String docID = docIdentifiers.get(i);

        // Assign new doc to cluster
        int clusterID = loader.findNearestCluster(docID);
        JsonClusterMember member = new JsonClusterMember(docID, StatefulProjectLoader.getDocTitle(docID), StatefulProjectLoader.getDocTitle(docID), clusterID);
        loader.addDocToCluster_ASI(member, clusterID);
        clusterAssignments.put(docID, clusterID);
      }

      System.out.println("Loaded up increment: " + incNumber + " with " + docVectorList.size() + " new documents.");

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    loader.setIsChanging(false);
    
    return loader.getClusterMembership();
  }

  /** 
   * Gets the cluster assignments that were made in the increment. 
   * 
   * @return Map of doc ID to cluster ID
   */
  public Map<String, Integer> getIncrementalClusterAssignments() {
      return clusterAssignments;
  }
}
