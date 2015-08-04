package gov.pnnl.aim.biz;

public class BusinessMonitor {

	private BusinessQueue queue = null;

	private ShyreMonitor monitorShyreThread = null;
	private POPMonitor monitorPOPThread = null;
	private OPAMonitor monitorOPAThread = null;
	private OPAModelMonitor monitorOPAModelThread = null;
	private OPAProportionsMonitor monitorOPAPropThread = null;

	public BusinessMonitor(BusinessQueue queue) {
		this.queue = queue;

		this.monitorShyreThread = new ShyreMonitor(queue);
		this.monitorShyreThread.start();

		this.monitorPOPThread = new POPMonitor(queue);
		this.monitorPOPThread.start();

		this.monitorOPAThread = new OPAMonitor(queue);
		this.monitorOPAThread.start();

		this.monitorOPAModelThread = new OPAModelMonitor(queue);
		this.monitorOPAModelThread.start();

		this.monitorOPAPropThread = new OPAProportionsMonitor(queue);
		this.monitorOPAPropThread.start();
	}

	public BusinessQueue getQueue() {
		return this.queue;
	}

	public void setQueue(BusinessQueue queue) {
		this.queue = queue;
	}

	public void start() {
		monitorShyreThread.setRunning(true);
		monitorPOPThread.setRunning(true);
		monitorOPAThread.setRunning(true);
		monitorOPAModelThread.setRunning(true);
		monitorOPAPropThread.setRunning(true);
	}

	public void stop() {
		monitorShyreThread.setRunning(false);
		monitorPOPThread.setRunning(false);
		monitorOPAThread.setRunning(false);
		monitorOPAModelThread.setRunning(false);
		monitorOPAPropThread.setRunning(false);
	}
}