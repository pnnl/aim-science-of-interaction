/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.nmr;

import java.util.Map;

/**
 * @author hamp645
 *
 */
public class NMRCompoundHistory {
  private Map<String, Double> map;

  /**
   * @param map
   */
  public void setMap(final Map<String, Double> map) {
    this.map = map;
  }

  /**
   * @return the map
   */
  public Map<String, Double> getMap() {
    return map;
  }
}
