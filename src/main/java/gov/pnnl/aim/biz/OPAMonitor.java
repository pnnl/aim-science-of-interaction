package gov.pnnl.aim.biz;

import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.avro.dint.OPAPiersLOBMessage;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.discovery.streams.StreamUtils;

import javax.ws.rs.core.Response;

public class OPAMonitor extends Thread {

	private BusinessQueue queue = null;
	private boolean isRunning = false;
	
	public OPAMonitor(BusinessQueue queue) {
		this.queue = queue;
	}
	
	public BusinessQueue getQueue() {
		return this.queue;
	}

	public void setQueue(BusinessQueue queue) {
		this.queue = queue;
	}
	
	public void setRunning(boolean state) {
		isRunning = state;
	}
	
	public void run() {

		try {
			
			StreamSEI streamWS = ASIConnector.getConnector(BusinessConfig.ASI_USER);
			
			// Read documents forever
			while (true) {

				if (isRunning) {
					try {

						// Read a message from the topic
						Response response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_OPA_TOPIC, true);
						OPAPiersLOBMessage msg = StreamUtils.getOPAMessage(response);
						if (msg != null) {
							queue.queueOPAData(msg);
						} else {
							System.out.println("OPA: Nothing on the topic " + BusinessConfig.ASI_OPA_TOPIC);
						}
						Thread.sleep(1000);
						
					} catch (Exception ex) {
						System.out.println("*** OPA ERROR");
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
