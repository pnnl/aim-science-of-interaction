package gov.pnnl.aim.nmr;

import java.util.ArrayList;
import java.util.List;

public class NMRSpectra {
  private String spectraId;

  private long timestamp;

  private List<NMRPoint> points = new ArrayList<NMRPoint>();

  public NMRSpectra() {

  }

  /**
   * @return the spectraId
   */
  public String getSpectraId() {
    return spectraId;
  }

  /**
   * @param spectraId
   *          the spectraId to set
   */
  public void setSpectraId(final String spectraId) {
    this.spectraId = spectraId;
  }

  /**
   * @return the timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp
   *          the timestamp to set
   */
  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * @return the points
   */
  public List<NMRPoint> getPoints() {
    return points;
  }

  /**
   * @param points
   *          the points to set
   */
  public void setPoints(final List<NMRPoint> points) {
    this.points = points;
  }
}
