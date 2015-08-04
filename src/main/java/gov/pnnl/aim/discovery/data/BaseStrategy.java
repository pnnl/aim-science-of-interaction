/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.data;

import java.io.Serializable;

/**
 * @author hamp645
 *
 */
public abstract class BaseStrategy implements SemanticStrategy, Serializable {
  // For Serializable
  private static final long serialVersionUID = 8417744761755539718L;
  
  /** The properties object */
  protected MatrixProperties properties;

  /**
   * @param properties
   */
  public BaseStrategy(final MatrixProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  public BaseStrategy() {
  }

  /**
   * Resets any stored state
   */
  public abstract void reset();

  /**
   * @return the properties
   */
  public MatrixProperties getProperties() {
    return properties;
  }

  /**
   * @param properties
   *          the properties to set
   */
  public void setProperties(final MatrixProperties properties) {
    this.properties = properties;
  }
}
