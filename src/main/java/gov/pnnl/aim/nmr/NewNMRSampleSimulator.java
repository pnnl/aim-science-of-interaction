/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.nmr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hamp645
 *
 */
public class NewNMRSampleSimulator {
  private final static int SPECTRA_PER = 3;

  private final static int COMPOUND_COUNT = 30;

  private final static int SPECTRA_POINT_COUNT = 1024;

  private int sampleIndex = 0;

  private int spectraIndex = 0;

  private final Map<String, NMRSample> samples = new HashMap<String, NMRSample>();

  /**
   * @return
   */
  public List<String> getCompundNames() {
    return Arrays.asList(OldNMRSampleStreamMonitor.COMPOUNDS);
  }

  /**
   * @return the next state to send to the client
   */
  public NMRReactorState nextState() {
    updateToNextState();

    NMRReactorState state = new NMRReactorState();

    List<String> sampleIds = new ArrayList<>(samples.keySet());
    sampleIds.sort(new Comparator<String>() {
      @Override
      public int compare(final String o1, final String o2) {
        return Long.compare(Long.parseLong(o1), Long.parseLong(o2));
      }
    });

    for (int i = samples.size() - 1; i > 0; i--) {
      state.getHistory().add(samples.get(sampleIds.get(i)));
    }

    System.out.println("Samples " + samples.keySet());
    return state;
  }

  private void updateToNextState() {
    if (samples.size() == 0 ) {
      NMRSample newSample = createNewSample();
      samples.put(newSample.getSampleID(), newSample);
      return;
    }

    List<String> sampleIds = new ArrayList<>(samples.keySet());
    sampleIds.sort(new Comparator<String>() {
      @Override
      public int compare(final String o1, final String o2) {
        return Long.compare(Long.parseLong(o1), Long.parseLong(o2));
      }
    });

    if (Math.random() > 0.5) {
      int index = (int) Math.floor(Math.random() * samples.size());
      String key = sampleIds.get(index);
      NMRSample s = samples.get(key);
      updateSample(s);
    } else {
      NMRSample newSample = createNewSample();
      samples.put(newSample.getSampleID(), newSample);
    }
  }

  /**
   * @param newSpectra
   */
  private void updateSample(final NMRSample sample) {
    sample.setDatetime(System.currentTimeMillis());
    sample.setError(createNewError());
    sample.setMeasures(createNewMeasures());
    sample.setSpectra(createNewSpectra());
  }

  /**
   * @return
   */
  private NMRSpectra createNewSpectra() {
    NMRSpectra s = new NMRSpectra();
    s.setSpectraId("SP-" + spectraIndex);
    s.setPoints(createSpectraPoints());
    s.setTimestamp(System.currentTimeMillis());
    spectraIndex++;
    return s;
  }

  /**
   * @return
   */
  private List<NMRPoint> createSpectraPoints() {
    List<NMRPoint> points = new ArrayList<NMRPoint>();
    for(int i = 0; i < SPECTRA_POINT_COUNT; i++) {
      points.add(new NMRPoint(i, Math.random()));
    }
    return points;
  }

  /**
   * @return
   */
  private NMRSample createNewSample() {
    NMRSample sample = new NMRSample();
    sample.setSampleID(Integer.toString(sampleIndex));
    sample.setDatetime(System.currentTimeMillis());
    sample.setError(createNewError());
    sample.setSpectra(createNewSpectra());
    sample.setMeasures(createNewMeasures());
    sampleIndex++;
    return sample;
  }

  /**
   * @return
   */
  private List<NMRCompoundMeasure> createNewMeasures() {
    List<NMRCompoundMeasure> measures = new ArrayList<>();
    for(int i = 0; i < COMPOUND_COUNT; i++) {
      measures.add(createNewMeasure(i));
    }
    return measures;
  }

  /**
   * @param i
   * @return
   */
  private NMRCompoundMeasure createNewMeasure(final int i) {
    NMRCompoundMeasure measure = new NMRCompoundMeasure();
    measure.setCompoundName(OldNMRSampleStreamMonitor.COMPOUNDS[i]);
    measure.setProbability(Math.random());
    measure.setConcentration(Math.random());
    measure.setVetoed(Math.random() < 0.25);
    return measure;
  }

  /**
   * @return
   */
  private NMRError createNewError() {
    NMRError error = new NMRError();
    double r = Math.random();
    if (r < .5) {
      error.setCode(1);
    } else if (r < .8) {
      error.setCode(3);
    } else {
      error.setCode((Math.random() < .5) ? 0 : 2);
    }
    error.setMessage(OldNMRSampleStreamMonitor.STATUS[error.getCode()]);
    return error;
  }
}
