/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.nmr;

/**
 * @author hamp645
 *
 */
public class NMRAbundanceRange {
  private double min;

  private double max;

  private String compound;

  /**
   * @return the min
   */
  public double getMin() {
    return min;
  }

  /**
   * @param min
   *          the min to set
   */
  public void setMin(final double min) {
    this.min = min;
  }

  /**
   * @return the max
   */
  public double getMax() {
    return max;
  }

  /**
   * @param max
   *          the max to set
   */
  public void setMax(final double max) {
    this.max = max;
  }

  /**
   * @return the compoud
   */
  public String getCompound() {
    return compound;
  }

  /**
   * @param compoud
   *          the compoud to set
   */
  public void setCompound(final String compoud) {
    this.compound = compoud;
  }
}
