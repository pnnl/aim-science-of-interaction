package gov.pnnl.aim.discovery.util;

import gov.pnnl.aim.discovery.interactions.SemanticInteractionEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SemanticEventLogger {

  private final File logDirectory;

  private final File logFile;

  public SemanticEventLogger(final File logDirectory){
	  System.out.println(logDirectory);
    this.logDirectory = logDirectory;
    logFile = createLogFile();
  }

  /**
   * @return
   */
  private File createLogFile() {
	  String date = getDate();
    // Create new file
	  File file = new File(logDirectory + "\\EventLog" + date + ".csv");
    // Write headers
	  try{
		  FileWriter writer = new FileWriter(file);
		  writer.append("EVENT");
		  writer.append(",");
		  writer.append("TIME");
		  writer.append(",");
		  writer.append("DATA");
		  writer.append("\n");
		  
		  writer.flush();
		  writer.close();
	  }
	  catch(IOException e){
		  e.printStackTrace();
	  }
	  return file;
  }

  public void log(final SemanticInteractionEvent event) {
	  if(event != null){
		  writeEvent(event);
	  }
  }

  private boolean writeEvent(final SemanticInteractionEvent event){
    try{
      // Maybe we shouldn't create a new one every time
      FileWriter writer = new FileWriter(logFile, true);
      writer.append(event.toString());
      writer.append("\n");
      writer.flush();
      writer.close();
      return true;
    }
    catch(IOException e){
      e.printStackTrace();
      return false;
    }
  }
  
  private String getDate(){
	  DateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
	  Calendar cal = Calendar.getInstance();
	  return date.format(cal.getTime());
  }

}
