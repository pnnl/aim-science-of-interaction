package gov.pnnl.aim.discovery.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringUtil {
	
	public static List<String> toStringList(List<CharSequence> list) {
		List<String> list2 = new ArrayList<String>();
		for (CharSequence s : list) {
			list2.add(s.toString());
		}
		return list2;
	}

  public static String join(Iterable<String> values, String joinWith) {
    StringBuilder sb = new StringBuilder();
    if (values != null) {
      Iterator<String> itr = values.iterator();
      if (itr.hasNext()) {
        sb.append(itr.next());
        while (itr.hasNext())  {
          sb.append(joinWith);
          sb.append(itr.next());
        }
      }
    }
    return sb.toString();
  }
  
  public static String join(ArrayList<Integer> values, String joinWith) {
    StringBuilder sb = new StringBuilder();
    if (values != null) {
      Iterator<Integer> itr = values.iterator();
      if (itr.hasNext()) {
        sb.append(itr.next());
        while (itr.hasNext())  {
          sb.append(joinWith);
          sb.append(itr.next());
        }
      }
    }
    return sb.toString();
  }
  
  public static List<String> removeBlanks(List<String> values) {
  	List<String> valuesFixed = new ArrayList<String>();
  	for (String value : values) {
  		if (value != null && value.trim().length() > 0) {
  			valuesFixed.add(value);
  		}
  	}
  	return valuesFixed;
  }
  
  public static void main(String [] args) throws IOException {
    File d = new File("C:\\apps\\INSPIRE\\DatasetRoot\\Sources\\tvgrazgood");
    File dest = new File("C:\\apps\\INSPIRE\\DatasetRoot\\Sources\\tvgraztext");
    File [] fs = d.listFiles(new FileFilter() {

      @Override
      public boolean accept(File arg0) {
        return arg0.getName().endsWith(".xml");
      }
      
    });
    

    int size = fs.length;
    for (int i = 0; i < size; i++) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fs[i]), "Unicode"));
      String line = "";
      StringBuffer content = new StringBuffer();
      boolean isContent = false;
      while ((line = reader.readLine()) != null) {
        
        if (line.contains("</TEXT>")) {
          isContent = false;
        }   
        
        if (isContent) {
          content.append(line);
          content.append("\n");
        }
        
        if (line.contains("<TEXT>")) {
          isContent = true;
        }     
      }        
      reader.close();
      

      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dest, fs[i].getName())));
      writer.write(content.toString());
      writer.close();
    }    
  }
}
