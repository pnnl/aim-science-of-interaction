/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.nmr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hamp645
 */
public class OldNMRReactorState {
  private NMRSample current = new NMRSample();

  private List<NMRSample> history = new ArrayList<>();

  /**
   *
   */
  public OldNMRReactorState() {
  }

  /**
   * @return the currrent
   */
  public NMRSample getCurrent() {
    return current;
  }

  /**
   * @param currrent
   *          the currrent to set
   */
  public void setCurrent(final NMRSample current) {
    this.current = current;
  }

  /**
   * @return the history
   */
  public List<NMRSample> getHistory() {
    return history;
  }

  /**
   * @param history
   *          the history to set
   */
  public void setHistory(final List<NMRSample> history) {
    this.history = history;
  }
}
