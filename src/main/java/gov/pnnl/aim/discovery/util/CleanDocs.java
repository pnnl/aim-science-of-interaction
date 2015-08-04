package gov.pnnl.aim.discovery.util;

import gov.pnnl.aim.discovery.CanvasConfig;
import gov.pnnl.aim.discovery.data.StatefulProjectLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class CleanDocs {

	public static void main(String[] args) throws ParseException {

		SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat fmt2 = new SimpleDateFormat("dd MMMMM yyyy");
		SimpleDateFormat fmt3 = new SimpleDateFormat("yyyy-MM-dd");
		
		HashSet<String> sources = new HashSet<String>();
		
		sources.add("homeland illumination");
		sources.add("international times");
		sources.add("kronos star");
		sources.add("the abila post");
		sources.add("the world");
		
		for (int i = 0; i < 845; i++) {
			
			String docID = i + ".txt";
						
			try {

				String date   = CleanDocs.getDocField(docID, 2);
				String author = "";
				if (date.matches("^\\d.*$") == false) {
					author = date;
					date = CleanDocs.getDocField(docID, 3);
				}
				if (date.matches("^\\d.*$") == false) {
					author = date;
					date = CleanDocs.getDocField(docID, 1);
				}
				
				if (date.equals("21 of October of 2013")) {
					date = "21 October 2013";
				}
				date = date.replace(" of", "");
				if (date.equals("13June 2010")) {
					date = "13 June 2010";
				}

				String source = CleanDocs.getDocField(docID, 0);
				String title  = CleanDocs.getDocField(docID, 1);
				String text   = CleanDocs.getDocText(docID);
				
				if (date.contains("/")) {
					Date d = fmt1.parse(date);
					date = fmt3.format(d);
				} else {
					Date d = fmt2.parse(date);
					date = fmt3.format(d);
				}
				
				
				if (sources.contains(source.toLowerCase()) || date.startsWith("2014")) {
				
					System.out.println(i + " ;;; " + date + "\n" + title + "\n" + source + "\n" + author + "\n----------");
									
					File f = new File("C:\\Users\\d3k199\\Desktop\\Docs\\" + date + "_" + docID);
					BufferedWriter writer = new BufferedWriter(new FileWriter(f));
					writer.write("Title: " + title + "\r\n");
					writer.write("Source: " + source + "\r\n");
					writer.write("Author: " + author + "\r\n");
					writer.write("Date: " + date + "\r\n");
					writer.write(text + "\r\n");
					writer.close();
				}
				
      } catch (FileNotFoundException e) {
	      e.printStackTrace();
      } catch (IOException e) {
	      e.printStackTrace();
      }
			
		}
	}


  public static String getDocField(final String docID, int fieldID) throws FileNotFoundException, IOException {
    final String path = "/data/GasTech/content/articles/" + docID;
    final URL resURL = StatefulProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      List<String> lines = StringUtil.removeBlanks(IOUtils.readLines(new FileInputStream(file), "UTF-8"));
      
      if (lines != null && lines.size() >= 3) {
      	return lines.get(fieldID);
      }
    }
    return docID;
  }
  
  public static String getDocTitle(final String docID) throws FileNotFoundException, IOException {
    final String path = "/data/GasTech/content/articles/" + docID;
    final URL resURL = StatefulProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      List<String> lines = StringUtil.removeBlanks(IOUtils.readLines(new FileInputStream(file), "UTF-8"));
      
      if (lines != null && lines.size() >= 3) {
      	return lines.get(1);
      }
    }
    return docID;
  }
  
  /**
   * @param docID
   * @return the full text
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static String getDocText(final String docID) throws FileNotFoundException, IOException {
    final String path = "/data/GasTech/content/articles/" + docID;
    final URL resURL = StatefulProjectLoader.class.getResource(path);

    if (resURL != null) {
      final File file = new File(resURL.getFile());
      List<String> lines = IOUtils.readLines(new FileInputStream(file), "UTF-8");
      lines = StringUtil.removeBlanks(lines.subList(5, lines.size()));
      
      StringBuilder builder = new StringBuilder();
      if (lines != null) {
	      for (int i = 0; i < lines.size(); i++) {
	      	builder.append("<p>");
	      	builder.append(lines.get(i));
	      	builder.append("</p>\n");
	      }
      }
      
      return builder.toString();
    }
    return docID;
  }

}
