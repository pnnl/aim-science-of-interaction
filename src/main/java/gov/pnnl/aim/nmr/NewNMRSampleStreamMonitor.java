package gov.pnnl.aim.nmr;

import gov.pnnl.aim.af.avro.NmrOpaStreamRecord;
import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.nmr.avro.OpaNmrMessage;
import gov.pnnl.aim.shyre.stream.avro.ShyreNmrMessage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

/**
 * This is the main class for monitoring the ASI for messages from CA, OPA, and Shyre.
 * Those messages are then integrated into the NMRReactorState object which is requested
 * frequently from the UI to update the display.
 *
 * @author D3K199
 *
 */
public class NewNMRSampleStreamMonitor {

  public final static String[] COMPOUNDS = new String[] { "1-Methylhistidine", "2-Ketobutyric acid", "Deoxyuridine", "4-Pyridoxic acid", "Alpha-ketoisovaleric acid", "3-Methoxytyramine", "(S)-3-Hydroxyisobutyric acid", "Ureidopropionic acid", "Carnosine", "Adenine", "Butyric acid", "Acetic acid", "Betaine", "Melibiose", "Adenosine", "Androstenedione", "Cellobiose", "Cyclic AMP", "Acetoacetic acid", "Adenosine 3\'5\'-diphosphate", "Creatine", "Cholesterol", "Pipecolic acid", "Deoxyinosine", "Dihydrouracil", "Dehydroepiandrosterone", "Glycerophosphocholine", "Dimethylamine", "Cytidine", "Dimethylglycine" };

  private final static Map<String, String> compoundNameMap = new HashMap<String, String>();

  static {
    compoundNameMap.put("HMDB00001", "1-Methylhistidine");
    compoundNameMap.put("HMDB00005", "2-Ketobutyric acid");
    compoundNameMap.put("HMDB00012", "Deoxyuridine");
    compoundNameMap.put("HMDB00017", "4-Pyridoxic acid");
    compoundNameMap.put("HMDB00019", "Alpha-ketoisovaleric acid");
    compoundNameMap.put("HMDB00022", "3-Methoxytyramine");
    compoundNameMap.put("HMDB00023", "(S)-3-Hydroxyisobutyric acid");
    compoundNameMap.put("HMDB00026", "Ureidopropionic acid");
    compoundNameMap.put("HMDB00033", "Carnosine");
    compoundNameMap.put("HMDB00034", "Adenine");
    compoundNameMap.put("HMDB00039", "Butyric acid");
    compoundNameMap.put("HMDB00042", "Acetic acid");
    compoundNameMap.put("HMDB00043", "Betaine");
    compoundNameMap.put("HMDB00048", "Melibiose");
    compoundNameMap.put("HMDB00050", "Adenosine");
    compoundNameMap.put("HMDB00053", "Androstenedione");
    compoundNameMap.put("HMDB00055", "Cellobiose");
    compoundNameMap.put("HMDB00058", "Cyclic AMP");
    compoundNameMap.put("HMDB00060", "Acetoacetic acid");
    compoundNameMap.put("HMDB00061", "Adenosine 3',5'-diphosphate");
    compoundNameMap.put("HMDB00064", "Creatine");
    compoundNameMap.put("HMDB00067", "Cholesterol");
    compoundNameMap.put("HMDB00070", "Pipecolic acid");
    compoundNameMap.put("HMDB00071", "Deoxyinosine");
    compoundNameMap.put("HMDB00076", "Dihydrouracil");
    compoundNameMap.put("HMDB00077", "Dehydroepiandrosterone");
    compoundNameMap.put("HMDB00086", "Glycerophosphocholine");
    compoundNameMap.put("HMDB00087", "Dimethylamine");
    compoundNameMap.put("HMDB00089", "Cytidine");
    compoundNameMap.put("HMDB00092", "Dimethylglycine");
  }

  public final static String[] STATUS = new String[] { "Compressive Analysis process has failed. Data is being passed as-is.", "All is well. This scan is within expected values and Compressive Analysis is running properly.", "Fault detected. This scan is outside of expected values due most likely to a failure of some type in the instrument.", "Point of diminishing returns. Compressive Analysis no longer sees significant change in the summed scan so the scanning ended early." };

  public final static int[] STATUS_SWAP = new int[] { 1, 0, 2, 3 };

