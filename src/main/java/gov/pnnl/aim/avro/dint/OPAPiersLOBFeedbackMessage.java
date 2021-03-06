/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.pnnl.aim.avro.dint;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class OPAPiersLOBFeedbackMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"OPAPiersLOBFeedbackMessage\",\"namespace\":\"gov.pnnl.aim.avro.dint\",\"fields\":[{\"name\":\"company\",\"type\":\"string\"},{\"name\":\"lob\",\"type\":\"string\"},{\"name\":\"feature\",\"type\":\"string\"},{\"name\":\"add\",\"type\":\"boolean\"},{\"name\":\"remove\",\"type\":\"boolean\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence company;
  @Deprecated public java.lang.CharSequence lob;
  @Deprecated public java.lang.CharSequence feature;
  @Deprecated public boolean add;
  @Deprecated public boolean remove;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public OPAPiersLOBFeedbackMessage() {}

  /**
   * All-args constructor.
   */
  public OPAPiersLOBFeedbackMessage(java.lang.CharSequence company, java.lang.CharSequence lob, java.lang.CharSequence feature, java.lang.Boolean add, java.lang.Boolean remove) {
    this.company = company;
    this.lob = lob;
    this.feature = feature;
    this.add = add;
    this.remove = remove;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return company;
    case 1: return lob;
    case 2: return feature;
    case 3: return add;
    case 4: return remove;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: company = (java.lang.CharSequence)value$; break;
    case 1: lob = (java.lang.CharSequence)value$; break;
    case 2: feature = (java.lang.CharSequence)value$; break;
    case 3: add = (java.lang.Boolean)value$; break;
    case 4: remove = (java.lang.Boolean)value$; break;
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
   * Gets the value of the 'lob' field.
   */
  public java.lang.CharSequence getLob() {
    return lob;
  }

  /**
   * Sets the value of the 'lob' field.
   * @param value the value to set.
   */
  public void setLob(java.lang.CharSequence value) {
    this.lob = value;
  }

  /**
   * Gets the value of the 'feature' field.
   */
  public java.lang.CharSequence getFeature() {
    return feature;
  }

  /**
   * Sets the value of the 'feature' field.
   * @param value the value to set.
   */
  public void setFeature(java.lang.CharSequence value) {
    this.feature = value;
  }

  /**
   * Gets the value of the 'add' field.
   */
  public java.lang.Boolean getAdd() {
    return add;
  }

  /**
   * Sets the value of the 'add' field.
   * @param value the value to set.
   */
  public void setAdd(java.lang.Boolean value) {
    this.add = value;
  }

  /**
   * Gets the value of the 'remove' field.
   */
  public java.lang.Boolean getRemove() {
    return remove;
  }

  /**
   * Sets the value of the 'remove' field.
   * @param value the value to set.
   */
  public void setRemove(java.lang.Boolean value) {
    this.remove = value;
  }

  /** Creates a new OPAPiersLOBFeedbackMessage RecordBuilder */
  public static gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder newBuilder() {
    return new gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder();
  }
  
  /** Creates a new OPAPiersLOBFeedbackMessage RecordBuilder by copying an existing Builder */
  public static gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder newBuilder(gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder other) {
    return new gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder(other);
  }
  
  /** Creates a new OPAPiersLOBFeedbackMessage RecordBuilder by copying an existing OPAPiersLOBFeedbackMessage instance */
  public static gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder newBuilder(gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage other) {
    return new gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder(other);
  }
  
  /**
   * RecordBuilder for OPAPiersLOBFeedbackMessage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<OPAPiersLOBFeedbackMessage>
    implements org.apache.avro.data.RecordBuilder<OPAPiersLOBFeedbackMessage> {

    private java.lang.CharSequence company;
    private java.lang.CharSequence lob;
    private java.lang.CharSequence feature;
    private boolean add;
    private boolean remove;

    /** Creates a new Builder */
    private Builder() {
      super(gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.company)) {
        this.company = data().deepCopy(fields()[0].schema(), other.company);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.lob)) {
        this.lob = data().deepCopy(fields()[1].schema(), other.lob);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.feature)) {
        this.feature = data().deepCopy(fields()[2].schema(), other.feature);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.add)) {
        this.add = data().deepCopy(fields()[3].schema(), other.add);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.remove)) {
        this.remove = data().deepCopy(fields()[4].schema(), other.remove);
        fieldSetFlags()[4] = true;
      }
    }
    
    /** Creates a Builder by copying an existing OPAPiersLOBFeedbackMessage instance */
    private Builder(gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage other) {
            super(gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.SCHEMA$);
      if (isValidValue(fields()[0], other.company)) {
        this.company = data().deepCopy(fields()[0].schema(), other.company);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.lob)) {
        this.lob = data().deepCopy(fields()[1].schema(), other.lob);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.feature)) {
        this.feature = data().deepCopy(fields()[2].schema(), other.feature);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.add)) {
        this.add = data().deepCopy(fields()[3].schema(), other.add);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.remove)) {
        this.remove = data().deepCopy(fields()[4].schema(), other.remove);
        fieldSetFlags()[4] = true;
      }
    }

    /** Gets the value of the 'company' field */
    public java.lang.CharSequence getCompany() {
      return company;
    }
    
    /** Sets the value of the 'company' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder setCompany(java.lang.CharSequence value) {
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
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder clearCompany() {
      company = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'lob' field */
    public java.lang.CharSequence getLob() {
      return lob;
    }
    
    /** Sets the value of the 'lob' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder setLob(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.lob = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'lob' field has been set */
    public boolean hasLob() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'lob' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder clearLob() {
      lob = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'feature' field */
    public java.lang.CharSequence getFeature() {
      return feature;
    }
    
    /** Sets the value of the 'feature' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder setFeature(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.feature = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'feature' field has been set */
    public boolean hasFeature() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'feature' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder clearFeature() {
      feature = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'add' field */
    public java.lang.Boolean getAdd() {
      return add;
    }
    
    /** Sets the value of the 'add' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder setAdd(boolean value) {
      validate(fields()[3], value);
      this.add = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'add' field has been set */
    public boolean hasAdd() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'add' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder clearAdd() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'remove' field */
    public java.lang.Boolean getRemove() {
      return remove;
    }
    
    /** Sets the value of the 'remove' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder setRemove(boolean value) {
      validate(fields()[4], value);
      this.remove = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'remove' field has been set */
    public boolean hasRemove() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'remove' field */
    public gov.pnnl.aim.avro.dint.OPAPiersLOBFeedbackMessage.Builder clearRemove() {
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    public OPAPiersLOBFeedbackMessage build() {
      try {
        OPAPiersLOBFeedbackMessage record = new OPAPiersLOBFeedbackMessage();
        record.company = fieldSetFlags()[0] ? this.company : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.lob = fieldSetFlags()[1] ? this.lob : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.feature = fieldSetFlags()[2] ? this.feature : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.add = fieldSetFlags()[3] ? this.add : (java.lang.Boolean) defaultValue(fields()[3]);
        record.remove = fieldSetFlags()[4] ? this.remove : (java.lang.Boolean) defaultValue(fields()[4]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
