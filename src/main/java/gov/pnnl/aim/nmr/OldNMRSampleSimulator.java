/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.nmr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hamp645
 *
 */
public class OldNMRSampleSimulator {
  private final static int SPECTRA_PER = 3;

  private final static int COMPOUND_COUNT = 30;

  private final static int SPECTRA_POINT_COUNT = 1024;

  private int sampleIndex = 0;

  private int spectraIndex = 0;

  private final OldNMRReactorState state = new OldNMRReactorState();

  /**
   * @return
   */
  public List<String> getCompundNames() {
    return Arrays.asList(OldNMRSampleStreamMonitor.COMPOUNDS);
  }

  /**
   * @return the next state to send to the client
   */
  public OldNMRReactorState nextState() {
    updateToNextState();
    return state;
  }

  private void updateToNextState() {
    if (state.getCurrent() == null) {
      // We have no sample data, so get started
      NMRSample newSample = createNewSample();
      state.setCurrent(newSample);
    } else {
      if (spectraIndex % SPECTRA_PER != 0) {
        // new spectra, old sample
        NMRSpectra newSpectra = createNewSpectra();
        updateSample(newSpectra);
      } else {
        // new spectra, from new sample
        state.getHistory().add(0, state.getCurrent());
        if (state.getHistory().size() > 10) {
          state.getHistory().remove(10);
        }
        NMRSample newSample = createNewSample();
        state.setCurrent(newSample);
      }
    }
  }

  /**
   * @param newSpectra
   */
  private void updateSample(final NMRSpectra newSpectra) {
    state.getCurrent().setDatetime(System.currentTimeMillis());
    state.getCurrent().setError(createNewError());
    state.getCurrent().setMeasures(createNewMeasures());
    state.getCurrent().setSpectra(newSpectra);
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
    sample.setSampleID("HDB" + sampleIndex);
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
