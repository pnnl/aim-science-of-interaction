package gov.pnnl.aim.biz.simulate;

import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.avro.dint.OPAPiersLOBMessage;
import gov.pnnl.aim.avro.dint.POPPiersLOBMessage;
import gov.pnnl.aim.avro.dint.ShyrePiersLOBMessage;
import gov.pnnl.aim.biz.BusinessConfig;
import gov.pnnl.aim.biz.LinesOfBusinessRestService;
import gov.pnnl.aim.discovery.streams.AimUtils;

import java.util.ArrayList;
import java.util.List;

public class SimulateBizInput extends Thread {

	private boolean isRunning = true;
	private String [] companies = new String [] {"ford", "honda", "toyota"};
	//, "adidas", "bmw", "bosch", "chrysler", "costco", "electrolux", "ford", "fredmeyer", "ge", "guess", "gymboree", "hm", "honda", "hyundai", "jcpenney", "jcrew", "kmart", "levi", "lg", "macys", "merged", "nike", "nissan", "oldnavy", "ralphlauren", "sears", "target", "toyota", "vw", "walmart", "whirlpool"};
	private StreamSEI streamWS = null;
	
	
	public static void main(String [] args) {
		SimulateBizInput biz = new SimulateBizInput();
		biz.run();
	}
	
	/**
	 * 
	 */
	public void run() {
		startSimulation();
	}
	
	/**
	 * Starts an endless loop of simulated data
	 */
	private void startSimulation() {
		try {
	    streamWS = AimAfWsClientFactory.generateAimAfStreamClientV2("https", "aim-af.pnl.gov", 8443, BusinessConfig.ASI_USER, BusinessConfig.ASI_PWD);

			// Create all the topics
			streamWS.createDynamicTopic(BusinessConfig.ASI_OPA_TOPIC);
			streamWS.createDynamicTopic(BusinessConfig.ASI_SHYRE_TOPIC);
			streamWS.createDynamicTopic(BusinessConfig.ASI_POP_TOPIC);
		
			// Loop forever sending messages about companies
			while (isRunning) {
				simulateOne();
			}		

			// Remove all the topics
			streamWS.removeDynamicTopic(BusinessConfig.ASI_OPA_TOPIC);
			streamWS.removeDynamicTopic(BusinessConfig.ASI_SHYRE_TOPIC);
			streamWS.removeDynamicTopic(BusinessConfig.ASI_POP_TOPIC);
			
    } catch (Exception e) {
	    e.printStackTrace();
    }
	}

	public void stopSimulation() {
	  isRunning = false;
  }
	
	/**
	 * 
	 * @throws Exception
	 */
	public void simulateOne() throws Exception {

		for (String comp : companies) {

			// Simulate a prediction update from Shyre 
			simShyre(comp);
			
			// Simulate a prediction update from OPA
			simOPA(comp);
			
			// Generate POP alerts randomly with 10% chance
//			if (Math.random() < .1) {
//				simPop(comp);
//			}
		}
	}

	/**
	 * 
	 * @param simComp
	 * @throws Exception
	 */
	public void simPop(String simComp) throws Exception {
		
		// Create a POPPiersLOBMessage object for sending to ASI
		long millis = System.currentTimeMillis();

		POPPiersLOBMessage msg = new POPPiersLOBMessage();
		msg.setCompany(simComp);
		msg.setDate(millis);
		msg.setLinesofbusiness("Clothing");
		msg.setScore(1.0);
		msg.setUsermsg("Sears is behaving strangely");
		
		
		// Send it
		System.out.println("POP Starting send: " + msg.getCompany());
		streamWS.submitDynamicTopicRecord(BusinessConfig.ASI_POP_TOPIC, AimUtils.serializeAvroRecordToByteBuffer(msg).array());
		System.out.println(" Sent! " + msg.getCompany());
	}

	/**
	 * 
	 * @param simComp
	 * @throws Exception
	 */
	public void simShyre(String simComp) throws Exception {
		
		// Create a ShyrePiersLOBMessage object for sending to ASI
		List<CharSequence> comps = new ArrayList<CharSequence>();
		List<Double> probs = new ArrayList<Double>();
		
		for (String c : LinesOfBusinessRestService.linesOfBusiness) {
			comps.add(c);
			probs.add(Math.random());
		}
		
		List<CharSequence> recordids = new ArrayList<CharSequence>();
		recordids.add("1234");
		recordids.add("5678");

		List<CharSequence> hscodes = new ArrayList<CharSequence>();
		recordids.add("1234");
		recordids.add("5678");
		
		ShyrePiersLOBMessage msg = new ShyrePiersLOBMessage();
		msg.setCompany(simComp);
		msg.setExamplarCompany("Gap");
		msg.setExpectedCount(10L);
		msg.setIsHighScore(true);
		msg.setLob("Clothing");
		msg.setProbability(Math.random());
		msg.setScore(Math.random());
		msg.setRecordids(recordids);
		msg.setHscodes(hscodes);
		
		// Send it
		System.out.println("Starting send: " + msg.getCompany());
		streamWS.submitDynamicTopicRecord(BusinessConfig.ASI_SHYRE_TOPIC, AimUtils.serializeAvroRecordToByteBuffer(msg).array());
		System.out.println("Sent! " + msg.getCompany());

		streamWS.removeDynamicTopic(BusinessConfig.ASI_SHYRE_TOPIC);
	}

	/**
	 * 
	 * @param simComp
	 * @throws Exception
	 */
	public void simOPA(String simComp) throws Exception {
		
		// Create a OPAPiersLOBMessage object for sending to ASI
		long millis = System.currentTimeMillis();

		List<CharSequence> comps = new ArrayList<CharSequence>();
		List<Double> probs = new ArrayList<Double>();
		
		for (String c : LinesOfBusinessRestService.linesOfBusiness) {
			comps.add(c);
			probs.add(Math.random());
		}
		
		OPAPiersLOBMessage msg = new OPAPiersLOBMessage();
		msg.setCompany(simComp);
		msg.setStartdate(millis - 60000);
		msg.setEnddate(millis);
		msg.setLinesofbusiness(comps);
		msg.setProbabilities(probs);
		
		// Send it
		streamWS.submitDynamicTopicRecord(BusinessConfig.ASI_OPA_TOPIC, AimUtils.serializeAvroRecordToByteBuffer(msg).array());
	}
}
