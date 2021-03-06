/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.pnnl.aim.avro.dint;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class OPAPiersLOBMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OPAPiersLOBMessage\",\"namespace\":\"gov.pnnl.aim.avro.dint\",\"fields\":[{\"name\":\"company\",\"type\":\"string\"},{\"name\":\"startdate\",\"type\":\"long\"},{\"name\":\"enddate\",\"type\":\"long\"},{\"name\":\"linesofbusiness\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"probabilities\",\"type\":{\"type\":\"array\",\"items\":\"double\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence company;
  @Deprecated public long startdate;
  @Deprecated public long enddate;
  @Deprecated public java.util.List<java.lang.CharSequence> linesofbusiness;
  @Deprecated public java.util.List<java.lang.Double> probabilities;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public OPAPiersLOBMessage() {}

  /**
   * All-args constructor.
   */
  public OPAPiersLOBMessage(java.lang.CharSequence company, java.lang.Long startdate, java.lang.Long enddate, java.util.List<java.lang.CharSequence> linesofbusiness, java.util.List<java.lang.Double> probabilities) {
    this.company = company;
    this.startdate = startdate;
    this.enddate = enddate;
    this.linesofbusiness = linesofbusiness;
    this.probabilities = probabilities;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return company;
    case 1: return startdate;
    case 2: return enddate;
    case 3: return linesofbusiness;
    case 4: return probabilities;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: company = (java.lang.CharSequence)value$; break;
    case 1: startdate = (java.lang.Long)value$; break;
    case 2: enddate = (java.lang.Long)value$; break;
    case 3: linesofbusiness = (java.util.List<java.lang.CharSequence>)value$; break;
    case 4: probabilities = (java.util.List<java.lang.Double>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'company' field.
   */
  public java.lang.CharSequence getCompany() {
    return company;
  }

  /**
   * Sets the value of the 'company' field.
   * @param value the value to set.
   */
  public void setCompany(java.lang.CharSequence value) {
    this.company = value;
  }

  /**
   * Gets the value of the 'startdate' field.
   */
  public java.lang.Long getStartdate() {
    return startdate;
  }

  /**
   * Sets the value of the 'startdate' field.
   * @param value the value to set.
   */
  public void setStartdate(java.lang.Long value) {
    this.startdate = value;
  }

  /**
   * Gets the value of the 'enddate' field.
   */
  public java.lang.Long getEnddate() {
    return enddate;
  }

  /**
   * Sets the value of the 'enddate' field.
   * @param value the value to set.
   */
  public void setEnddate(java.lang.Long value) {
    this.enddate = value;
  }

  /**
   * Gets the value of the 'linesofbusiness' field.
   */
  public java.util.List<java.lang.CharSequence> getLinesofbusiness() {
    return linesofbusiness;
  }

  /**
   * Sets the value of the 'linesofbusiness' field.
   * @param value the value to set.
   */
  public void setLinesofbusiness(java.util.List<java.lang.CharSequence> value) {
    this.linesofbusiness = value;
  }

  /**
   * Gets the value of the 'probabilities' field.
   */
  public java.util.List<java.lang.Double> getProbabilities() {
    return probabilities;
  }

  /**
   * Sets the value of the 'probabilities' field.
   * @param value the value to set.
   */
  public void setProbabilities(java.util.List<java.lang.Double> value) {
    this.probabilities = value;
  }

  /** Creates a new OPAPiersLOBMessage RecordBuilder */
  public static gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder newBuilder() {
    return new gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder();
  }
  
  /** Creates a new OPAPiersLOBMessage RecordBuilder by copying an existing Builder */
  public static gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder newBuilder(gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder other) {
    return new gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder(other);
  }
  
  /** Creates a new OPAPiersLOBMessage RecordBuilder by copying an existing OPAPiersLOBMessage instance */
  public static gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder newBuilder(gov.pnnl.aim.avro.dint.OPAPiersLOBMessage other) {
    return new gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder(other);
  }
  
  /**
   * RecordBuilder for OPAPiersLOBMessage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<OPAPiersLOBMessage>
    implements org.apache.avro.data.RecordBuilder<OPAPiersLOBMessage> {

    private java.lang.CharSequence company;
    private long startdate;
    private long enddate;
    private java.util.List<java.lang.CharSequence> linesofbusiness;
    private java.util.List<java.lang.Double> probabilities;

    /** Creates a new Builder */
    private Builder() {
      super(gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.company)) {
        this.company = data().deepCopy(fields()[0].schema(), other.company);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.startdate)) {
        this.startdate = data().deepCopy(fields()[1].schema(), other.startdate);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.enddate)) {
        this.enddate = data().deepCopy(fields()[2].schema(), other.enddate);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.linesofbusiness)) {
        this.linesofbusiness = data().deepCopy(fields()[3].schema(), other.linesofbusiness);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.probabilities)) {
        this.probabilities = data().deepCopy(fields()[4].schema(), other.probabilities);
        fieldSetFlags()[4] = true;
      }
    }
    
    /** Creates a Builder by copying an existing OPAPiersLOBMessage instance */
    private Builder(gov.pnnl.aim.avro.dint.OPAPiersLOBMessage other) {
            super(gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.SCHEMA$);
      if (isValidValue(fields()[0], other.company)) {
        this.company = data().deepCopy(fields()[0].schema(), other.company);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.startdate)) {
        this.startdate = data().deepCopy(fields()[1].schema(), other.startdate);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.enddate)) {
        this.enddate = data().deepCopy(fields()[2].schema(), other.enddate);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.linesofbusiness)) {
        this.linesofbusiness = data().deepCopy(fields()[3].schema(), other.linesofbusiness);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.probabilities)) {
        this.probabilities = data().deepCopy(fields()[4].schema(), other.probabilities);
        fieldSetFlags()[4] = true;
      }
    }

    /** Gets the value of the 'company' field */
    public java.lang.CharSequence getCompany() {
      return company;
    }
    
    /** Sets the value of the 'company' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder setCompany(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.company = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'company' field has been set */
    public boolean hasCompany() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'company' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder clearCompany() {
      company = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'startdate' field */
    public java.lang.Long getStartdate() {
      return startdate;
    }
    
    /** Sets the value of the 'startdate' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder setStartdate(long value) {
      validate(fields()[1], value);
      this.startdate = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'startdate' field has been set */
    public boolean hasStartdate() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'startdate' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder clearStartdate() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'enddate' field */
    public java.lang.Long getEnddate() {
      return enddate;
    }
    
    /** Sets the value of the 'enddate' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder setEnddate(long value) {
      validate(fields()[2], value);
      this.enddate = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'enddate' field has been set */
    public boolean hasEnddate() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'enddate' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder clearEnddate() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'linesofbusiness' field */
    public java.util.List<java.lang.CharSequence> getLinesofbusiness() {
      return linesofbusiness;
    }
    
    /** Sets the value of the 'linesofbusiness' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder setLinesofbusiness(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[3], value);
      this.linesofbusiness = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'linesofbusiness' field has been set */
    public boolean hasLinesofbusiness() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'linesofbusiness' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder clearLinesofbusiness() {
      linesofbusiness = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'probabilities' field */
    public java.util.List<java.lang.Double> getProbabilities() {
      return probabilities;
    }
    
    /** Sets the value of the 'probabilities' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder setProbabilities(java.util.List<java.lang.Double> value) {
      validate(fields()[4], value);
      this.probabilities = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'probabilities' field has been set */
    public boolean hasProbabilities() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'probabilities' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBMessage.Builder clearProbabilities() {
      probabilities = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    public OPAPiersLOBMessage build() {
      try {
        OPAPiersLOBMessage record = new OPAPiersLOBMessage();
        record.company = fieldSetFlags()[0] ? this.company : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.startdate = fieldSetFlags()[1] ? this.startdate : (java.lang.Long) defaultValue(fields()[1]);
        record.enddate = fieldSetFlags()[2] ? this.enddate : (java.lang.Long) defaultValue(fields()[2]);
        record.linesofbusiness = fieldSetFlags()[3] ? this.linesofbusiness : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[3]);
        record.probabilities = fieldSetFlags()[4] ? this.probabilities : (java.util.List<java.lang.Double>) defaultValue(fields()[4]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
