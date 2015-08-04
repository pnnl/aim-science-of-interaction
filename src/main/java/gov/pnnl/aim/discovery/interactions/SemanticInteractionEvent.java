/**
 * $BATTELLE_CONTRACT_DISCLAIMER$
 */
package gov.pnnl.aim.discovery.interactions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * @author hamp645
 *
 */
public abstract class SemanticInteractionEvent {
  /** The type of delimiter */
  public final static String DELIMITER = ",";
  public static boolean isReading = false;
  
  /** 
   * Document ID that goes with {@link #isReading}. This is an ugly hack to get 
   * things ready for the user study.
   */
  protected static String docIdBeingRead;

  /** The type of the event */
  protected SemanticInteractions type;

  /** The timestamp of the event */
  protected String timestamp;

  /**
   * Default constructor
   */
  public SemanticInteractionEvent() {
	  
    timestamp = getDate();
  }
  
  public SemanticInteractionEvent checkIsReading(){
	  
	  if(isReading){
		  EndReadEvent ere = new EndReadEvent(docIdBeingRead);
		  return ere;
	  }
	  else{
		  return null;
	  }
  }

  /**
   * @return the type
   */
  public SemanticInteractions getType() {
    return type;
  }

  /**
   * @return the timestamp
   */
  public String getTimestamp() {
    return timestamp;
  }
  

  @Override
  public String toString() {
    return type + DELIMITER + timestamp + DELIMITER;
  }
  
  private String getDate(){
	  DateFormat date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	  Calendar cal = Calendar.getInstance();
	  return date.format(cal.getTime());
  }
}
