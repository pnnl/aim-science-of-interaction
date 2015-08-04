/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

/**
 * @author hamp645
 *
 */
public class LabelClusterWrapper {
  private int clusterId;

  private String label;

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

  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label
   *          the label to set
   */
  public void setLabel(final String label) {
    this.label = label;
  }
}
