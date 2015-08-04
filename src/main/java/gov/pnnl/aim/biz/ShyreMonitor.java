package gov.pnnl.aim.biz;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.avro.dint.ShyrePiersLOBMessage;
import gov.pnnl.aim.discovery.streams.ASIConnector;
import gov.pnnl.aim.discovery.streams.StreamUtils;
import gov.pnnl.aristotle.aiminterface.NousAnswerStreamRecord;

public class ShyreMonitor extends Thread {

	private BusinessQueue queue = null;
	private boolean isRunning = false;
	
	public ShyreMonitor(BusinessQueue queue) {
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
						Response response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_NOUS_STREAM, true);

						final InputStream in = (InputStream) response.getEntity();
						final byte[] resultBytes = IOUtils.toByteArray(in);

						if (resultBytes.length > 0) {
							final NousAnswerStreamRecord msg = AimAfWsClientFactory.deserializeAvroRecordFromByteArray(resultBytes, NousAnswerStreamRecord.class);
							if (msg != null) {
								queue.queueNousShyreData(msg);
							} else {
								System.out.println("NOUS-SHYRE: Nothing on the topic " + BusinessConfig.ASI_NOUS_STREAM);
							}
						} else {
							System.out.println("NOUS-SHYRE: Nothing on the topic " + BusinessConfig.ASI_NOUS_STREAM);
						}
						
						Thread.sleep(1000);
						
					} catch (Exception ex) {
						System.out.println("*** NOUS-SHYRE ERROR");
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
	
	public void runOld() {

		try {
			
			StreamSEI streamWS = ASIConnector.getConnector(BusinessConfig.ASI_USER);
			
			// Read documents forever
			while (true) {

				if (isRunning) {
					try {
						
						// Read a message from the topic
						Response response = streamWS.retrieveDynamicTopicRecord(BusinessConfig.ASI_SHYRE_TOPIC, true);
						ShyrePiersLOBMessage msg = StreamUtils.getShyreMessage(response);	
						if (msg != null) {
							queue.queueShyreData(msg);
						} else {
							System.out.println("SHYRE: Nothing on the topic " + BusinessConfig.ASI_SHYRE_TOPIC);
						}
						
						Thread.sleep(1000);
						
					} catch (Exception ex) {
						System.out.println("*** SHYRE ERROR");
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
