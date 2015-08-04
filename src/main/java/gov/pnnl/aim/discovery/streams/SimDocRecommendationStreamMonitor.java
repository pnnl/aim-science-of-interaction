package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.discovery.streams.avro.LabeledDocumentMessage;

import java.util.ArrayList;
import java.util.List;

public class SimDocRecommendationStreamMonitor extends RecommendationStreamMonitor {

	public static final long RAND_WAIT_MIN_MS = 100;
	public static final long RAND_WAIT_MAX_MS = 2000;
	public static final long RAND_CLUSTER_MAX = 10;
	
	private static String [] titles = new String[] {
		"POK PROTESTS END IN ARRESTS",
		"The Abila Post ON THE SCENE BLOG",
		"Protest POK on the ends of the Hoofdkwartier GAStech in arrests",
		"THE SPONTANEOUS PROTESTS BURST AROUND ABILA",
		"WHO ARE THE PROTESTERS",
		"POK PROTESTS END IN ARRESTS"
	};
	
	
	private DocumentQueue queue = null;
	
	public SimDocRecommendationStreamMonitor(DocumentQueue queue) {
		this.queue = queue;
	}
	
	public void run() {

		try {
			
			long counter = 100000;
			int titleCounter = 0;
			
			// Simulate reading a document stream
			while (true) {
				
				if (isMonitoring()) {
					List<Double> vector = new ArrayList<Double>();
					for (int i = 0; i < 200; i++) {
						vector.add(Math.random());
					}
					
					// Generate a fake incoming document and assign it to a random cluster
					// Assume clusters ids are within the 1 - RAND_CLUSTER_MAX
					int label = (int) (Math.random() * RAND_CLUSTER_MAX);
					LabeledDocumentMessage doc = new LabeledDocumentMessage("vast2014", (counter++) + ".txt", titles[titleCounter], Integer.toString(label), vector);
					queue.queueData(doc);

					// Cycle the titleCounter
					titleCounter = (titleCounter == titles.length - 1) ? 0 : titleCounter + 1;
					
					if (queue.size() == 10) {
						stopMonitoring();
					}
										
					// Sleep for somewhere between RAND_WAIT_MIN_MS and RAND_WAIT_MAX_MS seconds
					Thread.sleep((long) (RAND_WAIT_MIN_MS + ((RAND_WAIT_MAX_MS - RAND_WAIT_MIN_MS) * Math.random())));
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
