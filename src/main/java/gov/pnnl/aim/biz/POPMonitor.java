package gov.pnnl.aim.biz;

import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.avro.dint.POPPiersLOBMessage;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.discovery.streams.StreamUtils;

import javax.ws.rs.core.Response;

public class POPMonitor extends Thread {

	private BusinessQueue queue = null;
	private boolean isRunning = false;
	
	public POPMonitor(BusinessQueue queue) {
		this.queue = queue;
	}
	
	public BusinessQueue getQueue() {
		return this.queue;
	}

	public void setQueue(BusinessQueue queue) {
		this.queue = queue;
	}
	
	public void setRunning(boolean state) {
		isRunning  = state;
	}
	
	public void run() {

		try {
			
			StreamSEI streamWS = ASIConnector.getConnector(BusinessConfig.ASI_USER);
			
			// Read documents forever
			while (true) {

				if (isRunning) {
					try {
						
						// Read a message from the topic
						Response response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_POP_TOPIC, true);
						POPPiersLOBMessage msg = StreamUtils.getPOPMessage(response);
						if (msg != null) {
							queue.queuePOPData(msg);
						} else {
							System.out.println("POP: Nothing on the topic " + BusinessConfig.ASI_POP_TOPIC);
						}
						Thread.sleep(1000);
						
					} catch (Exception ex) {
						System.out.println("*** POP ERROR");
						ex.printStackTrace();
						Thread.sleep(1000);
					}
				} else {
					Thread.sleep(1000);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
