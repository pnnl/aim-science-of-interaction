package gov.pnnl.aim.discovery.interactions;

public class DocumentReadEvent extends SemanticInteractionEvent {

  private final String docId;

  private final int readCount;

  /**
   * @param docId
   */
  public DocumentReadEvent(final String docId, final int readCount) {
    this.docId = docId;
    this.readCount = readCount;

    this.type = SemanticInteractions.READ_COUNT;
  }

  /**
   * @return the id
   */
  public String getDocId() {
    return docId;
  }

  /**
   * @return the readCount
   */
  public int getReadCount() {
    return readCount;
  }

  @Override
  public String toString() {
    return type + DELIMITER + timestamp + DELIMITER + docId + DELIMITER + readCount;
  }
}
