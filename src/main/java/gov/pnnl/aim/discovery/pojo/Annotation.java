/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author leej324
 *
 */
public class Annotation implements Serializable {
  
  // For Serializable
  private static final long serialVersionUID = 9026114148259461337L;

  /**
   * selected text in the original document
   */
  private String text;

  /**
   * annotation of the selected text
   */
  private String annotation;
  
  /**
   * docId
   */
  private String docId;
  
  /**
   * starting index of the selected text in the original document
   */
  private int start;

  /**
   * 
   * @param docId
   * @param text
   * @param annotation
   * @param start
   */
  public Annotation(final String docId, final String text, final String annotation, final int start) {
    this.setDocId(docId);
    this.setText(text);
    this.setAnnotation(annotation);
    this.setStart(start);
  }
  
  /**
   * 
   * @return annotation
   */
  public String getAnnotation() {
    return annotation;
  }
  
  /**
   * 
   * @param annotation
   */
  public void setAnnotation(String annotation) {
    this.annotation = annotation;
  }

  /**
   * 
   * @return text
   */
  public String getText() {
    return text;
  }
  
  /**
   * 
   * @param text
   */
  public void setText(String text) {
    this.text = text;
  }
  
  /**
   * 
   * @return docId
   */
  public String getDocId() {
    return docId;
  }
  
  /**
   * 
   * @param docId
   */
  public void setDocId(String docId) {
    this.docId = docId;
  }

  /**
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * @param start the start to set
   */
  public void setStart(int start) {
    this.start = start;
  }
  
  @Override
  public String toString() {
    JsonObject o = new JsonObject();
    o.addProperty("start", start);
    o.addProperty("text", text);
    o.addProperty("docId", docId);
    o.addProperty("annotation", annotation);
    return new Gson().toJson(o);
  }
  
  /**
   * 
   * @param annotation
   * @return
   */
  public boolean equals(Annotation annotation) {
    return this.docId.equals(annotation.docId)
        && this.start == annotation.start
        && this.text.equals(annotation.text);
  }
}
