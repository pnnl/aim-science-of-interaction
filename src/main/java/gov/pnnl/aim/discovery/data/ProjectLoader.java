package gov.pnnl.aim.discovery.data;

import gov.pnnl.aim.discovery.pojo.JsonCluster;
import gov.pnnl.aim.discovery.pojo.JsonClusterMember;
import gov.pnnl.aim.discovery.pojo.JsonOutput;
import gov.pnnl.jac.cluster.Cluster;
import gov.pnnl.jac.cluster.ClusterList;
import gov.pnnl.jac.collections.TwoWayMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class ProjectLoader {

  private String path = "/data";

  public ProjectLoader() {

  }

  public ProjectLoader(final String path) {
    this.path = path;
  }

  public List<String> getProjectList() {

    final File file = new File(ProjectLoader.class.getResource(path).getFile());

    List<String> projectNames = new ArrayList<String>();
    for (File f : file.listFiles()) {

      if (f.isDirectory()) {
        projectNames.add(f.getName());
      }
    }

    return projectNames;
  }

  public List<JsonOutput> getProjectObjects(final String projectName) {

    List<JsonOutput> projectObjs = new ArrayList<JsonOutput>();

    // Load the data
    final String path = "/data/" + projectName +"/TextFeatures";
    final URL resURL = ProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      InspireTextMatrixInitializer in = new InspireTextMatrixInitializer(file, AIMDataType.TEXT);
      MatrixProperties props = in.initialize();

      SDIClusteringStrategy clusterStrategy = new SDIClusteringStrategy(props);
      ClusterList clusterList = clusterStrategy.cluster();

      TwoWayMap<Integer, String> rows = props.getRowIdentifierMap();

      RealMatrix matrix = clusterStrategy.getTwoSpaceMatrix();
      int clusterCount = clusterList.getClusterCount();
      for (int i = 0; i < clusterCount; i++) {

        double x = matrix.getColumn(i)[0];
        double y = matrix.getColumn(i)[1];

        Cluster cluster = clusterList.getCluster(i);
        int [] members = cluster.getMembership();

        for (int j = 0; j < members.length; j++) {

          int rowID = members[j];
          String docID = props.getRowIdentifiers().get(rowID);
          String docText = docID;
          try {
            docText = getDocTextSnippet(projectName, docID);
          } catch (Exception e) {
            e.printStackTrace();
          }

          JsonOutput text = new JsonOutput();
          text.setId(docID);
          text.setType("text");
          text.setValue(docText);
          text.setRelX(x + ((.1 * Math.random()) - .05));
          text.setRelY(y + ((.1 * Math.random()) - .05));

          projectObjs.add(text);
        }

      }
    }

    return projectObjs;
  }

  public List<JsonCluster> getClusterMembership(final String projectName, final Map<Integer, String> labelMap) {
    List<JsonCluster> clusters = new ArrayList<>();

    // Load the data
    final String path = "/data/" + projectName +"/TextFeatures";
    final URL resURL = ProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      InspireTextMatrixInitializer in = new InspireTextMatrixInitializer(file, AIMDataType.TEXT);
      MatrixProperties props = in.initialize();

      int vectorLength = props.getColumnCount();

      SDIClusteringStrategy clusterStrategy = new SDIClusteringStrategy(props);
      ClusterList clusterList = clusterStrategy.cluster();

      RealMatrix matrix = clusterStrategy.getTwoSpaceMatrix();
      int clusterCount = clusterList.getClusterCount();
      for (int i = 0; i < clusterCount; i++) {

        double x = matrix.getColumn(i)[0];
        double y = matrix.getColumn(i)[1];

        Cluster cluster = clusterList.getCluster(i);
        int [] members = cluster.getMembership();

        JsonCluster jsonCluster = new JsonCluster(i, x, y, labelMap.get(i));
        jsonCluster.setCentroid(new double[vectorLength]);

        for (int j = 0; j < members.length; j++) {

          int rowID = members[j];
          String docID = props.getRowIdentifiers().get(rowID);
          String docText = docID;
          try {
            docText = getDocTextSnippet(projectName, docID);
          } catch (Exception e) {
            e.printStackTrace();
          }

          JsonClusterMember member = new JsonClusterMember(docID, "", docText, i);
          jsonCluster.add(member);
        }
        clusters.add(jsonCluster);
      }
    }

    return clusters;
  }

  public String getDocTextSnippet(final String projectName, final String docID) throws FileNotFoundException, IOException {
    String docText = getDocText(projectName, docID);
    if (docText.length() > 100) {
      docText = docText.substring(0, 100);
    }
    return docText;
  }

  public String getDocText(final String projectName, final String docID) throws FileNotFoundException, IOException {

    final String path = "/data/" + projectName +"/content/" + docID;
    final URL resURL = ProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      String docText = IOUtils.toString(new FileInputStream(file), "UTF-8");
      return docText;
    }
    return docID;
  }

  /** Gets the features for the project. */
  public List<String> getFeatureLabels(final String projectName) {
    // Load the data
    final String path = "/data/" + projectName +"/TextFeatures";
    final URL resURL = ProjectLoader.class.getResource(path);

    if (resURL == null) {
      return Collections.emptyList();
    }

    // Note: In order to reuse code, this performs the matrix initialization
    // again, and we throw away most of it. With a little refactoring, this
    // could easily be made more efficient, but it's fast enough for now.
    final File file = new File(resURL.getFile());
    InspireTextMatrixInitializer in = new InspireTextMatrixInitializer(file, AIMDataType.TEXT);
    MatrixProperties props = in.initialize();

    List<String> features = props.getColumnIdentifiers();
    return features;
  }
}
