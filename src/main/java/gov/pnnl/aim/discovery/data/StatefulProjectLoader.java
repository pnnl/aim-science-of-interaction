package gov.pnnl.aim.discovery.data;

import gov.pnnl.aim.discovery.CanvasConfig;
import gov.pnnl.aim.discovery.pojo.Annotation;
import gov.pnnl.aim.discovery.pojo.DocumentHighlight;
import gov.pnnl.aim.discovery.pojo.JsonCluster;
import gov.pnnl.aim.discovery.pojo.JsonClusterMember;
import gov.pnnl.aim.discovery.streams.CanvasStreamer;
import gov.pnnl.aim.discovery.streams.UserStudyDocumentManager;
import gov.pnnl.aim.discovery.util.LuceneIndexBuilder;
import gov.pnnl.aim.discovery.util.StatsUtil;
import gov.pnnl.aim.discovery.util.StringUtil;
import gov.pnnl.jac.cluster.Cluster;
import gov.pnnl.jac.cluster.ClusterList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Keeps the loaded data around.
 *
 * @author hamp645
 */
public class StatefulProjectLoader implements Serializable {
  // For Serializable
  private static final long serialVersionUID = -8710579893033699397L;

  private static String SERIALIZATION_DIRECTORY;

  public final static int UNGROUPED_ID = -1;
  public final static String UNGROUPED = "ungrouped";

  private JsonCluster ungrouped;

  /** Name of the project */
  private final String projectName;

  /** Map of labels for the loaded clusters */
  final Map<Integer, String> labelMap = new HashMap<>();

  /** The current list of clusters */
  final Map<Integer, JsonCluster> clusters;

  //  int clusterID = 0;


  private SDIClusteringStrategy clusterStrategy = null;
  FeatureWeightingStrategy featureStrategy = null;

  /** The vectors and associated mappings. */
  private MatrixProperties matrixProps;

  private String user;

  private int currentIncrement = 0;
  
  /** Delegate for managing loading of document increments. */
  private transient UserStudyDocumentManager documentManager;

  /** Whether the loader is undergoing a bulk change. */
  private boolean isChanging = false;

  /** Whether the state has changed since the last save. */
  private boolean changed = false;


  /**
   * Constructs a new project object.
   *
   * <p> Now private, called only from our static factory method, loadProject().
   */
  private StatefulProjectLoader(final String projectName) {
    this.projectName = projectName;
    clusters = buildClusterMembership();
    labelMap.put(UNGROUPED_ID, UNGROUPED);
    updateDocumentWeights();
    
    buildIndex();
  }

  private Map<Integer, JsonCluster> buildClusterMembership() {
    Map<Integer, JsonCluster> clusters = new HashMap<Integer, JsonCluster>();

    MatrixProperties props = getMatrixProperties();
    if (props != null) {
      // These are our two major objects to keep state of the vector space and feature weight inference
      clusterStrategy = new SDIClusteringStrategy(props);
      featureStrategy = new FeatureWeightingStrategy(props);

      // How many features are in our vector space?  usually 200
      featureStrategy.init(props.getColumnCount());

      // Cluster the base vector space
      ClusterList clusterList = clusterStrategy.cluster();

      // Convert the clustering results to the POJOs which support the web client
      RealMatrix matrix = clusterStrategy.getTwoSpaceMatrix();
      int clusterCount = clusterList.getClusterCount();
      for (int i = 0; i < clusterCount; i++) {

        double x = matrix.getColumn(i)[0];
        double y = matrix.getColumn(i)[1];

        Cluster cluster = clusterList.getCluster(i);
        int[] members = cluster.getMembership();

        JsonCluster jsonCluster = new JsonCluster(i, x, y, labelMap.get(i));

        // Copy centroid from JAC Cluster to JsonCluster
        double[] center = cluster.getCenter();
        jsonCluster.setCentroid(center);

        for (int j = 0; j < members.length; j++) {

          int rowID = members[j];
          String docID = props.getRowIdentifiers().get(rowID);
          String docText = docID;
          String title = docID;
          try {
            title = getDocTitle(docID);
            docText = getDocTextSnippet(docID);
          } catch (Exception e) {
            e.printStackTrace();
          }

          JsonClusterMember member = new JsonClusterMember(docID, title, docText, i);
          jsonCluster.add(member);
        }

        clusters.put(i, jsonCluster);
      }

      //      clusterID = clusterCount;

      /**
       * This is test code to support outputting files for Alejandro's projects
       */
      //      try {
      //
      //      	String s = "C:\\code\\aim-soi-streaming\\discoveryStreaming\\src\\main\\resources\\data\\" + projectName + "\\TextFeatures";
      //	      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(s, "clusters.csv")));
      //
      //	      writer.write("label,docid");
      //      	for (String kw : props.getColumnIdentifiers()) {
      //        	writer.write(",");
      //        	writer.write(kw);
      //      	}
      //      	writer.write("\n");
      //
      //
      //	      RealMatrix nspaceMatrix = props.getMatrix();
      //	      for (int i = 0; i < clusterCount; i++) {
      //
      //	        Cluster cluster = clusterList.getCluster(i);
      //	        int[] members = cluster.getMembership();
      //	        for (int row : members) {
      //
      //	        	String docID = props.getRowIdentifiers().get(row);
      //	          if (docID.contains("/")) {
      //	          	docID = docID.substring(docID.indexOf("/") + 1);
      //	          }
      //
      //	        	writer.write("cluster-" + i);
      //	        	writer.write("," + docID);
      //	        	double [] vector = nspaceMatrix.getRow(row);
      //	        	for (double val : vector) {
      //		        	writer.write(",");
      //		        	writer.write(Double.toString(val));
      //	        	}
      //	        	writer.write("\n");
      //	        }
      //	      }
      //	      writer.close();
      //      } catch (Exception ex) {
      //      	ex.printStackTrace();
      //      }

      List<String> features = props.getColumnIdentifiers();
      autoLabelClusters(clusters, features);
    }

    ungrouped = new JsonCluster(UNGROUPED_ID, 0, 0, UNGROUPED);
    clusters.put(UNGROUPED_ID, ungrouped);

    return clusters;
  }

