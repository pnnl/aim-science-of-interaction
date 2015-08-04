package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.discovery.CanvasConfig;
import gov.pnnl.aim.discovery.CanvasRestService;
import gov.pnnl.aim.discovery.data.StatefulProjectLoader;
import gov.pnnl.aim.discovery.streams.avro.LabeledDocumentMessage;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class PopDocRecommendationStreamMonitor extends RecommendationStreamMonitor {

	public static final long RAND_WAIT_MIN_MS = 100;
	public static final long RAND_WAIT_MAX_MS = 2000;
	
	private DocumentQueue queue = null;
	
	public PopDocRecommendationStreamMonitor(DocumentQueue queue) {
		this.queue = queue;
	}
	
	public void run() {

		try {
			
	    final String path = "/data/" + CanvasConfig.PROJECT_NAME + "/results/incremental/400-poplabeled.csv";
	    final URL resURL = StatefulProjectLoader.class.getResource(path);

      final File file = new File(resURL.getFile());
      List<String> lines = IOUtils.readLines(new FileInputStream(file), "UTF-8");
      
			// Simulate reading a document stream
			for (String line : lines) {
				
				if (line.startsWith("docid")) {
					continue;
				}
				
				if (isMonitoring()) {
					
					String [] values = line.split(",");
					String id = "incremental/" + values[0];
					String label = values[1];
					
					List<Double> vector = new ArrayList<Double>();
					for (int i = 2; i < values.length; i++) {
						vector.add(Double.parseDouble(values[i]));
					}
					
					LabeledDocumentMessage doc = new LabeledDocumentMessage("vast2014", id, id, label, vector);
					queue.queueData(doc);
					
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
