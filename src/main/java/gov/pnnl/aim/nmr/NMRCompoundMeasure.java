package gov.pnnl.aim.nmr;

public class NMRCompoundMeasure {
  private String compoundName;

  private double probability;

  private double concentration;

  private boolean vetoed;

  public NMRCompoundMeasure() {

  }

  /**
   * @return the compoundName
   */
  public String getCompoundName() {
    return compoundName;
  }

  /**
   * @param compoundName
   *          the compoundName to set
   */
  public void setCompoundName(final String compoundName) {
    this.compoundName = compoundName;
  }

  /**
   * @return the probability
   */
  public double getProbability() {
    return probability;
  }

  /**
   * @param probability
   *          the probability to set
   */
  public void setProbability(final double probability) {
    this.probability = probability;
  }

  /**
   * @return the concentration
   */
  public double getConcentration() {
    return concentration;
  }

  /**
   * @param concentration
   *          the concentration to set
   */
  public void setConcentration(final double concentration) {
    this.concentration = concentration;
  }

  /**
   * @return the vetoed
   */
  public boolean isVetoed() {
    return vetoed;
  }

  /**
   * @param vetoed
   *          the vetoed to set
   */
  public void setVetoed(final boolean vetoed) {
    this.vetoed = vetoed;
  }
}
