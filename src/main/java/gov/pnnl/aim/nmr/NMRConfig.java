package gov.pnnl.aim.nmr;

public class NMRConfig {

	// These control pauses between requests to ASI
	public static final long SLEEP_ON_ERROR_MILLIS = 1000;
	public static final long SLEEP_ON_NODATA_MILLIS = 1000;
	public static final long SLEEP_ON_DATA_MILLIS = 100;
	
	// This probably needs to be always true because our charting on the client side wants [0,1] values
	public static final boolean NORMALIZE_SPECTRUM = true;
	
	// This determines the max number of samples displayed in the history portion of the UI 
	public static final int MAX_SAMPLES_DISPLAYED = 10;
	
	// This determines whether a sample requires data from OPA or Shyre before it is 
	// allowed to be displayed in the history in the UI.	
	// Otherwise we just keep it in memory on the server until we have something useful to show the user. 
	public static final boolean REQUIRE_OPA_OR_SHYRE_SAMPLE_PREDICTIONS = true;
	
	
	// User and password for ASI
	public static String ASI_USER = "test_user";
	public static String ASI_PWD  = "test_password";

	// Incoming data topics
	public static String ASI_CA_TOPIC    = "aim-nmr-ca-to-ui";
	public static String ASI_SHYRE_TOPIC = "aim-nmr-shyre-to-ui";
	public static String ASI_OPA_TOPIC   = "aim-nmr-opa-to-ui";
	
	// Feedback topics
	public static String ASI_COMPOUND_FEEDBACK_TOPIC = "aim-nmr-soi-to-opa";
	public static String ASI_STATUS_FEEDBACK_TOPIC   = "aim-nmr-soi-to-ca";
	
}