  private final Set<String> knownCompounds = new HashSet<String>();

  private NMRReactorState state = new NMRReactorState();

  private final Map<String, NMRSample> mapSampleIDToSample = new HashMap<String, NMRSample>();
  
  private final Set<String> mapSampleIDHasOPA = new HashSet<String>();

  public NewNMRSampleStreamMonitor() {
    // Create an easy way for us to see if a compound is known or not
    for (String c : COMPOUNDS) {
      knownCompounds.add(c.toLowerCase());
    }

    // Create monitors for each algorithm
    CAMonitor caMonitor = new CAMonitor();
    OPAMonitor opaMonitor = new OPAMonitor();
    ShyreMonitor shyreMonitor = new ShyreMonitor();

    // Start listening for messages
    caMonitor.start();
    opaMonitor.start();
    shyreMonitor.start();
  }

  public static String getStatus(final int code) {
    if (code >= 0 && code < NewNMRSampleStreamMonitor.STATUS.length) {
      return NewNMRSampleStreamMonitor.STATUS[code];
    }
    return "Unknown Compressive Analysis Status Code: " + code;
  }

  /**
   * See if a compound name is in our known compounds list
   *
   * @param compound
   * @return
   */
  public boolean isKnownCompound(final String compound) {
    return knownCompounds.contains(compound.toLowerCase());
  }

  /**
   * @return
   */
  public List<String> getCompundNames() {
    return Arrays.asList(COMPOUNDS);
  }

  /**
   *
   * @return
   */
  public NMRReactorState getNMRReactorState() {
    state.updateRanges();
    return state;
  }

  /**
   *
   */
  private void incorporateCompressiveAnalysisMessage(final NmrOpaStreamRecord msg) {
    String sampleID = msg.getRunNumber().toString();
    NMRSample currentSample = getSample(sampleID);

    NMRError error = new NMRError();
    error.setCode(msg.getStatus().intValue());
    error.setMessage(getStatus(msg.getStatus().intValue()));

    List<NMRPoint> points = null;
    if (NMRConfig.NORMALIZE_SPECTRUM) {
      points = normalizeSpectrum(msg.getValues());
    } else {
      points = asNMRPointList(msg.getValues());
    }

    NMRSpectra spectra = new NMRSpectra();
    spectra.setPoints(points);

    //System.out.println("CA: Sample: " + sampleID + ", Error Code: " + msg.getStatus().intValue() + ", Spectrum Length: " + msg.getValues().size());

    // Update our current sample
    currentSample.setError(error);
    currentSample.setSpectra(spectra);
    currentSample.setDatetime(msg.getTimestamp().longValue());

    // Rebuild the reactor state from the hashmap so the client can update
    rebuildNMRReactorState();
  }

  private void incorporateShyreMessage(final ShyreNmrMessage msg) {
    String sampleID = msg.getSpectra().toString();
    NMRSample currentSample = getSample(sampleID);

    List<NMRCompoundMeasure> measures = currentSample.getMeasures();

    String compoundName = msg.getCompound().toString();
    String compoundID = compoundName;
    compoundName = compoundNameMap.get(compoundName); // rename to names the UI understands
    boolean isPresent = msg.getPresent();
    System.out.println("SHYRE: Sample: " + sampleID + ", Compound: " + compoundID + ", " + compoundName + ", IsPresent: " + isPresent);

    // Get the measure for this compound
    NMRCompoundMeasure measure = getMeasure(measures, compoundName);

    // If not found, create and add it
    if (measure == null) {
      measure = new NMRCompoundMeasure();
      measure.setCompoundName(compoundName);
      measures.add(measure);
      System.out.println("**** Bad compound: " + compoundName);
      return;
    }

    // Update the veto status for this compound
    measure.setVetoed(isPresent == false);

    // Rebuild the reactor state from the hashmap so the client can update
    rebuildNMRReactorState();
  }