  /**
   * Finds the existing cluster nearest to a document, with distance measured
   * in feature-weighted n-space between the cluster centroid and the document
   * vector.
   *
   * <p>This is intended to be used to assign a new document to a cluster. It
   * assumes that the MatrixProperties have been updated for the new document.
   *
   * @return ID of nearest cluster
   */
  public int findNearestCluster(final String docId) {
    // Get the feature-weighted version of the document vector
    double[] vector = matrixProps.getVector(docId);
    double[] weightedVector = featureStrategy.getWeightedVector(vector);

    // Prepare to find nearest cluster
    int bestClusterId = UNGROUPED_ID;
    double bestDistanceSq = Double.POSITIVE_INFINITY;

    // Check each cluster
    for (JsonCluster cluster : clusters.values()) {
      int clusterId = cluster.getClusterId();
      if (clusterId != UNGROUPED_ID) {
        // Get the feature-weighted version of the centroid
        double[] centroid = cluster.getCentroid();
        double[] weightedCentroid = featureStrategy.getWeightedVector(centroid);

        double distanceSq = distanceSquared(weightedCentroid, weightedVector);
        if (bestDistanceSq > distanceSq) {
          // Found new best
          bestDistanceSq = distanceSq;
          bestClusterId = clusterId;
        }
      }
    }

    return bestClusterId;
  }

  /** Finds the square of the Euclidean distance between two vectors. */
  private static double distanceSquared(final double[] v1, final double[] v2) {
    if (v1.length != v2.length) {
      throw new IllegalArgumentException("Mismatched vector lengths");
    }

    double distanceSq = 0;
    for (int i = 0; i < v1.length; i++) {
      double delta = v1[i] - v2[i];
      distanceSq += delta * delta;
    }

    return distanceSq;
  }

  /** Fully recomputes a cluster's centroid from its members' vectors. */
  private void recomputeClusterCentroid(final JsonCluster cluster) {
    // Prepare to sum vectors
    MatrixProperties props = getMatrixProperties();
    int columnCount = props.getColumnCount();
    double[] clusterVector = new double[columnCount];

    // For each document vector in the cluster
    List<JsonClusterMember> members = cluster.getMembers();
    for (JsonClusterMember member : members) {
      String docId = member.getDocID();
      double[] docVector = props.getVector(docId);

      // Add to cluster vector
      for (int i = 0; i < columnCount; i++) {
        clusterVector[i] += docVector[i];
      }
    }

    // Convert sum to mean
    int memberCount = members.size();
    for (int i = 0; i < columnCount; i++) {
      clusterVector[i] /= memberCount;
    }

    // Centroid = mean document vector
    cluster.setCentroid(clusterVector);
  }

