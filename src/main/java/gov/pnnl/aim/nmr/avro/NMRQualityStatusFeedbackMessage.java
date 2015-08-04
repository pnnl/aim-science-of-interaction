/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.pnnl.aim.nmr.avro;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class NMRQualityStatusFeedbackMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NMRQualityStatusFeedbackMessage\",\"namespace\":\"gov.pnnl.aim.nmr.avro\",\"fields\":[{\"name\":\"version\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"fileName\",\"type\":\"string\"},{\"name\":\"rowNumber\",\"type\":\"int\"},{\"name\":\"sampleID\",\"type\":\"string\"},{\"name\":\"originalQualityStatus\",\"type\":\"int\"},{\"name\":\"userStatedqualityStatus\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public int version;
  @Deprecated public long timestamp;
  @Deprecated public java.lang.CharSequence fileName;
  @Deprecated public int rowNumber;
  @Deprecated public java.lang.CharSequence sampleID;
  @Deprecated public int originalQualityStatus;
  @Deprecated public int userStatedqualityStatus;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public NMRQualityStatusFeedbackMessage() {}

  /**
   * All-args constructor.
   */
  public NMRQualityStatusFeedbackMessage(java.lang.Integer version, java.lang.Long timestamp, java.lang.CharSequence fileName, java.lang.Integer rowNumber, java.lang.CharSequence sampleID, java.lang.Integer originalQualityStatus, java.lang.Integer userStatedqualityStatus) {
    this.version = version;
    this.timestamp = timestamp;
    this.fileName = fileName;
    this.rowNumber = rowNumber;
    this.sampleID = sampleID;
    this.originalQualityStatus = originalQualityStatus;
    this.userStatedqualityStatus = userStatedqualityStatus;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return version;
    case 1: return timestamp;
    case 2: return fileName;
    case 3: return rowNumber;
    case 4: return sampleID;
    case 5: return originalQualityStatus;
    case 6: return userStatedqualityStatus;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: version = (java.lang.Integer)value$; break;
    case 1: timestamp = (java.lang.Long)value$; break;
    case 2: fileName = (java.lang.CharSequence)value$; break;
    case 3: rowNumber = (java.lang.Integer)value$; break;
    case 4: sampleID = (java.lang.CharSequence)value$; break;
    case 5: originalQualityStatus = (java.lang.Integer)value$; break;
    case 6: userStatedqualityStatus = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'version' field.
   */
  public java.lang.Integer getVersion() {
    return version;
  }

  /**
   * Sets the value of the 'version' field.
   * @param value the value to set.
   */
  public void setVersion(java.lang.Integer value) {
    this.version = value;
  }

  /**
   * Gets the value of the 'timestamp' field.
   */
  public java.lang.Long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the value of the 'timestamp' field.
   * @param value the value to set.
   */
  public void setTimestamp(java.lang.Long value) {
    this.timestamp = value;
  }

  /**
   * Gets the value of the 'fileName' field.
   */
  public java.lang.CharSequence getFileName() {
    return fileName;
  }

  /**
   * Sets the value of the 'fileName' field.
   * @param value the value to set.
   */
  public void setFileName(java.lang.CharSequence value) {
    this.fileName = value;
  }

  /**
   * Gets the value of the 'rowNumber' field.
   */
  public java.lang.Integer getRowNumber() {
    return rowNumber;
  }

  /**
   * Sets the value of the 'rowNumber' field.
   * @param value the value to set.
   */
  public void setRowNumber(java.lang.Integer value) {
    this.rowNumber = value;
  }

  /**
   * Gets the value of the 'sampleID' field.
   */
  public java.lang.CharSequence getSampleID() {
    return sampleID;
  }

  /**
   * Sets the value of the 'sampleID' field.
   * @param value the value to set.
   */
  public void setSampleID(java.lang.CharSequence value) {
    this.sampleID = value;
  }

  /**
   * Gets the value of the 'originalQualityStatus' field.
   */
  public java.lang.Integer getOriginalQualityStatus() {
    return originalQualityStatus;
  }

  /**
   * Sets the value of the 'originalQualityStatus' field.
   * @param value the value to set.
   */
  public void setOriginalQualityStatus(java.lang.Integer value) {
    this.originalQualityStatus = value;
  }

  /**
   * Gets the value of the 'userStatedqualityStatus' field.
   */
  public java.lang.Integer getUserStatedqualityStatus() {
    return userStatedqualityStatus;
  }

  /**
   * Sets the value of the 'userStatedqualityStatus' field.
   * @param value the value to set.
   */
  public void setUserStatedqualityStatus(java.lang.Integer value) {
    this.userStatedqualityStatus = value;
  }

  /** Creates a new NMRQualityStatusFeedbackMessage RecordBuilder */
  public static gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder newBuilder() {
    return new gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder();
  }
  
  /** Creates a new NMRQualityStatusFeedbackMessage RecordBuilder by copying an existing Builder */
  public static gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder newBuilder(gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder other) {
    return new gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder(other);
  }
  
  /** Creates a new NMRQualityStatusFeedbackMessage RecordBuilder by copying an existing NMRQualityStatusFeedbackMessage instance */
  public static gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder newBuilder(gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage other) {
    return new gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder(other);
  }
  
  /**
   * RecordBuilder for NMRQualityStatusFeedbackMessage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<NMRQualityStatusFeedbackMessage>
    implements org.apache.avro.data.RecordBuilder<NMRQualityStatusFeedbackMessage> {

    private int version;
    private long timestamp;
    private java.lang.CharSequence fileName;
    private int rowNumber;
    private java.lang.CharSequence sampleID;
    private int originalQualityStatus;
    private int userStatedqualityStatus;

    /** Creates a new Builder */
    private Builder() {
      super(gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.version)) {
        this.version = data().deepCopy(fields()[0].schema(), other.version);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.fileName)) {
        this.fileName = data().deepCopy(fields()[2].schema(), other.fileName);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.rowNumber)) {
        this.rowNumber = data().deepCopy(fields()[3].schema(), other.rowNumber);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.sampleID)) {
        this.sampleID = data().deepCopy(fields()[4].schema(), other.sampleID);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.originalQualityStatus)) {
        this.originalQualityStatus = data().deepCopy(fields()[5].schema(), other.originalQualityStatus);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.userStatedqualityStatus)) {
        this.userStatedqualityStatus = data().deepCopy(fields()[6].schema(), other.userStatedqualityStatus);
        fieldSetFlags()[6] = true;
      }
    }
    
    /** Creates a Builder by copying an existing NMRQualityStatusFeedbackMessage instance */
    private Builder(gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage other) {
            super(gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.SCHEMA$);
      if (isValidValue(fields()[0], other.version)) {
        this.version = data().deepCopy(fields()[0].schema(), other.version);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.fileName)) {
        this.fileName = data().deepCopy(fields()[2].schema(), other.fileName);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.rowNumber)) {
        this.rowNumber = data().deepCopy(fields()[3].schema(), other.rowNumber);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.sampleID)) {
        this.sampleID = data().deepCopy(fields()[4].schema(), other.sampleID);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.originalQualityStatus)) {
        this.originalQualityStatus = data().deepCopy(fields()[5].schema(), other.originalQualityStatus);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.userStatedqualityStatus)) {
        this.userStatedqualityStatus = data().deepCopy(fields()[6].schema(), other.userStatedqualityStatus);
        fieldSetFlags()[6] = true;
      }
    }

    /** Gets the value of the 'version' field */
    public java.lang.Integer getVersion() {
      return version;
    }
    
    /** Sets the value of the 'version' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder setVersion(int value) {
      validate(fields()[0], value);
      this.version = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'version' field has been set */
    public boolean hasVersion() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'version' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder clearVersion() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'timestamp' field */
    public java.lang.Long getTimestamp() {
      return timestamp;
    }
    
    /** Sets the value of the 'timestamp' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder setTimestamp(long value) {
      validate(fields()[1], value);
      this.timestamp = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'timestamp' field has been set */
    public boolean hasTimestamp() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'timestamp' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder clearTimestamp() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'fileName' field */
    public java.lang.CharSequence getFileName() {
      return fileName;
    }
    
    /** Sets the value of the 'fileName' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder setFileName(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.fileName = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'fileName' field has been set */
    public boolean hasFileName() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'fileName' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder clearFileName() {
      fileName = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'rowNumber' field */
    public java.lang.Integer getRowNumber() {
      return rowNumber;
    }
    
    /** Sets the value of the 'rowNumber' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder setRowNumber(int value) {
      validate(fields()[3], value);
      this.rowNumber = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'rowNumber' field has been set */
    public boolean hasRowNumber() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'rowNumber' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder clearRowNumber() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'sampleID' field */
    public java.lang.CharSequence getSampleID() {
      return sampleID;
    }
    
    /** Sets the value of the 'sampleID' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder setSampleID(java.lang.CharSequence value) {
      validate(fields()[4], value);
      this.sampleID = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'sampleID' field has been set */
    public boolean hasSampleID() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'sampleID' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder clearSampleID() {
      sampleID = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'originalQualityStatus' field */
    public java.lang.Integer getOriginalQualityStatus() {
      return originalQualityStatus;
    }
    
    /** Sets the value of the 'originalQualityStatus' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder setOriginalQualityStatus(int value) {
      validate(fields()[5], value);
      this.originalQualityStatus = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'originalQualityStatus' field has been set */
    public boolean hasOriginalQualityStatus() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'originalQualityStatus' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder clearOriginalQualityStatus() {
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'userStatedqualityStatus' field */
    public java.lang.Integer getUserStatedqualityStatus() {
      return userStatedqualityStatus;
    }
    
    /** Sets the value of the 'userStatedqualityStatus' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder setUserStatedqualityStatus(int value) {
      validate(fields()[6], value);
      this.userStatedqualityStatus = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'userStatedqualityStatus' field has been set */
    public boolean hasUserStatedqualityStatus() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'userStatedqualityStatus' field */
    public gov.pnnl.aim.nmr.avro.NMRQualityStatusFeedbackMessage.Builder clearUserStatedqualityStatus() {
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public NMRQualityStatusFeedbackMessage build() {
      try {
        NMRQualityStatusFeedbackMessage record = new NMRQualityStatusFeedbackMessage();
        record.version = fieldSetFlags()[0] ? this.version : (java.lang.Integer) defaultValue(fields()[0]);
        record.timestamp = fieldSetFlags()[1] ? this.timestamp : (java.lang.Long) defaultValue(fields()[1]);
        record.fileName = fieldSetFlags()[2] ? this.fileName : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.rowNumber = fieldSetFlags()[3] ? this.rowNumber : (java.lang.Integer) defaultValue(fields()[3]);
        record.sampleID = fieldSetFlags()[4] ? this.sampleID : (java.lang.CharSequence) defaultValue(fields()[4]);
        record.originalQualityStatus = fieldSetFlags()[5] ? this.originalQualityStatus : (java.lang.Integer) defaultValue(fields()[5]);
        record.userStatedqualityStatus = fieldSetFlags()[6] ? this.userStatedqualityStatus : (java.lang.Integer) defaultValue(fields()[6]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
