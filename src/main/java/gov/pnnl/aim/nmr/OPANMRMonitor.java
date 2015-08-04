package gov.pnnl.aim.nmr;

import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.discovery.streams.StreamUtils;
import gov.pnnl.aim.streams.avro.CompoundPredictMessage;

import java.util.List;

import javax.ws.rs.core.Response;

public class OPANMRMonitor {

	public static void main(String[] args) throws Exception {
		
		consumeNMR();
	}
	
	public static void consumeNMR() throws InterruptedException { 
		
		StreamSEI streamWS = ASIConnector.getConnector(NMRConfig.ASI_USER);
		
		int counter = 0;
		
		// Read documents forever
		while (true) {
	
			try {

				// Read a message from the topic
				Response response = streamWS.retrieveDynamicTopicRecord(NMRConfig.ASI_OPA_TOPIC, true);
				CompoundPredictMessage msg = StreamUtils.getOPANMRMessage(response);
				
				List<Double> probs = msg.getProbabilities();
				
				counter++;

				System.out.print(counter);
				for (double prob : probs) {
					System.out.print(", " + prob);
				}
				
			} catch (Exception ex) {
				//ex.printStackTrace();
				Thread.sleep(1000);
			}
		}
	}
}
