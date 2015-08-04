/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author hamp645
 *
 */
public class JsonClusterMember implements Serializable {

  // For Serializable
  private static final long serialVersionUID = -4214987340754725934L;

  private final String docID;

  private final String title;

  private final String docText;

  /** Annotation for the document. Never null. */
  private Set<Annotation> annotations = new HashSet<>();

  /** Annotation for the document. Never null. */
  private final Map<String, DocumentHighlight> highlights = new HashMap<>();

  /** the count of how many times the document has been read */
  private int read = 0;

  private String className = "document";

  private boolean bookmarked = false;

  private int docWeight = 0;

  private int clusterId = -1;

  /**
   * @param docID
   * @param docText
   * @param clusterId
   */
  public JsonClusterMember(final String docID, final String title, final String docText, final int clusterId) {
    this.docID = docID;
    this.title = title;
    this.docText = docText;
    this.clusterId = clusterId;
  }

  /**
   * @return the docID
   */
  public String getDocID() {
    return docID;
  }

  /**
   * @return the docText
   */
  public String getDocText() {
    return docText;
  }

  /**
   * @return the clusterId
   */
  public int getClusterId() {
    return clusterId;
  }

  /**
   * @param clusterId
   *          the clusterId to set
   */
  public void setClusterId(final int clusterId) {
    this.clusterId = clusterId;
  }

  @Override
  public String toString() {
    return docID;
  }

  public Set<Annotation> getAnnotations() {
    return annotations;
  }

  /**
   * @return
   */
  public Collection<DocumentHighlight> getHighlights() {
    return highlights.values();
  }

  public void setAnnotations(final Set<Annotation> annotations) {
    if (annotations == null) {
      throw new NullPointerException();
    }
    this.annotations = annotations;
  }

  /**
   * @param highlight
   */
  public void addHighlight(final DocumentHighlight highlight) {
    highlights.put(highlight.getText(), highlight);
  }

  public void addAnnotation(final Annotation annotation) {
    annotations.add(annotation);
  }

  public void removeAnnotation(final Annotation annotation) {
    annotations.remove(annotation);
  }

  public String getTitle() {
    return title;
  }

  /**
   * to get isRead
   *
   * @return isRead
   */
  public int getRead() {
    return read;
  }

  /**
   * @return the className
   */
  public String getClassName() {
    return className;
  }

  /**
   * @param className
   *          the className to set
   */
  public void setClassName(final String className) {
    this.className = className;
  }

  /**
   * @return the bookmarked
   */
  public boolean isBookmarked() {
    return bookmarked;
  }

  /**
   * @param bookmarked
   *          the bookmarked to set
   */
  public void setBookmarked(final boolean bookmarked) {
    this.bookmarked = bookmarked;
  }

  /**
   * @return the docWeight
   */
  public int getDocWeight() {
    return docWeight;
  }

  /**
   * @param docWeight
   *          the docWeight to set
   */
  public void setDocWeight(final int docWeight) {
    this.docWeight = docWeight;
  }

  /**
   * @param read
   *          the read to set
   */
  public void setRead(final int read) {
    this.read = read;
  }
}
