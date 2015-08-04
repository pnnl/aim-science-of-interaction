package gov.pnnl.aim.nmr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hamp645
 */
public class NMRSample {
  /** Unique identifier for the sample */
  private String sampleID = "";

  /** Datetime the sample was processed by the NMR */
  private long datetime = 0;

  /** The set of compounds measured in this sample */
  private List<NMRCompoundMeasure> measures = new ArrayList<NMRCompoundMeasure>();

  /** The most recently receive spectra */
  private NMRSpectra spectra = new NMRSpectra();

  /** This is the error condition code reported from one of the algorithms */
  private NMRError error = new NMRError();

  /**
   * This generates random data for testing
   */
  public NMRSample() {
  }

  /**
   * @return the sampleID
   */
  public String getSampleID() {
    return sampleID;
  }

  /**
   * @param sampleID
   *          the sampleID to set
   */
  public void setSampleID(final String sampleID) {
    this.sampleID = sampleID;
  }

  /**
   * @return the datetime
   */
  public long getDatetime() {
    return datetime;
  }

  /**
   * @param datetime
   *          the datetime to set
   */
  public void setDatetime(final long datetime) {
    this.datetime = datetime;
  }

  /**
   * @return the measures
   */
  public List<NMRCompoundMeasure> getMeasures() {
    return measures;
  }

  /**
   * @param measures
   *          the measures to set
   */
  public void setMeasures(final List<NMRCompoundMeasure> measures) {
    this.measures = measures;
  }

  /**
   * @return the spectra
   */
  public NMRSpectra getSpectra() {
    return spectra;
  }

  /**
   * @param spectra
   *          the spectra to set
   */
  public void setSpectra(final NMRSpectra spectra) {
    this.spectra = spectra;
  }

  /**
   * @return the error
   */
  public NMRError getError() {
    return error;
  }

  /**
   * @param error
   *          the error to set
   */
  public void setError(final NMRError error) {
    this.error = error;
  }
}
