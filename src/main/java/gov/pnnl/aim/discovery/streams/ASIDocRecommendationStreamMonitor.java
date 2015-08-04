package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.af.sei.v2.StreamSEI;
import gov.pnnl.aim.discovery.streams.avro.LabeledDocumentMessage;

import javax.ws.rs.core.Response;


public class ASIDocRecommendationStreamMonitor extends RecommendationStreamMonitor {

	private DocumentQueue queue = null;
	
	public ASIDocRecommendationStreamMonitor(DocumentQueue queue) {
		this.queue = queue;
	}
	
	public void run() {

		try {
			
			StreamSEI streamWS = ASIConnector.getConnector(this.queue.getUsername());
			
			// Read documents forever
			while (true) {

				if (isMonitoring()) {
					// Read a message from the topic
					Response response = streamWS.retrieveDynamicTopicRecord(StreamsConfig.RECOMMENDATION_TOPIC, true);
					LabeledDocumentMessage doc = StreamUtils.getLabeledDocumentMessage(response);
					
					queue.queueData(doc);
				} else {
					Thread.sleep(1000);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public DocumentQueue getDocumentQueue() {
		return this.queue;
	}

	@Override
	public void setDocumentQueue(DocumentQueue queue) {
		this.queue = queue;
	}
}