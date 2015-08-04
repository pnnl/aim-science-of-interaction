/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.nmr;

/**
 * @author hamp645
 *
 */
public class NMRPoint {
  private int x;

  private double y;

  /**
   * @param i
   * @param random
   */
  public NMRPoint(final int i, final double random) {
    x = i;
    y = random;
  }

  /**
   * @return the x
   */
  public int getX() {
    return x;
  }

  /**
   * @param x
   *          the x to set
   */
  public void setX(final int x) {
    this.x = x;
  }

  /**
   * @return the y
   */
  public double getY() {
    return y;
  }

  /**
   * @param y
   *          the y to set
   */
  public void setY(final double y) {
    this.y = y;
  }
}
