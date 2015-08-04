package gov.pnnl.aim.discovery.interactions;


public class BookmarkDocumentEvent extends SemanticInteractionEvent {

  private final String docId;

  /**
   * @param docId
   */
  public BookmarkDocumentEvent(final String docId) {
    this.docId = docId;

    this.type = SemanticInteractions.BOOKMARK;
  }

  /**
   * @return the id
   */
  public String getDocId() {
    return docId;
  }

  @Override
  public String toString() {
    return type + DELIMITER + timestamp + DELIMITER + docId;
  }
}