  private void incorporateOPAMessage(final OpaNmrMessage msg) {
    String sampleID = msg.getRunNumber().toString();
    NMRSample currentSample = getSample(sampleID);

    List<NMRCompoundMeasure> currentMeasures = currentSample.getMeasures();
    List<Double> abundances = msg.getAbundances();
    List<Double> probabilities = msg.getProbabilities();

    if (COMPOUNDS.length != abundances.size()) {
      System.out.println("*** OPA WARNING ... abundances-compounsd mismatch in length.  abundances = " + abundances.size() + " compounds = " + COMPOUNDS.length);
    }
    if (COMPOUNDS.length != probabilities.size()) {
      System.out.println("*** OPA WARNING ... probabilities-compounds mismatch in length.  probabilities = " + probabilities.size() + " compounds = " + COMPOUNDS.length);
    }

    String logProbs = "";
    String logAbs = "";
    for (int i = 0; i < COMPOUNDS.length; i++) {
      NMRCompoundMeasure m = currentMeasures.get(i);
      if (i < abundances.size()) {
        logAbs += abundances.get(i) + " ";
        m.setConcentration(abundances.get(i));
      }
      if (i < probabilities.size()) {
        logProbs += probabilities.get(i) + " ";
        double prob = probabilities.get(i);
        m.setProbability(prob);
        if (prob < .7) {
          m.setVetoed(true);
        }
      }
    }
    
    mapSampleIDHasOPA.add(sampleID);
    

    System.out.println("OPA: Sample: " + sampleID + ", Probabilities: " + logProbs + ", Abundances: " + logAbs);

    // Rebuild the reactor state from the hashmap so the client can update
    rebuildNMRReactorState();
  }

  /**
   * This method converts the input from CA to the pojo SOI needs
   *
   * @param spectrumValues
   * @return
   */
  private List<NMRPoint> asNMRPointList(final List<Double> spectrumValues) {
    List<NMRPoint> points = new ArrayList<NMRPoint>();
    for (int i = 0; i < spectrumValues.size(); i++) {
      points.add(new NMRPoint(i, spectrumValues.get(i)));
      System.out.print(spectrumValues.get(i));
      System.out.print(" ");
    }
    System.out.println();
    // return points;
    // List<NMRPoint> points = new ArrayList<NMRPoint>();
    // for (int i = 0; i < 1000; i++) {
    // points.add(new NMRPoint(i, Math.random()));
    // }
    return points;
  }

  /**
   * This method converts the input from CA to the pojo SOI needs
   * and normalizes the spectrum from [0,1]
   *
   * @param spectrumValues
   * @return
   */
  private List<NMRPoint> normalizeSpectrum(final List<Double> spectrumValues) {
    // Convert the incoming summed spectrum values to our NMRSpectra
    double minVal = Double.MAX_VALUE;
    double maxVal = -Double.MAX_VALUE;
    for (double d : spectrumValues) {
      minVal = Math.min(minVal, d);
      maxVal = Math.max(maxVal, d);
    }
    double delta = maxVal - minVal;

    List<NMRPoint> points = new ArrayList<NMRPoint>();
    for (int i = 0; i < spectrumValues.size(); i++) {
      double val = spectrumValues.get(i).doubleValue();
      double scaledVal = (val - minVal) / delta;
      if (scaledVal < 0 || scaledVal > 1) {
        System.out.println("Not good");
      }
      points.add(new NMRPoint(i, scaledVal));
    }
    return points;
  }

  /**
   * This method uses the hashmap of NMR samples to update the NMRReactorState.
   * The purpose of this is to handle AIM models sending updates to different samples at
   * different times. The map allows us to update any sample at any time.
   * When we rebuild the reactor state, the sample with the largest (assumed of type Long)
   * sample ID is the current sample.
   */
  private void rebuildNMRReactorState() {
    NMRReactorState stateFresh = new NMRReactorState();

    List<String> sampleIds = new ArrayList<>(mapSampleIDToSample.keySet());
    sampleIds.sort(new Comparator<String>() {
      @Override
      public int compare(final String o1, final String o2) {
        return Long.compare(Long.parseLong(o1), Long.parseLong(o2));
      }
    });

    System.out.println("Samples " + sampleIds);
    for(int i = sampleIds.size() - 1; i >= 0; i--) {
      String sampleID = sampleIds.get(i);
      if (mapSampleIDHasOPA.contains(sampleID)) {
	stateFresh.getHistory().add(mapSampleIDToSample.get(sampleID));
      }
    }

    state = stateFresh;
  }

