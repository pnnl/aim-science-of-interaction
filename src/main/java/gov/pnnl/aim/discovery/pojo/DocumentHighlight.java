/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author hamp645
 *
 */
public class DocumentHighlight implements Serializable {
  private String text;

  private int start;

  private String docId;

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @param text
   *          the text to set
   */
  public void setText(final String text) {
    this.text = text;
  }

  /**
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * @param start
   *          the start to set
   */
  public void setStart(final int start) {
    this.start = start;
  }

  /**
   * @return the docId
   */
  public String getDocId() {
    return docId;
  }

  /**
   * @param docId
   *          the docId to set
   */
  public void setDocId(final String docId) {
    this.docId = docId;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
