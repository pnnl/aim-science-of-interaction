package gov.pnnl.aim.discovery.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//import javax.servlet.http.*;

public class OldSemanticEventLogger {

	private String eventType = "Event";
	private UUID id;
	private String startTime;
	private String endTime;
	private String action = "A user event occured";
	
	//After event created, then logged
	public OldSemanticEventLogger(){
		
		this.startTime = getTime();
		this.endTime = getTime();
		this.id = UUID.randomUUID();
		
		//log
		boolean success = logCSV("semanticEventLog.csv");
		if(success){
			System.out.print("File saved!");
		}
		else{
			System.out.print("An error occured!");
		}
	}
	
	private boolean logCSV(String fileName){
		
		try{
//			String hostPath = getServletContext().getRealPath("/");
			
			File file = new File("C:\\test\\" + fileName);
			file.createNewFile();
			
			FileWriter writer = new FileWriter(file);
			
			writer.append("ID");
			writer.append(",");
			writer.append("ACTION");
			writer.append(",");
			writer.append("START_TIME");
			writer.append(",");
			writer.append("END_TIME");
			writer.append("\n");
			
			writer.append(id.toString());
			writer.append(",");
			writer.append(this.action);
			writer.append(",");
			writer.append(this.startTime);
			writer.append(",");
			writer.append(this.endTime);
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
	
	private String getTime(){
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime());
	}
}
