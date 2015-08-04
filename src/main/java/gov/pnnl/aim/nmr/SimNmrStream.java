package gov.pnnl.aim.nmr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.biz.BusinessConfig;
import gov.pnnl.aim.discovery.streams.AimUtils;
import gov.pnnl.aim.nmr.avro.NMRQualityStatusSpectrum;
import gov.pnnl.aim.nmr.avro.OpaNmrMessage;
import gov.pnnl.aim.shyre.stream.avro.ShyreNmrMessage;

public class SimNmrStream {

	private final static String[] COMPOUNDS = new String[] { 
			"1-Methylhistidine", "2-Ketobutyric acid", "Deoxyuridine", "4-Pyridoxic acid", "Alpha-ketoisovaleric acid", 
			"3-Methoxytyramine", "(S)-3-Hydroxyisobutyric acid", "Ureidopropionic acid", "Carnosine", "Adenine", "Butyric acid", 
			"Acetic acid", "Betaine", "Melibiose", "Adenosine", "Androstenedione", "Cellobiose", "Cyclic AMP", "Acetoacetic acid", 
			"Adenosine 3\'5\'-diphosphate", "Creatine", "Cholesterol", "Pipecolic acid", "Deoxyinosine", "Dihydrouracil", 
			"Dehydroepiandrosterone", "Glycerophosphocholine", "Dimethylamine", "Cytidine", "Dimethylglycine" };
	
  private final static int COMPOUND_COUNT = 30;
  private final static int SPECTRA_POINT_COUNT = 200;
  
	public static void main(String[] args) throws Exception {
		
		StreamSEI streamWS = AimAfWsClientFactory.generateAimAfStreamClientV2("https", "aim-af.pnl.gov", 8443, BusinessConfig.ASI_USER, BusinessConfig.ASI_PWD);

		simCA(streamWS);
		simOPA(streamWS);
		simShyre(streamWS);
	}
	
	/**
	 * 
	 * @param streamWS
	 * @throws IOException
	 */
	public static void simCA(StreamSEI streamWS) throws IOException {
		
		// Create all the topics
		streamWS.createDynamicTopic(NMRConfig.ASI_CA_TOPIC);

		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < SPECTRA_POINT_COUNT; i++) {
			values.add(Math.random());
		}
		
		NMRQualityStatusSpectrum msg = new NMRQualityStatusSpectrum();
		msg.setFileName("data.csv");
		msg.setQualityStatus((int) (Math.random() * 3));
		msg.setRowNumber(1);
		msg.setSampleID("Sample-X");
		msg.setTimestamp(System.currentTimeMillis());
		msg.setVersion(1);
		msg.setSpectrum(values);		
		
		// Send it
		System.out.println("Starting send: " + msg.getSampleID());
		streamWS.submitDynamicTopicRecord(NMRConfig.ASI_CA_TOPIC, AimUtils.serializeAvroRecordToByteBuffer(msg).array());
		System.out.println("Sent! " + msg.getSampleID());

		streamWS.removeDynamicTopic(NMRConfig.ASI_CA_TOPIC);
	}

	/**
	 * 
	 * @param streamWS
	 * @throws IOException
	 */
	public static void simOPA(StreamSEI streamWS) throws IOException {
		
		// Create all the topics
		streamWS.createDynamicTopic(NMRConfig.ASI_OPA_TOPIC);
	
		OpaNmrMessage msg = new OpaNmrMessage();
		msg.setFileName("data.csv");
		msg.setRowNumber(1);
		msg.setUuid("Sample-X");
		msg.setTimestamp(Long.toString(System.currentTimeMillis()));
		msg.setVersion(1);

		List<Double> probabilities = new ArrayList<Double>();
		List<Double> abundances = new ArrayList<Double>();
		for (int i = 0; i < COMPOUND_COUNT; i++) {
			probabilities.add(Math.random());
			abundances.add(Math.random());
		}
		
		msg.setProbabilities(probabilities);
		msg.setAbundances(abundances);
		
		// Send it
		System.out.println("Starting send: " + msg.getUuid());
		streamWS.submitDynamicTopicRecord(NMRConfig.ASI_OPA_TOPIC, AimUtils.serializeAvroRecordToByteBuffer(msg).array());
		System.out.println("Sent! " + msg.getUuid());

		streamWS.removeDynamicTopic(NMRConfig.ASI_OPA_TOPIC);
	}

	public static void simShyre(StreamSEI streamWS) throws IOException {
		
		// Create all the topics
		streamWS.createDynamicTopic(NMRConfig.ASI_SHYRE_TOPIC);
	
		for (int i = 0; i < 3; i++) {
			ShyreNmrMessage msg = new ShyreNmrMessage();
			msg.setCompound(COMPOUNDS[(int) (Math.random() * COMPOUNDS.length)]);
			msg.setPresent(Math.random() < .8);
			msg.setCount(i + 1);
			msg.setTotal(3);
			msg.setSpectra("Sample-X");
			
			// Send it
			System.out.println("Starting send: " + msg.getCompound());
			streamWS.submitDynamicTopicRecord(NMRConfig.ASI_SHYRE_TOPIC, AimUtils.serializeAvroRecordToByteBuffer(msg).array());
			System.out.println("Sent! " + msg.getCompound());
		}

		streamWS.removeDynamicTopic(NMRConfig.ASI_SHYRE_TOPIC);
	}
}
