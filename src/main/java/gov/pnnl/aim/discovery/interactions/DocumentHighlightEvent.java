package gov.pnnl.aim.discovery.interactions;

import gov.pnnl.aim.discovery.pojo.DocumentHighlight;

public class DocumentHighlightEvent extends SemanticInteractionEvent {

  private final String docId;

  private final String text;

  private final int start;

  /**
   * @param highlight
   */
  public DocumentHighlightEvent(final DocumentHighlight highlight) {
    this.docId = highlight.getDocId();
    this.text = highlight.getText();
    this.start = highlight.getStart();

    this.type = SemanticInteractions.HIGHLIGHTED;
  }

  /**
   * @return the id
   */
  public String getDocId() {
    return docId;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @return the start
   */
  public int getStart() {
    return start;
  }

  @Override
  public String toString() {
    return type + DELIMITER + timestamp + DELIMITER + docId + DELIMITER + text + DELIMITER + start;
  }
}
