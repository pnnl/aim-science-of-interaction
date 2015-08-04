package gov.pnnl.aim.nmr;


public class NMRError {
  private int code = -1;

  private String message = "None";

  /**
   *
   */
  public NMRError() {
  }

  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * @param code
   *          the code to set
   */
  public void setCode(final int code) {
    this.code = code;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message
   *          the message to set
   */
  public void setMessage(final String message) {
    this.message = message;
  }

}
