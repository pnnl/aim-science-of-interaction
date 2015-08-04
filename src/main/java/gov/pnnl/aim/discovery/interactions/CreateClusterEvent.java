/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.interactions;

/**
 * @author hamp645
 *
 */
public class CreateClusterEvent extends SemanticInteractionEvent {

  private final int id;

  /**
   * @param id
   */
  public CreateClusterEvent(final int id) {
    this.id = id;
    this.type = SemanticInteractions.CREATE_CLUSTER;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return type + DELIMITER + timestamp + DELIMITER + id;
  }
}