  /** Gets the matrix and mappings, lazy-initializing if needed. */
  public MatrixProperties getMatrixProperties() {
    if (matrixProps == null) {
      // Load the data
      final String path = "/data/" + projectName + "/TextFeatures";
      final URL resURL = StatefulProjectLoader.class.getResource(path);

      if (resURL != null) {
        final File file = new File(resURL.getFile());
        InspireTextMatrixInitializer in = new InspireTextMatrixInitializer(file, AIMDataType.TEXT);
        matrixProps = in.initialize();
      }
    }

    return matrixProps;
  }

  /**
   * @return the list of clusters
   */
  public List<JsonCluster> getClusterMembership() {
    return new ArrayList<JsonCluster>(clusters.values());
  }

  /** Labels clusters by finding the top three features in their vectors. */
  private void autoLabelClusters(final Map<Integer, JsonCluster> clusters, final List<String> features) {
    // For each cluster
    Iterator<Integer> iter = clusters.keySet().iterator();
    while (iter.hasNext()) {
      int id = iter.next();
      JsonCluster cluster = clusters.get(id);

      // Get cluster centroid
      double[] centroid = cluster.getCentroid();

      // Find top three dimensions
      List<Integer> topThree = sortVectorDimensionsByValue(centroid, 3);

      // Append the feature String for each
      StringBuilder buffer = new StringBuilder();
      for (int i : topThree) {
        String feature = features.get(i);
        buffer.append(", ");
        buffer.append(feature);
      }

      // Set label
      String clusterLabel = buffer.toString().substring(2);
      int clusterId = cluster.getClusterId();
      cluster.setClusterLabel(clusterLabel);
      labelMap.put(clusterId, clusterLabel);
    }
  }

  /** Gets the indexes of the top n dimensions of the vector. */
  private static List<Integer> sortVectorDimensionsByValue(final double[] vector, final int n) {
    // Populate list with dimension indexes
    List<Integer> indexList = new ArrayList<Integer>(vector.length);
    for (int index = 0; index < vector.length; index++) {
      indexList.add(index);
    }

    // Sort indexes by associated value, high to low
    Collections.sort(indexList, new Comparator<Integer>() {
      @Override
      public int compare(final Integer i, final Integer j) {
        return (int) Math.signum(vector[j] - vector[i]);
      }
    });

    return indexList.subList(0, n);
  }

  /** Builds the Lucene index for this dataset. */
  private void buildIndex() {
    String indexPath = getLuceneSearchIndexPath();
    URL indexUrl = getClass().getResource(indexPath);

    if (indexUrl == null) {
      // Index doesn't exist, so create it

      String contentPath = "/data/" + projectName + "/content/articles";
      URL contentUrl = getClass().getResource(contentPath);

      if (contentUrl != null) {
        File contentDir = new File(contentUrl.getFile());
        File[] files = contentDir.listFiles();
        List<File> fileList = Arrays.asList(files);

        File projectDir = getProjectDir();
        File indexDir = new File(projectDir, "lucene/base/index");
        try {
          LuceneIndexBuilder.indexFiles(indexDir, fileList);
        }
        catch (IOException e) {
          //TODO
          e.printStackTrace();
        }
      }
    }
  }

  public String getUser()
  {
    return this.user;
  }

  public void setUser(final String user)
  {
    this.user = user;
    noteChange();
  }

  public static void setSerializationDir(final String path) {
    SERIALIZATION_DIRECTORY = path;
  }

  /** Gets the project directory. */
  private File getProjectDir() {
    File projectDir = getProjectDir(projectName);
    return projectDir;
  }

  /** Gets the project directory. */
  private static File getProjectDir(final String projectName) {
    String projectDirName = SERIALIZATION_DIRECTORY;
    File projectDir = new File(projectDirName);
    return projectDir;
  }

  /** Gets the path for the project serialization file. */
  private static File getProjectFile(final String projectName, final String user) {
    File projectDir = getProjectDir(projectName);
    String filename = String.format("%s_%s.ser", projectName, user);

    return new File(projectDir, filename);
  }

  /**
   * Gets the path for a timestamp version of the project serialization file.
   * This form of the serialization file includes a timestamp in the filename.
   */
  private static File getTimestampedProjectFile(final String projectName, final String user) {
    File projectDir = getProjectDir(projectName);
    String timestamp = getMillisecondTimestamp();
    String filename = String.format("%s_%s-%s.ser", projectName, user, timestamp);

    return new File(projectDir, filename);
  }

  /** Gets a millisecond-precision timestamp. */
  private static String getMillisecondTimestamp() {
    Date now = Calendar.getInstance().getTime();
    DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    String timestamp = format.format(now);
    return timestamp;
  }

