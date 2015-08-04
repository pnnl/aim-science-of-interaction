package gov.pnnl.aim.nmr;

public class SamplePrediction {
  public static int index = 0;

  public static int spIndex = 0;

  // Unique identifier for the sample
  private String sampleID;

  // Unique identifier for the sample
  private String spectraID;

  // Datetime the sample was processed by the NMR
  private long datetime = 0;

  // This is the static list of compound names
  private String[] compoundNames = new String[30];

  // This is the original spectra off the NMR machine
  private double[] spectra = new double[200];

  // This is the probability predicting presence of compounds from the classifier algorithm
  private double[] compoundProbabilities = new double[30];

  // This is the concentration value of compounds from the classifier algorithm
  private double[] compoundConcentrations = new double[30];

  // This is the veto declared by the reasoning algorithm that a compound is not present
  private boolean[] compoundVeto = new boolean[30];

  // This is the error condition code reported from one of the algorithms
  private int errorCondition = 0;

  // This is the error condition message associated with the above code
  private String errorConditionMessage = "";

  private static String[] ERROR_MSG = new String[] { "", "Off Balance", "High Noise", "Something", "Angry", "Sad" };

  /**
   * This generates random data for testing
   */
  public SamplePrediction() {
    setDatetime(System.currentTimeMillis());

    setCompoundNames(new String[] { "1-Methylhistidine", "2-Ketobutyric acid", "Deoxyuridine", "4-Pyridoxic acid", "Alpha-ketoisovaleric acid", "3-Methoxytyramine", "(S)-3-Hydroxyisobutyric acid", "Ureidopropionic acid", "Carnosine", "Adenine", "Butyric acid", "Acetic acid", "Betaine", "Melibiose", "Adenosine", "Androstenedione", "Cellobiose", "Cyclic AMP", "Acetoacetic acid", "Adenosine 3\'5\'-diphosphate", "Creatine", "Cholesterol", "Pipecolic acid", "Deoxyinosine", "Dihydrouracil", "Dehydroepiandrosterone", "Glycerophosphocholine", "Dimethylamine", "Cytidine", "Dimethylglycine" });

    for (int i = 0; i < compoundProbabilities.length; i++) {
      compoundProbabilities[i] = Math.random();
      compoundConcentrations[i] = Math.random();
    }

    for (int i = 0; i < compoundVeto.length; i++) {
      compoundVeto[i] = Math.random() < .1;
    }

    for (int i = 0; i < spectra.length; i++) {
      spectra[i] = Math.random(); // Math.cos(Math.toRadians(i/90));
    }

    double r = Math.random();
    if (r < .5) {
      setErrorCondition((int) (Math.random() * 5.0));
    } else {
      setErrorCondition(0);
    }

    setSampleID("HMDB" + index++);
    setSpectraID("SP" + spIndex++);
  }

  /**
   * This generates random data for testing
   */
  public SamplePrediction(final String sampleId) {
    setDatetime(System.currentTimeMillis());

    setCompoundNames(new String[] { "1-Methylhistidine", "2-Ketobutyric acid", "Deoxyuridine", "4-Pyridoxic acid", "Alpha-ketoisovaleric acid", "3-Methoxytyramine", "(S)-3-Hydroxyisobutyric acid", "Ureidopropionic acid", "Carnosine", "Adenine", "Butyric acid", "Acetic acid", "Betaine", "Melibiose", "Adenosine", "Androstenedione", "Cellobiose", "Cyclic AMP", "Acetoacetic acid", "Adenosine 3\'5\'-diphosphate", "Creatine", "Cholesterol", "Pipecolic acid", "Deoxyinosine", "Dihydrouracil", "Dehydroepiandrosterone", "Glycerophosphocholine", "Dimethylamine", "Cytidine", "Dimethylglycine" });

    for (int i = 0; i < compoundProbabilities.length; i++) {
      compoundProbabilities[i] = Math.random();
      compoundConcentrations[i] = Math.random();
    }

    for (int i = 0; i < compoundVeto.length; i++) {
      compoundVeto[i] = Math.random() < .1;
    }

    for (int i = 0; i < spectra.length; i++) {
      spectra[i] = Math.random(); // Math.cos(Math.toRadians(i/90));
    }

    double r = Math.random();
    if (r < .5) {
      setErrorCondition((int) (Math.random() * 5.0));
    } else {
      setErrorCondition(0);
    }

    setSampleID(sampleId);
    setSpectraID("SP" + spIndex++);
  }

  /**
   * @return the spectraID
   */
  public String getSpectraID() {
    return spectraID;
  }

  /**
   * @param spectraID
   *          the spectraID to set
   */
  public void setSpectraID(final String spectraID) {
    this.spectraID = spectraID;
  }

  /**
   * Gets the identifier for this sample
   */
  public String getSampleID() {
    return sampleID;
  }

  public void setSampleID(final String sampleID) {
    this.sampleID = sampleID;
  }

  /**
   * Gets the raw spectra data
   *
   * @return
   */
  public double[] getSpectra() {
    return spectra;
  }

  public void setSpectra(final double[] spectra) {
    this.spectra = spectra;
  }

  /**
   * Gets the compound names
   *
   * @return
   */
  public String[] getCompoundNames() {
    return compoundNames;
  }

  public void setCompoundNames(final String[] compoundNames) {
    this.compoundNames = compoundNames;
  }

  /**
   * Gets the probabilities for each compound as predicted by the classifier
   *
   * @return
   */
  public double[] getCompoundProbabilities() {
    return compoundProbabilities;
  }

  public void setCompoundProbabilities(final double[] compoundProbabilities) {
    this.compoundProbabilities = compoundProbabilities;
  }

  /**
   *
   * @return
   */
  public double[] getCompoundConcentrations() {
    return compoundConcentrations;
  }

  public void setCompoundConcentrations(final double[] compoundConcentrations) {
    this.compoundConcentrations = compoundConcentrations;
  }

  /**
   * Gets the compound veto which Shyre might produce
   *
   * @return
   */
  public boolean[] getCompoundVeto() {
    return compoundVeto;
  }

  public void setCompoundVeto(final boolean[] compoundVeto) {
    this.compoundVeto = compoundVeto;
  }

  /**
   *
   * @return
   */
  public int getErrorCondition() {
    return errorCondition;
  }

  public void setErrorCondition(final int errorCondition) {
    this.errorCondition = errorCondition;
  }

  /**
   *
   * @return
   */
  public String getErrorConditionMessage() {
    if (getErrorCondition() >= 0 && getErrorCondition() < ERROR_MSG.length) {
      errorConditionMessage = ERROR_MSG[getErrorCondition()];
      return errorConditionMessage;
    }
    return "Unknown";
  }

  public void setErrorConditionMessage(final String errorConditionMessage) {
    this.errorConditionMessage = errorConditionMessage;
  }

  /**
   *
   * @return
   */
  public long getDatetime() {
    return datetime;
  }

  public void setDatetime(final long datetime) {
    this.datetime = datetime;
  }
}
