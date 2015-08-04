package gov.pnnl.aim.discovery.streams;

public abstract class RecommendationStreamMonitor implements Runnable {

	private boolean isMonitoring = false;
	
	public abstract DocumentQueue getDocumentQueue();
	public abstract void setDocumentQueue(DocumentQueue queue);

	public boolean isMonitoring() {
		return this.isMonitoring;
	}
	
	public void startMonitoring() {
		this.isMonitoring = true;
	}

	public void stopMonitoring() {
		this.isMonitoring = false;
	}
}