  /**
   * Searches the index for a String.
   *
   * @param queryStr  String to search for
   * @param maxHits   Maximum number of hits to return
   *
   * @return List of document ID Strings of hits
   */
  public List<String> search(final String queryStr, final int maxHits) {
    try {
      // Find and open the Lucene index
      String indexPath = getLuceneSearchIndexPath();
      URL indexUrl = getClass().getResource(indexPath);
      String indexDir = indexUrl.getFile();
      Directory index = FSDirectory.open(new File(indexDir));

      // Create a query
      StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
      Query q = new QueryParser(Version.LUCENE_35, "text__TEXT", analyzer).parse(queryStr);

      // Query against the index
      IndexReader reader = IndexReader.open(index);
      IndexSearcher searcher = new IndexSearcher(reader);
      TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits, true);
      searcher.search(q, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      searcher.close();
      reader.close();

      // Package the hits as document ID Strings
      List<String> docList = new ArrayList<String>();
      for (ScoreDoc hit : hits) {
        int docIndex = hit.doc;
        String docId = toDocId(docIndex);
        if (docId != null) {
          // Found in currently known documents
          docList.add(docId);
        }
      }

      return docList;
    }
    catch (IOException | ParseException e) {
      // Something went wrong, so give no results
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  /** Gets the path to the Lucene index used for search. */
  private String getLuceneSearchIndexPath() {
    String path = "/data/" + projectName + "/lucene/base/index";
    return path;
  }

  /** Converts a document index into a document ID string. */
  private String toDocId(final int docIndex) {
    MatrixProperties props = getMatrixProperties();
    String docId = props.getRowIdentifier(docIndex);
    return docId;
  }

  /**
   * @param docID
   * @return the snippet
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static String getDocTextSnippet(final String docID) throws FileNotFoundException, IOException {
    String docText = getDocText(docID);
    if (docText.length() > 100) {
      docText = docText.substring(0, 100);
    }
    return docText;
  }


  /**
   * @param docID
   * @return the snippet
   * @throws FileNotFoundException
   * @throws IOException
   */
  //  public String getDocTitle(final String docID) throws FileNotFoundException, IOException {
  //    String docText = getDocText(docID);
  //    if (docText.length() > 100) {
  //      docText = docText.substring(0, 100);
  //    }
  //    return docText;
  //  }

  public static String getDocTitle(final String docID) throws FileNotFoundException, IOException {
    final String path = "/data/" + CanvasConfig.PROJECT_NAME + "/content/" + docID;
    final URL resURL = StatefulProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      List<String> lines = StringUtil.removeBlanks(IOUtils.readLines(new FileInputStream(file), "UTF-8"));

      if (lines != null && lines.size() >= 3) {
        String title = lines.get(0);
        if (title.startsWith("Title: ")) {
          title = title.substring(7);
        }
        return title;
      }
    }
    return docID;
  }

  /**
   * @param docID
   * @return the full text
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static String getDocText(final String docID) throws FileNotFoundException, IOException {
    final String path = "/data/" + CanvasConfig.PROJECT_NAME + "/content/" + docID;
    final URL resURL = StatefulProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      List<String> lines = IOUtils.readLines(new FileInputStream(file), "UTF-8");

      StringBuilder builder = new StringBuilder();
      if (lines != null) {
        if (lines.size() >= 1) {
          for (int i = 1; i < lines.size(); i++) {
            builder.append("<p>");
            builder.append(lines.get(i));
            builder.append("</p>");
          }
        }
      }

      return builder.toString();
    }
    return docID;
  }
  
  /** Gets the document text. */
  public String getFullDocument(String docId) throws IOException {
      String text = getDocText(docId);
      return text;
  }

  /**
   * Gets the features for the project.
   *
   * @return the feature labels
   */
  public List<String> getFeatureLabels() {
    MatrixProperties props = getMatrixProperties();
    if (props == null) {
      return Collections.emptyList();
    }

    List<String> features = props.getColumnIdentifiers();
    return features;
  }
  
  /** Gets the current feature weights. */
  public double[] getFeatureWeights() {
      double[] weights = featureStrategy.getFeatureWeights();
      return weights;
  }

  /**
   * @param clusterNumber
   * @param label
   */
  public void labelCluster(final Integer clusterNumber, final String label) {
    if (clusters.containsKey(clusterNumber)) {
      JsonCluster jsonCluster = clusters.get(clusterNumber);
      jsonCluster.setClusterLabel(label);
      labelMap.put(clusterNumber, label);
      noteChange();
    } else {
      System.err.println("there is not a cluster #" + clusterNumber);
    }
  }

  /** Creates a new empty JsonCluster. */
  private JsonCluster createNewCluster(final Integer clusterNumber, final String label, final double x, final double y) {
    if (clusters.containsKey(clusterNumber)) {
      throw new IllegalArgumentException("a cluster #" + clusterNumber + " already exists");
    }

    MatrixProperties props = getMatrixProperties();
    int vectorLength = props.getColumnCount();

    JsonCluster cluster = new JsonCluster(clusterNumber, x, y, label);
    cluster.setCentroid(new double[vectorLength]);
    clusters.put(clusterNumber, cluster);
    labelMap.put(clusterNumber, label);
    System.out.println("cluster #" + clusterNumber + " is created.");

    return cluster;
  }

  /** Gets (largest cluster ID in use) + 1. */
  private int getNextClusterId() {
    Set<Integer> clusterIds = clusters.keySet();

    if (clusterIds.isEmpty()) {
      return 0;
    }
    return Collections.max(clusterIds) + 1;
  }

  /**
   * Creates a new empty JsonCluster with the next cluster ID.
   * @param label
   * @param x
   * @param y
   *
   * @return The cluster ID
   */
  public int createNewCluster(final String label, final double x, final double y) {
    int clusterId = getNextClusterId();
    createNewCluster(clusterId, label, x, y);

    return clusterId;
  }

  /**
   * Creates a new empty JsonCluster with the next cluster ID, with ASI messaging.
   *
   * <p>This version of the method includes ASI messaging support. It wraps the
   * base version, which can then be used as a building block for other methods
   * without imposing the same ASI message.
   *
   *  @return The cluster ID
   */
  public int createNewCluster_ASI(final String label, final int x, final int y) {
    int clusterId = createNewCluster(label, x, y);
    streamCanvasToASI("CreateCluster");
    noteChange();

    return clusterId;
  }

  /**
   * Annotates a document with a note.
   *
   * @param docId       Document to annotate
   * @return a list of annotations
   */
  public List<Annotation> getAnnotations(final String docId) {
    JsonClusterMember doc = findDoc(docId);
    Set<Annotation> annotation = null;
    if (doc != null) {
      annotation = doc.getAnnotations();
    }
    return new ArrayList<>(annotation);
  }

  /**
   *
   * @param annotation annotation object
   * @return true if success
   */
  public JsonClusterMember annotateDocument(final Annotation annotation) {
    JsonClusterMember doc = findDoc(annotation.getDocId());
    if (doc == null) {
    } else {
      System.out.println("set annotation: " + annotation.toString());
      doc.addAnnotation(annotation);
      noteChange();
    }
    return doc;
  }

  /**
   * Annotates a document with a note.
   *
   * @param docId       Document to annotate
   * @param annotation  Note to attach to document
   *
   * return true if successful
   */
  public boolean annotateDocument(final String docId, final String annotation) {
    JsonClusterMember doc = findDoc(docId);
    if (doc == null) {
      return false;
    }
    doc.addAnnotation(new Annotation(docId, null, annotation, 0));
    noteChange();
    return true;
  }

  /**
   * @param idString
   * @return
   */
  public List<DocumentHighlight> getHighlights(final String idString) {
    JsonClusterMember doc = findDoc(idString);
    Collection<DocumentHighlight> annotation = null;
    if (doc != null) {
      annotation = doc.getHighlights();
    }
    return new ArrayList<>(annotation);
  }

  /**
   * @param highlight
   * @return
   */
  public JsonClusterMember highlightDocument(final DocumentHighlight highlight) {
    String docId = highlight.getDocId();
    JsonClusterMember doc = findDoc(docId);
    if (doc == null) {
    } else {
      System.out.println("added annotation: " + highlight.toString());
      doc.addHighlight(highlight);
      noteChange();
    }
    return doc;
  }


  /**
   * @param docId
   * @return
   */
  public JsonClusterMember bookmarkDocument(final String docId) {
    JsonClusterMember doc = findDoc(docId);
    if (doc == null) {
    } else {
      System.out.println("bookmark document: " + docId);
      doc.setBookmarked(true);
      boostDocument(docId);
      noteChange();
    }
    return doc;
  }


  /**
   * @param docId
   * @param readCount
   * @return
   */
  public JsonClusterMember readDocument(final String docId, final int readCount) {
    JsonClusterMember doc = findDoc(docId);
    if (doc == null) {
    } else {
      System.out.println("read document: " + docId + " " + readCount);
      doc.setRead(readCount);
      noteChange();
    }
    return doc;
  }



  /**
   * Moves a document from one cluster to another.
   *
   * @param docID          Document to move
   * @param srcClusterID   Source cluster for the document
   * @param destClusterID  Destination cluster for the document
   *
   * @return Whether a move actually occurred. It wouldn't if the document
   * wasn't in the source cluster.
   */
  public boolean moveToCluster(final String docID, final int srcClusterID, final int destClusterID) {
    // Remove this document from its old cluster
    JsonClusterMember member = removeDocFromCluster(docID, srcClusterID);

    boolean ok = (member != null);
    if (ok) {
      // Add this document to the new cluster
      addDocToCluster(member, destClusterID);
    }
    return ok;
  }

  /**
   * Moves a document from one cluster to another, with ASI messaging.
   *
   * <p>This version of the method includes ASI messaging support. It wraps the
   * base version, which can then be used as a building block for other methods
   * without imposing the same ASI message.
   *
   * @param docID          Document to move
   * @param srcClusterID   Source cluster for the document
   * @param destClusterID  Destination cluster for the document
   *
   * @return Whether a move actually occurred. It wouldn't if the document
   * wasn't in the source cluster.
   */
  public boolean moveToCluster_ASI(final String docId, final int srcClusterId, final int destClusterId) {
    boolean ok = moveToCluster(docId, srcClusterId, destClusterId);
    if (ok) {
      boostOnMoveToCluster(docId, destClusterId);
      streamCanvasToASI("MoveToCluster");
      noteChange();
    }
    return ok;
  }

  /** Boosts when moving a document to a cluster. */
  private void boostOnMoveToCluster(final String docId, final int clusterId) {
    JsonCluster cluster = clusters.get(clusterId);

    // Get row indexes for the new doc and those already in the cluster
    int newRowIndex = clusterStrategy.getRowIndex(docId);
    List<Integer> existingRowIndexes = getClusterMembers(cluster);

    // Update the feature and document weighting
    featureStrategy.boost(newRowIndex, existingRowIndexes);
    updateDocumentWeights();
  }
  
  /** Boosts the top features of a document. */
  private void boostDocument(String docId) {
    featureStrategy.boost(docId);
    updateDocumentWeights();
  }

  /** Repositions a cluster. */
  public void moveCluster(final int clusterId, final double x, final double y) {
    JsonCluster cluster = clusters.get(clusterId);
    cluster.setX(x);
    cluster.setY(y);
    noteChange();
  }

  /**
   * Gets the row (document) indexes for a JsonCluster.
   *
   * @return Indexes (in no guaranteed order)
   */
  private List<Integer> getClusterMembers(final JsonCluster cluster) {
    // Get the members
    List<JsonClusterMember> members = cluster.getMembers();
    int count = members.size();

    // For each member
    List<Integer> rowIndexList = new ArrayList<Integer>(count);
    for (JsonClusterMember member : members) {

      // Convert its String identifier to a row index
      String docId = member.getDocID();
      int rowIndex = clusterStrategy.getRowIndex(docId);
      rowIndexList.add(rowIndex);
    }

    return rowIndexList;
  }

  // Finds the cluster id for a given docID
  private int findDocClusterID(final String docID) {
    Iterator<Integer> iter = clusters.keySet().iterator();
    while (iter.hasNext()) {
      int clusterId = iter.next();
      List<JsonClusterMember> members = clusters.get(clusterId).getMembers();
      for (int i = 0; i < members.size(); i++) {
        JsonClusterMember member = members.get(i);
        if (member.getDocID().equals(docID)) {
          return clusterId;
        }
      }
    }
    return -1;
  }

  /** Finds a document by ID.
   * @param docID
   * @return
   */
  public JsonClusterMember findDoc(final String docID) {
    Iterator<Integer> iter = clusters.keySet().iterator();
    while (iter.hasNext()) {
      int clusterId = iter.next();
      List<JsonClusterMember> members = clusters.get(clusterId).getMembers();
      for (int i = 0; i < members.size(); i++) {
        JsonClusterMember member = members.get(i);
        if (member.getDocID().equals(docID)) {
          return member;
        }
      }
    }
    return null;
  }

  /** Removes document from a known cluster it is a member of. */
  private JsonClusterMember removeDocFromCluster(final String docID, final int clusterID) {
    JsonCluster cluster = clusters.get(clusterID);
    List<JsonClusterMember> members = cluster.getMembers();
    for (int i = 0; i < members.size(); i++) {
      JsonClusterMember member = members.get(i);
      if (member.getDocID().equals(docID)) {
        member.setClusterId(UNGROUPED_ID);
        return members.remove(i);
      }
    }

    recomputeClusterCentroid(cluster);

    return null;
  }

  /** Figures out which cluster the document is in, then removes it. */
  @SuppressWarnings("unused")
  private JsonClusterMember removeDocFromCluster(final String docID) {
    int clusterID = findDocClusterID(docID);
    return removeDocFromCluster(docID, clusterID);
  }

  /** Adds a document (JsonClusterMember) to a cluster. */
  private void addDocToCluster(final JsonClusterMember doc, final int clusterId) {
    if (!clusters.containsKey(clusterId)) {
      throw new IllegalArgumentException(String.format("Cluster %d doesn't exist", clusterId));
    }

    JsonCluster cluster = clusters.get(clusterId);
    cluster.add(doc);

    recomputeClusterCentroid(cluster);
  }

  /**
   * Adds a document to a cluster.
   *
   * <p>This version of the method includes ASI messaging support. It wraps the
   * base version, which can then be used as a building block for other methods
   * without imposing the same ASI message.
   */
  public boolean addDocToCluster_ASI(final JsonClusterMember doc, final int clusterId) {
    addDocToCluster(doc, clusterId);
    streamCanvasToASI("AddDocToCluster");
    noteChange();

    return true;
  }

  /** Removes a cluster. */
  private boolean removeCluster(final int clusterID) {
    if (clusters.containsKey(clusterID)) {
      JsonCluster cluster = clusters.get(clusterID);

      // Move the cluster's members to the trash cluster
      for (JsonClusterMember member : cluster.getMembers()) {
        addDocToCluster(member, UNGROUPED_ID);
      }

      clusters.remove(clusterID);
      return true;
    }
    return false;
  }

  /**
   * Removes a cluster.
   *
   * <p>This version of the method includes ASI messaging support. It wraps the
   * base version, which can then be used as a building block for other methods
   * without imposing the same ASI message.
   */
  public boolean removeCluster_ASI(final int clusterId) {
    boolean ok = removeCluster(clusterId);
    if (ok) {
      streamCanvasToASI("RemoveCluster");
      noteChange();
    }
    return ok;
  }

  /** Moves a document from a cluster into the trash (ungrouped) cluster. */
  private void moveDocToUnused(final String docId) {
    int srcClusterId = findDocClusterID(docId);
    moveToCluster(docId, srcClusterId, UNGROUPED_ID);
  }

  /**
   * Moves a document from a cluster into the trash (ungrouped) cluster.
   *
   * <p>This version of the method includes ASI messaging support. It wraps the
   * base version, which can then be used as a building block for other methods
   * without imposing the same ASI message.
   */
  public void moveDocToUnused_ASI(final String docId) {
    moveDocToUnused(docId);
    streamCanvasToASI("RemoveFromCluster");
    noteChange();
  }

  /**
   * Creates a new cluster for a given document.
   *
   * @return clusterNumber
   */
  public int createClusterWithDoc(final String label, final double x, final double y, final JsonClusterMember member) {
    int clusterId = getNextClusterId();
    createNewCluster(clusterId, label, x, y);
    addDocToCluster(member, clusterId);

    return clusterId;
  }

  /**
   * Creates a new cluster for a given document, with ASI messaging.
   *
   * <p>This version of the method includes ASI messaging support. It wraps the
   * base version, which can then be used as a building block for other methods
   * without imposing the same ASI message.
   */
  public void createClusterWithDoc_ASI(final String label, final int x, final int y, final JsonClusterMember member) {
    createClusterWithDoc(label, x, y, member);
    streamCanvasToASI("CreateClusterWithDoc");
    noteChange();
  }

  public JsonCluster getCluster(final int clusterId) {
    if (clusters.containsKey(clusterId)) {
      return clusters.get(clusterId);
    }
    System.err.println("[getCluster]: cluster #" + clusterId + " doesn't exist");
    return null;
  }

  private void streamCanvasToASI(final String interaction) {

    String username =  this.getUser();
    if (CanvasConfig.STREAM_TO_ASI && username != null && !username.isEmpty()) {
      List<JsonCluster> jsonClusters = new ArrayList<JsonCluster>(
          clusters.values());
      try {
        CanvasStreamer.runCanvasStream(user, projectName, interaction,
            jsonClusters, matrixProps);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Updates all document weights based on the current boosted weighting.
   * Stores the weights in their document objects.
   * 
   * <p>For now there's also a returned Map, to help with backward compatibility 
   * for the event reporting.
   *
   * @return Map of document ID to weight (0=smallest to 3=largest)
   */
  public synchronized Map<String, Integer> updateDocumentWeights() {
      Map<String, Integer> docWts = featureStrategy.computeDocumentWeights();
      
      for (Map.Entry<String, Integer> entry : docWts.entrySet()) {
          String docId = entry.getKey();
          int docWt = entry.getValue();
          
          JsonClusterMember doc = findDoc(docId);
          doc.setDocWeight(docWt);
      }
      
      return docWts;
  }

  /**
   * Gets previously computed document weights.
   * 
   * @return Map of doc ID to weight (0 to 3)
   */
  public Map<String, Integer> getDocumentWeights() {
      Map<String, Integer> weights = featureStrategy.getDocumentWeights();
      return weights;
  }
  
  /**
   * Gets the project, either by reading a previously serialized object, or if
   * there is none, by constructing a new object.
   */
  public static StatefulProjectLoader loadProject(final String projectName, final String userName) {
    StatefulProjectLoader loader = null;

    // Deserialize if we can
    File serializationFile = getProjectFile(projectName, userName);
    if (serializationFile.exists()) {
      try {
        System.out.println("Deserializing from: " + serializationFile.getAbsolutePath());
        loader = (StatefulProjectLoader) deserialize(serializationFile);
      }
      catch (IOException | ClassNotFoundException e) {
        // Load failed; warn and drop through to give a new project.
        e.printStackTrace();
      }
    }

    if (loader == null) {
      // No project yet; make one
      loader = new StatefulProjectLoader(projectName);
      loader.setUser(userName);
    }

    return loader;
  }

  /** Writes the project serialization file. */
  private void saveProject() {
    try {
      File serializationFile = getProjectFile(projectName, user);
      serialize(serializationFile, this);
      changed = false;
    }
    catch (IOException e) {
      System.err.println("Failed to write serialization file");
      e.printStackTrace();
    }
  }

  /** Handles the end of the session, usually saving the serialization file. */
  public void endSession() {
    if (CanvasConfig.SAVE_ON_END) {
      saveProject();
    }
  }

  /**
   * Writes a backup version of the project serialization file. This version
   * differs from the main version in that it incorporates a timestamp into
   * the filename. It is intended to provide backups in case of inadvertent data
   * corruption in the user study (such as loading too many data increments or
   * data from the wrong user).
   */
  private void saveBackup() {
    try {
      if (CanvasConfig.SAVE_ON_BACKUP_REQUEST) {
        File backupFile = getTimestampedProjectFile(projectName, user);
        serialize(backupFile, this);
      }
    }
    catch (IOException e) {
      System.err.println("Failed to write serialization backup file");
      e.printStackTrace();
    }
  }

  /** Saves project after a change, provided that option is enabled. */
  private void saveChange() {
    if (CanvasConfig.SAVE_ON_CHANGE) {
      saveProject();
    }
  }

  /**
   * Records a change in state since the last save of the project's main
   * serialization file.
   */
  private void noteChange() {
    if (isChanging) {
      changed = true;
    }
    else {
      saveChange();
    }
  }

  /**
   * Sets the start or finish of a change transaction. Calls to saveChange()
   * will be deferred until the end of the transaction.
   *
   * @param flag  true for start, false for finish
   */
  public void setIsChanging(final boolean flag) {
    isChanging = flag;
    if (changed  &&  !isChanging) {
      saveChange();
    }
  }

  /** Serializes an object to a file. */
  private static void serialize(final File f, final Serializable obj) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream out = new ObjectOutputStream(fos)) {
      out.writeObject(obj);
    }
  }

  /** Reads a serialized object from a file. */
  private static Object deserialize(final File f) throws IOException, ClassNotFoundException {
    try (FileInputStream fis = new FileInputStream(f);
        ObjectInputStream in = new ObjectInputStream(fis)) {
      Object obj = in.readObject();
      return obj;
    }
  }

  public int getCurrentIncrement() {
    return currentIncrement;
  }

  public void setCurrentIncrement(final int currentIncrement) {
    this.currentIncrement = currentIncrement;
    noteChange();
  }
  
  /** Loads the next increment of data for the user study. */
  public List<JsonCluster> loadNextIncrement() {
      saveBackup();

      documentManager = new UserStudyDocumentManager(currentIncrement, this);
      List<JsonCluster> clusters = documentManager.loadNextIncrement();
      
      updateDocumentWeights();
      
      return clusters;
  }
  
  /** Gets the cluster assignments for the most recent increment. */
  public Map<String, Integer> getIncrementalClusterAssignments() {
      return documentManager.getIncrementalClusterAssignments();
  }
}
























