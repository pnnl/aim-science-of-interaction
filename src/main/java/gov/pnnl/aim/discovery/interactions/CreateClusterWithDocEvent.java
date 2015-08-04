package gov.pnnl.aim.discovery.interactions;


public class CreateClusterWithDocEvent extends SemanticInteractionEvent {

  private final String id;

  /**
   * @param member
   */
  public CreateClusterWithDocEvent(final String id) {
    this.id = id;
    this.type = SemanticInteractions.CREATE_CLUSTER_WITH_DOC;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return type + DELIMITER + timestamp + DELIMITER + id;
  }
}
