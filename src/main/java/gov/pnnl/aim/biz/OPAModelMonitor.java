package gov.pnnl.aim.biz;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import gov.pnnl.aim.af.avro.ModelMessage;
import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.streams.ASIConnector;

public class OPAModelMonitor extends Thread {

	private BusinessQueue queue = null;
	private boolean isRunning = false;
	
	public OPAModelMonitor(BusinessQueue queue) {
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
						Response response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_OPA_MODEL_TOPIC, true);

						// De-serialize and display message
						final InputStream in = (InputStream) response.getEntity();
						final byte[] result = IOUtils.toByteArray(in);
						ModelMessage msg = null;
						if (result.length > 0) {
							msg = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(result, ModelMessage.class);
						}
						
						if (msg != null) {
							queue.queueOPAData(msg);
						} else {
							System.out.println("OPA MODEL: Nothing on the topic " + BusinessConfig.ASI_OPA_MODEL_TOPIC);
						}
						Thread.sleep(1000);
						
					} catch (Exception ex) {
						System.out.println("*** OPA MODEL ERROR");
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
