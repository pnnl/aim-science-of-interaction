package gov.pnnl.aim.discovery.pojo;


/** 
 * A record of a substring and where it occurred in the original text.
 * 
 * @author Grant Nakamura, December 2014
 */
public class Substring {
  /** The substring itself. */
  private String text;
  
  /** Index of where it occurred. */
  private int index;
  
  
  public Substring(String text, int index) {
    this.text = text;
    this.index = index;
  }
  
  public String getText() {
    return text;
  }
  
  public int getIndex() {
    return index;
  }
  
  public int getEndIndex() {
    return index + text.length();
  }
  
  public String toString() {
    return String.format("[%d]%s", index, text);
  }
}
