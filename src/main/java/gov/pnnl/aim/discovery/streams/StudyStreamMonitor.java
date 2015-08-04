package gov.pnnl.aim.discovery.streams;

import gov.pnnl.aim.discovery.CanvasConfig;
import gov.pnnl.aim.discovery.data.AIMDataType;
import gov.pnnl.aim.discovery.data.InspireTextMatrixInitializer;
import gov.pnnl.aim.discovery.data.MatrixProperties;
import gov.pnnl.aim.discovery.data.StatefulProjectLoader;
import gov.pnnl.aim.discovery.pojo.JsonCluster;
import gov.pnnl.aim.discovery.streams.avro.LabeledDocumentMessage;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class StudyStreamMonitor extends RecommendationStreamMonitor {

	private DocumentQueue queue = null;
	private int incNumber = 0;
	private StatefulProjectLoader loader = null;
	
	public StudyStreamMonitor(DocumentQueue queue, int incNumber, StatefulProjectLoader loader) {
		this.queue = queue;
		this.incNumber = incNumber;
		this.loader = loader;
	}
	
	public void run() {

		// Simulate reading a document stream
		while (true) {
			
			if (isMonitoring()) {
								
				incNumber++;

		    final String pathColl = "/data/" + CanvasConfig.PROJECT_NAME + "/results/inc" + incNumber + "/00000000.coll";
		    final URL resURLColl = StatefulProjectLoader.class.getResource(pathColl);
	      final File fileColl = new File(resURLColl.getFile());
	      
	      List<String> docIdentifiers = InspireTextMatrixInitializer.loadCollectionFileDocIdentifiers(fileColl);
	      
	      
	      final String path = "/data/" + CanvasConfig.PROJECT_NAME + "/results/inc" + incNumber + "/00000000.vect.txt";
		    final URL resURL = StatefulProjectLoader.class.getResource(path);
	      final File file = new File(resURL.getFile());
	      

	      try {
	      	
		      // Read all the new vectors in to memory
		      List<String> lines = IOUtils.readLines(new FileInputStream(file), "UTF-8");
		      
		      // Iterate over each vector
		      List<double []> docVectorArrays = new ArrayList<double []>();
	      	List<List<Double>> docVectorList = new ArrayList<List<Double>>();
	      	
		      for (int i = 3; i < lines.size(); i++) {
		      	
		      	// Split out the vector values, first item is doc sequence number
		      	String [] values = lines.get(i).split(" ");
		      	
		      	// Make the vector a double array
		      	double [] vect = new double[values.length - 1];
		      	docVectorArrays.add(vect);
		      	
		      	List<Double> vectList = new ArrayList<Double>();
						for (int j = 1; j < values.length; j++) {
							vect[j - 1] = Double.parseDouble(values[j]);
							vectList.add(vect[j - 1]);
						}
						docVectorList.add(vectList);
		      }
		      
		      // Extend the matrix to add these vectors
		      MatrixProperties matrixProps = loader.getMatrixProperties();
		      matrixProps.addDocuments(docIdentifiers, docVectorArrays, AIMDataType.TEXT);
		      

		      // Add the new documents to the queue for the UI to pick up
		      for (int i = 0; i < docVectorList.size(); i++) {
			      
		      	String docID = docIdentifiers.get(i);
		      	List<Double> vector = docVectorList.get(i);

			      int clusterID = loader.findNearestCluster(docID);
			      
						// Make a streaming message out of this document
						LabeledDocumentMessage doc = new LabeledDocumentMessage("vast2014", docID, docID, Integer.toString(clusterID), vector);
						queue.queueData(doc);
												
		      }
					
					
	      } catch (Exception ex) {
	      	ex.printStackTrace();
	      }
		    
				
				// Stop monitoring to the user has to hit the play button 
				// again to get another increment of documents
				stopMonitoring();				
				
			} else {
				try {
	        Thread.sleep(5000);
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
			}
		}		
	}
	
	

	@Override
  public DocumentQueue getDocumentQueue() {
	  return queue;
  }

	@Override
  public void setDocumentQueue(DocumentQueue queue) {
	  this.queue = queue;
  }
	
}
