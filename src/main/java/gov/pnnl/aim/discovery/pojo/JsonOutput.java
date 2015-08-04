/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.pojo;

/**
 * @author hamp645
 *
 */
public class JsonOutput {
  private String id;

  private String type;

  private String value;

  private double relX;

  private double relY;

  /**
   * @return the relX
   */
  public double getRelX() {
    return relX;
  }

  /**
   * @param relX
   *          the relX to set
   */
  public void setRelX(final double relX) {
    this.relX = relX;
  }

  /**
   * @return the relY
   */
  public double getRelY() {
    return relY;
  }

  /**
   * @param relY
   *          the relY to set
   */
  public void setRelY(final double relY) {
    this.relY = relY;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value
   *          the value to set
   */
  public void setValue(final String value) {
    this.value = value;
  }
}