  NMRSample getSample(final String sampleID) {
    NMRSample currentSample = null;
    if (mapSampleIDToSample.containsKey(sampleID)) {
      currentSample = mapSampleIDToSample.get(sampleID);
    } else {
      currentSample = new NMRSample();
      currentSample.setSampleID(sampleID);
      List<NMRCompoundMeasure> measures = new ArrayList<NMRCompoundMeasure>();
      for (String compoundName : COMPOUNDS) {
        NMRCompoundMeasure measure = new NMRCompoundMeasure();
        measure.setCompoundName(compoundName);
        measures.add(measure);
      }
      currentSample.setMeasures(measures);
      mapSampleIDToSample.put(sampleID, currentSample);
    }
    return currentSample;
  }

  /**
   *
   * @param measures
   * @param compoundName
   * @return
   */
  private NMRCompoundMeasure getMeasure(final List<NMRCompoundMeasure> measures, final String compoundName) {
    for (NMRCompoundMeasure measure : measures) {
      if (measure.getCompoundName().equalsIgnoreCase(compoundName)) {
        return measure;
      }
    }
    return null;
  }

  /**
   * This thread monitors for Compressive Analysis NMR messages
   */
  public class CAMonitor extends Thread {
    @Override
    public void run() {
      StreamSEI streamWS = ASIConnector.getConnector(NMRConfig.ASI_USER);

      // Read documents forever
      while (true) {
        try {
          // Read a message from the topic
          Response response = streamWS.retrieveDynamicTopicRecord(NMRConfig.ASI_CA_TOPIC, true);
          final InputStream in = (InputStream) response.getEntity();
          final byte[] result = IOUtils.toByteArray(in);

          if (result.length > 0) {
            NmrOpaStreamRecord spectrum = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, NmrOpaStreamRecord.class);
            incorporateCompressiveAnalysisMessage(spectrum);

            // Sleep a little so we don't overload ASI
            try {
              Thread.sleep(NMRConfig.SLEEP_ON_DATA_MILLIS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

          } else {
            // The topic is probably empty, so sleep for some time and try again
            try {
              Thread.sleep(NMRConfig.SLEEP_ON_NODATA_MILLIS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }

        } catch (Exception ex) {
          System.out.println("*** CA ERROR");
          ex.printStackTrace();
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * This thread monitors for OPA NMR messages
   */
  public class OPAMonitor extends Thread {

    @Override
    public void run() {
      StreamSEI streamWS = ASIConnector.getConnector(NMRConfig.ASI_USER);

      // Read documents forever
      while (true) {

        try {

          // Read a message from the topic
          Response response = streamWS.retrieveDynamicTopicRecord(NMRConfig.ASI_OPA_TOPIC, true);
          final InputStream in = (InputStream) response.getEntity();
          final byte[] result = IOUtils.toByteArray(in);
          if (result.length > 0) {

            final OpaNmrMessage msg = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, OpaNmrMessage.class);
            incorporateOPAMessage(msg);

            // Sleep a little so we don't overload ASI
            try {
              Thread.sleep(NMRConfig.SLEEP_ON_DATA_MILLIS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          } else {
            try {
              Thread.sleep(NMRConfig.SLEEP_ON_NODATA_MILLIS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }

        } catch (Exception ex) {
          System.out.println("*** OPA ERROR");
          ex.printStackTrace();
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * This thread monitors for Shyre NMR messages
   */
  public class ShyreMonitor extends Thread {

    @Override
    public void run() {
      StreamSEI streamWS = ASIConnector.getConnector(NMRConfig.ASI_USER);

      // Read documents forever
      while (true) {

        try {

          // Read a message from the topic
          Response response = streamWS.retrieveDynamicTopicRecord(NMRConfig.ASI_SHYRE_TOPIC, true);
          final InputStream in = (InputStream) response.getEntity();
          final byte[] result = IOUtils.toByteArray(in);
          if (result.length > 0) {

            final ShyreNmrMessage msg = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, ShyreNmrMessage.class);
            incorporateShyreMessage(msg);

            // Sleep a little so we don't overload ASI
            try {
              Thread.sleep(NMRConfig.SLEEP_ON_DATA_MILLIS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

          } else {
            try {
              Thread.sleep(NMRConfig.SLEEP_ON_NODATA_MILLIS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }

        } catch (Exception ex) {
          System.out.println("*** SHYRE ERROR");
          ex.printStackTrace();
          try {
            Thread.sleep(NMRConfig.SLEEP_ON_ERROR_MILLIS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
