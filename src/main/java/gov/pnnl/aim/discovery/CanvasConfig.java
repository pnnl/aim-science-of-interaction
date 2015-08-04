package gov.pnnl.aim.discovery;

public class CanvasConfig {

//  public static String PROJECT_NAME = "GasTech400";
  public static String PROJECT_NAME = "StreamDocs";
//  public static String PROJECT_NAME = "VoA100";
    public static boolean STREAM_TO_ASI = false;
    
    /** Serialize the project when end of session is detected? */
    public static boolean SAVE_ON_END = true;
    
    /** Serialize the project on any state change? */
    public static boolean SAVE_ON_CHANGE = true;
    
    /** Serialize the project on backup requests? */
    public static boolean SAVE_ON_BACKUP_REQUEST = true;
}
