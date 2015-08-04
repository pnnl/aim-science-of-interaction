package gov.pnnl.aim.discovery.streams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gov.pnnl.aim.af.client.ws.AimAfWsClientFactory;
import gov.pnnl.aim.af.sei.v2.StreamSEI;

public class ASIConnector {

	static {
	  System.setProperty("java.util.logging.config.file", "");
	}
	  
	private static Map<String, StreamSEI> streamConnectors = new HashMap<String, StreamSEI>();
	
	public static StreamSEI initConnector(String username, String password) throws IOException, Exception {
		StreamSEI streamWS = AimAfWsClientFactory.generateAimAfStreamClientV2("https", "aim-af.pnl.gov", 8443, username, password);
		streamConnectors.put(username, streamWS);
		return streamWS;
	}
	
	public static StreamSEI getConnector(String username) {
		return streamConnectors.get(username);
	}
}
