/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.pnnl.aim.af.avro;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class NousProfileQuestionStreamRecord extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NousProfileQuestionStreamRecord\",\"namespace\":\"gov.pnnl.aim.af.avro\",\"fields\":[{\"name\":\"version\",\"type\":\"int\"},{\"name\":\"uuid\",\"type\":\"string\"},{\"name\":\"timestemp\",\"type\":\"long\"},{\"name\":\"source\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public int version;
  @Deprecated public java.lang.CharSequence uuid;
  @Deprecated public long timestemp;
  @Deprecated public java.lang.CharSequence source;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public NousProfileQuestionStreamRecord() {}

  /**
   * All-args constructor.
   */
  public NousProfileQuestionStreamRecord(java.lang.Integer version, java.lang.CharSequence uuid, java.lang.Long timestemp, java.lang.CharSequence source) {
    this.version = version;
    this.uuid = uuid;
    this.timestemp = timestemp;
    this.source = source;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return version;
    case 1: return uuid;
    case 2: return timestemp;
    case 3: return source;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: version = (java.lang.Integer)value$; break;
    case 1: uuid = (java.lang.CharSequence)value$; break;
    case 2: timestemp = (java.lang.Long)value$; break;
    case 3: source = (java.lang.CharSequence)value$; break;
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
   * Gets the value of the 'uuid' field.
   */
  public java.lang.CharSequence getUuid() {
    return uuid;
  }

  /**
   * Sets the value of the 'uuid' field.
   * @param value the value to set.
   */
  public void setUuid(java.lang.CharSequence value) {
    this.uuid = value;
  }

  /**
   * Gets the value of the 'timestemp' field.
   */
  public java.lang.Long getTimestemp() {
    return timestemp;
  }

  /**
   * Sets the value of the 'timestemp' field.
   * @param value the value to set.
   */
  public void setTimestemp(java.lang.Long value) {
    this.timestemp = value;
  }

  /**
   * Gets the value of the 'source' field.
   */
  public java.lang.CharSequence getSource() {
    return source;
  }

  /**
   * Sets the value of the 'source' field.
   * @param value the value to set.
   */
  public void setSource(java.lang.CharSequence value) {
    this.source = value;
  }

  /** Creates a new NousProfileQuestionStreamRecord RecordBuilder */
  public static gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder newBuilder() {
    return new gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder();
  }
  
  /** Creates a new NousProfileQuestionStreamRecord RecordBuilder by copying an existing Builder */
  public static gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder newBuilder(gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder other) {
    return new gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder(other);
  }
  
  /** Creates a new NousProfileQuestionStreamRecord RecordBuilder by copying an existing NousProfileQuestionStreamRecord instance */
  public static gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder newBuilder(gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord other) {
    return new gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder(other);
  }
  
  /**
   * RecordBuilder for NousProfileQuestionStreamRecord instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<NousProfileQuestionStreamRecord>
    implements org.apache.avro.data.RecordBuilder<NousProfileQuestionStreamRecord> {

    private int version;
    private java.lang.CharSequence uuid;
    private long timestemp;
    private java.lang.CharSequence source;

    /** Creates a new Builder */
    private Builder() {
      super(gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.version)) {
        this.version = data().deepCopy(fields()[0].schema(), other.version);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.uuid)) {
        this.uuid = data().deepCopy(fields()[1].schema(), other.uuid);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.timestemp)) {
        this.timestemp = data().deepCopy(fields()[2].schema(), other.timestemp);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.source)) {
        this.source = data().deepCopy(fields()[3].schema(), other.source);
        fieldSetFlags()[3] = true;
      }
    }
    
    /** Creates a Builder by copying an existing NousProfileQuestionStreamRecord instance */
    private Builder(gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord other) {
            super(gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.SCHEMA$);
      if (isValidValue(fields()[0], other.version)) {
        this.version = data().deepCopy(fields()[0].schema(), other.version);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.uuid)) {
        this.uuid = data().deepCopy(fields()[1].schema(), other.uuid);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.timestemp)) {
        this.timestemp = data().deepCopy(fields()[2].schema(), other.timestemp);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.source)) {
        this.source = data().deepCopy(fields()[3].schema(), other.source);
        fieldSetFlags()[3] = true;
      }
    }

    /** Gets the value of the 'version' field */
    public java.lang.Integer getVersion() {
      return version;
    }
    
    /** Sets the value of the 'version' field */
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder setVersion(int value) {
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
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder clearVersion() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'uuid' field */
    public java.lang.CharSequence getUuid() {
      return uuid;
    }
    
    /** Sets the value of the 'uuid' field */
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder setUuid(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.uuid = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'uuid' field has been set */
    public boolean hasUuid() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'uuid' field */
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder clearUuid() {
      uuid = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'timestemp' field */
    public java.lang.Long getTimestemp() {
      return timestemp;
    }
    
    /** Sets the value of the 'timestemp' field */
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder setTimestemp(long value) {
      validate(fields()[2], value);
      this.timestemp = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'timestemp' field has been set */
    public boolean hasTimestemp() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'timestemp' field */
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder clearTimestemp() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'source' field */
    public java.lang.CharSequence getSource() {
      return source;
    }
    
    /** Sets the value of the 'source' field */
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder setSource(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.source = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'source' field has been set */
    public boolean hasSource() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'source' field */
    public gov.pnnl.aim.af.avro.NousProfileQuestionStreamRecord.Builder clearSource() {
      source = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    public NousProfileQuestionStreamRecord build() {
      try {
        NousProfileQuestionStreamRecord record = new NousProfileQuestionStreamRecord();
        record.version = fieldSetFlags()[0] ? this.version : (java.lang.Integer) defaultValue(fields()[0]);
        record.uuid = fieldSetFlags()[1] ? this.uuid : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.timestemp = fieldSetFlags()[2] ? this.timestemp : (java.lang.Long) defaultValue(fields()[2]);
        record.source = fieldSetFlags()[3] ? this.source : (java.lang.CharSequence) defaultValue(fields()[3]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}