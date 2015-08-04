package gov.pnnl.aim.nmr;

public class NMRErrors {

	private static String [] errors = new String[] {"All is well", "Broken", "Messed Up", "Short Circuit"};
	
	public static String getErrorMsg(int code) {
		return errors[code];
	}
}
