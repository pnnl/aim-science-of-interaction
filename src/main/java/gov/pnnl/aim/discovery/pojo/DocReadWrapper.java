/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

/**
 * @author hamp645
 *
 */
public class DocReadWrapper {
  private String docId;

  private int readCount;

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

  /**
   * @return the readCount
   */
  public int getReadCount() {
    return readCount;
  }

  /**
   * @param readCount
   *          the readCount to set
   */
  public void setReadCount(final int readCount) {
    this.readCount = readCount;
  }

}
