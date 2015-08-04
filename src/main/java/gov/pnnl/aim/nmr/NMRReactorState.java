/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.nmr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hamp645
 */
public class NMRReactorState {
  private List<NMRAbundanceRange> ranges = new ArrayList<>();

  private List<NMRSample> history = new ArrayList<>();

  /**
   *
   */
  public NMRReactorState() {
  }

  /**
   * @return the ranges
   */
  public List<NMRAbundanceRange> getRanges() {
    return ranges;
  }

  /**
   * @param ranges
   *          the ranges to set
   */
  public void setRanges(final List<NMRAbundanceRange> ranges) {
    this.ranges = ranges;
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

  public void updateRanges() {
    Map<String, NMRAbundanceRange> map = new HashMap<>();
    for (NMRSample s : getHistory()) {
      for (NMRCompoundMeasure m : s.getMeasures()) {
        NMRAbundanceRange range = map.get(m.getCompoundName());
        if (range == null) {
          range = new NMRAbundanceRange();
          range.setMin(m.getConcentration());
          range.setMax(m.getConcentration());
          range.setCompound(m.getCompoundName());
          map.put(m.getCompoundName(), range);
        } else {
          if (m.getConcentration() < range.getMin()) {
            range.setMin(m.getConcentration());
          }
          if (m.getConcentration() > range.getMax()) {
            range.setMax(m.getConcentration());
          }
        }
      }
    }

    setRanges(new ArrayList<>(map.values()));
  }
  
  public NMRSample getSample(String sampleID) {
	  for (NMRSample sample : history) {
		  if (sample.getSampleID().equalsIgnoreCase(sampleID)) {
			  return sample;
		  }
	  }
	  return null;
  }
}
