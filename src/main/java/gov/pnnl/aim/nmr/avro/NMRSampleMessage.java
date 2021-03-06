/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.pnnl.aim.nmr.avro;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class NMRSampleMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"NMRSampleMessage\",\"namespace\":\"gov.pnnl.aim.nmr.avro\",\"fields\":[{\"name\":\"version\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"fileName\",\"type\":\"string\"},{\"name\":\"rowNumber\",\"type\":\"int\"},{\"name\":\"sampleID\",\"type\":\"string\"},{\"name\":\"qualityStatus\",\"type\":\"int\"},{\"name\":\"originalSpectrum\",\"type\":{\"type\":\"array\",\"items\":\"double\"}},{\"name\":\"spectrum\",\"type\":{\"type\":\"array\",\"items\":\"double\"}},{\"name\":\"compoundPredictions\",\"type\":{\"type\":\"array\",\"items\":\"double\"}},{\"name\":\"compoundVetos\",\"type\":{\"type\":\"array\",\"items\":\"boolean\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public int version;
  @Deprecated public long timestamp;
  @Deprecated public java.lang.CharSequence fileName;
  @Deprecated public int rowNumber;
  @Deprecated public java.lang.CharSequence sampleID;
  @Deprecated public int qualityStatus;
  @Deprecated public java.util.List<java.lang.Double> originalSpectrum;
  @Deprecated public java.util.List<java.lang.Double> spectrum;
  @Deprecated public java.util.List<java.lang.Double> compoundPredictions;
  @Deprecated public java.util.List<java.lang.Boolean> compoundVetos;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public NMRSampleMessage() {}

  /**
   * All-args constructor.
   */
  public NMRSampleMessage(java.lang.Integer version, java.lang.Long timestamp, java.lang.CharSequence fileName, java.lang.Integer rowNumber, java.lang.CharSequence sampleID, java.lang.Integer qualityStatus, java.util.List<java.lang.Double> originalSpectrum, java.util.List<java.lang.Double> spectrum, java.util.List<java.lang.Double> compoundPredictions, java.util.List<java.lang.Boolean> compoundVetos) {
    this.version = version;
    this.timestamp = timestamp;
    this.fileName = fileName;
    this.rowNumber = rowNumber;
    this.sampleID = sampleID;
    this.qualityStatus = qualityStatus;
    this.originalSpectrum = originalSpectrum;
    this.spectrum = spectrum;
    this.compoundPredictions = compoundPredictions;
    this.compoundVetos = compoundVetos;
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
    case 5: return qualityStatus;
    case 6: return originalSpectrum;
    case 7: return spectrum;
    case 8: return compoundPredictions;
    case 9: return compoundVetos;
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
    case 5: qualityStatus = (java.lang.Integer)value$; break;
    case 6: originalSpectrum = (java.util.List<java.lang.Double>)value$; break;
    case 7: spectrum = (java.util.List<java.lang.Double>)value$; break;
    case 8: compoundPredictions = (java.util.List<java.lang.Double>)value$; break;
    case 9: compoundVetos = (java.util.List<java.lang.Boolean>)value$; break;
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
   * Gets the value of the 'qualityStatus' field.
   */
  public java.lang.Integer getQualityStatus() {
    return qualityStatus;
  }

  /**
   * Sets the value of the 'qualityStatus' field.
   * @param value the value to set.
   */
  public void setQualityStatus(java.lang.Integer value) {
    this.qualityStatus = value;
  }

  /**
   * Gets the value of the 'originalSpectrum' field.
   */
  public java.util.List<java.lang.Double> getOriginalSpectrum() {
    return originalSpectrum;
  }

  /**
   * Sets the value of the 'originalSpectrum' field.
   * @param value the value to set.
   */
  public void setOriginalSpectrum(java.util.List<java.lang.Double> value) {
    this.originalSpectrum = value;
  }

  /**
   * Gets the value of the 'spectrum' field.
   */
  public java.util.List<java.lang.Double> getSpectrum() {
    return spectrum;
  }

  /**
   * Sets the value of the 'spectrum' field.
   * @param value the value to set.
   */
  public void setSpectrum(java.util.List<java.lang.Double> value) {
    this.spectrum = value;
  }

  /**
   * Gets the value of the 'compoundPredictions' field.
   */
  public java.util.List<java.lang.Double> getCompoundPredictions() {
    return compoundPredictions;
  }

  /**
   * Sets the value of the 'compoundPredictions' field.
   * @param value the value to set.
   */
  public void setCompoundPredictions(java.util.List<java.lang.Double> value) {
    this.compoundPredictions = value;
  }

  /**
   * Gets the value of the 'compoundVetos' field.
   */
  public java.util.List<java.lang.Boolean> getCompoundVetos() {
    return compoundVetos;
  }

  /**
   * Sets the value of the 'compoundVetos' field.
   * @param value the value to set.
   */
  public void setCompoundVetos(java.util.List<java.lang.Boolean> value) {
    this.compoundVetos = value;
  }

  /** Creates a new NMRSampleMessage RecordBuilder */
  public static gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder newBuilder() {
    return new gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder();
  }
  
  /** Creates a new NMRSampleMessage RecordBuilder by copying an existing Builder */
  public static gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder newBuilder(gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder other) {
    return new gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder(other);
  }
  
  /** Creates a new NMRSampleMessage RecordBuilder by copying an existing NMRSampleMessage instance */
  public static gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder newBuilder(gov.pnnl.aim.nmr.avro.NMRSampleMessage other) {
    return new gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder(other);
  }
  
  /**
   * RecordBuilder for NMRSampleMessage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<NMRSampleMessage>
    implements org.apache.avro.data.RecordBuilder<NMRSampleMessage> {

    private int version;
    private long timestamp;
    private java.lang.CharSequence fileName;
    private int rowNumber;
    private java.lang.CharSequence sampleID;
    private int qualityStatus;
    private java.util.List<java.lang.Double> originalSpectrum;
    private java.util.List<java.lang.Double> spectrum;
    private java.util.List<java.lang.Double> compoundPredictions;
    private java.util.List<java.lang.Boolean> compoundVetos;

    /** Creates a new Builder */
    private Builder() {
      super(gov.pnnl.aim.nmr.avro.NMRSampleMessage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder other) {
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
      if (isValidValue(fields()[5], other.qualityStatus)) {
        this.qualityStatus = data().deepCopy(fields()[5].schema(), other.qualityStatus);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.originalSpectrum)) {
        this.originalSpectrum = data().deepCopy(fields()[6].schema(), other.originalSpectrum);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.spectrum)) {
        this.spectrum = data().deepCopy(fields()[7].schema(), other.spectrum);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.compoundPredictions)) {
        this.compoundPredictions = data().deepCopy(fields()[8].schema(), other.compoundPredictions);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.compoundVetos)) {
        this.compoundVetos = data().deepCopy(fields()[9].schema(), other.compoundVetos);
        fieldSetFlags()[9] = true;
      }
    }
    
    /** Creates a Builder by copying an existing NMRSampleMessage instance */
    private Builder(gov.pnnl.aim.nmr.avro.NMRSampleMessage other) {
            super(gov.pnnl.aim.nmr.avro.NMRSampleMessage.SCHEMA$);
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
      if (isValidValue(fields()[5], other.qualityStatus)) {
        this.qualityStatus = data().deepCopy(fields()[5].schema(), other.qualityStatus);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.originalSpectrum)) {
        this.originalSpectrum = data().deepCopy(fields()[6].schema(), other.originalSpectrum);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.spectrum)) {
        this.spectrum = data().deepCopy(fields()[7].schema(), other.spectrum);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.compoundPredictions)) {
        this.compoundPredictions = data().deepCopy(fields()[8].schema(), other.compoundPredictions);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.compoundVetos)) {
        this.compoundVetos = data().deepCopy(fields()[9].schema(), other.compoundVetos);
        fieldSetFlags()[9] = true;
      }
    }

    /** Gets the value of the 'version' field */
    public java.lang.Integer getVersion() {
      return version;
    }
    
    /** Sets the value of the 'version' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setVersion(int value) {
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
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearVersion() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'timestamp' field */
    public java.lang.Long getTimestamp() {
      return timestamp;
    }
    
    /** Sets the value of the 'timestamp' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setTimestamp(long value) {
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
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearTimestamp() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'fileName' field */
    public java.lang.CharSequence getFileName() {
      return fileName;
    }
    
    /** Sets the value of the 'fileName' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setFileName(java.lang.CharSequence value) {
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
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearFileName() {
      fileName = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'rowNumber' field */
    public java.lang.Integer getRowNumber() {
      return rowNumber;
    }
    
    /** Sets the value of the 'rowNumber' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setRowNumber(int value) {
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
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearRowNumber() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'sampleID' field */
    public java.lang.CharSequence getSampleID() {
      return sampleID;
    }
    
    /** Sets the value of the 'sampleID' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setSampleID(java.lang.CharSequence value) {
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
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearSampleID() {
      sampleID = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'qualityStatus' field */
    public java.lang.Integer getQualityStatus() {
      return qualityStatus;
    }
    
    /** Sets the value of the 'qualityStatus' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setQualityStatus(int value) {
      validate(fields()[5], value);
      this.qualityStatus = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'qualityStatus' field has been set */
    public boolean hasQualityStatus() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'qualityStatus' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearQualityStatus() {
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'originalSpectrum' field */
    public java.util.List<java.lang.Double> getOriginalSpectrum() {
      return originalSpectrum;
    }
    
    /** Sets the value of the 'originalSpectrum' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setOriginalSpectrum(java.util.List<java.lang.Double> value) {
      validate(fields()[6], value);
      this.originalSpectrum = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'originalSpectrum' field has been set */
    public boolean hasOriginalSpectrum() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'originalSpectrum' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearOriginalSpectrum() {
      originalSpectrum = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /** Gets the value of the 'spectrum' field */
    public java.util.List<java.lang.Double> getSpectrum() {
      return spectrum;
    }
    
    /** Sets the value of the 'spectrum' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setSpectrum(java.util.List<java.lang.Double> value) {
      validate(fields()[7], value);
      this.spectrum = value;
      fieldSetFlags()[7] = true;
      return this; 
    }
    
    /** Checks whether the 'spectrum' field has been set */
    public boolean hasSpectrum() {
      return fieldSetFlags()[7];
    }
    
    /** Clears the value of the 'spectrum' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearSpectrum() {
      spectrum = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /** Gets the value of the 'compoundPredictions' field */
    public java.util.List<java.lang.Double> getCompoundPredictions() {
      return compoundPredictions;
    }
    
    /** Sets the value of the 'compoundPredictions' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setCompoundPredictions(java.util.List<java.lang.Double> value) {
      validate(fields()[8], value);
      this.compoundPredictions = value;
      fieldSetFlags()[8] = true;
      return this; 
    }
    
    /** Checks whether the 'compoundPredictions' field has been set */
    public boolean hasCompoundPredictions() {
      return fieldSetFlags()[8];
    }
    
    /** Clears the value of the 'compoundPredictions' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearCompoundPredictions() {
      compoundPredictions = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    /** Gets the value of the 'compoundVetos' field */
    public java.util.List<java.lang.Boolean> getCompoundVetos() {
      return compoundVetos;
    }
    
    /** Sets the value of the 'compoundVetos' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder setCompoundVetos(java.util.List<java.lang.Boolean> value) {
      validate(fields()[9], value);
      this.compoundVetos = value;
      fieldSetFlags()[9] = true;
      return this; 
    }
    
    /** Checks whether the 'compoundVetos' field has been set */
    public boolean hasCompoundVetos() {
      return fieldSetFlags()[9];
    }
    
    /** Clears the value of the 'compoundVetos' field */
    public gov.pnnl.aim.nmr.avro.NMRSampleMessage.Builder clearCompoundVetos() {
      compoundVetos = null;
      fieldSetFlags()[9] = false;
      return this;
    }

    @Override
    public NMRSampleMessage build() {
      try {
        NMRSampleMessage record = new NMRSampleMessage();
        record.version = fieldSetFlags()[0] ? this.version : (java.lang.Integer) defaultValue(fields()[0]);
        record.timestamp = fieldSetFlags()[1] ? this.timestamp : (java.lang.Long) defaultValue(fields()[1]);
        record.fileName = fieldSetFlags()[2] ? this.fileName : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.rowNumber = fieldSetFlags()[3] ? this.rowNumber : (java.lang.Integer) defaultValue(fields()[3]);
        record.sampleID = fieldSetFlags()[4] ? this.sampleID : (java.lang.CharSequence) defaultValue(fields()[4]);
        record.qualityStatus = fieldSetFlags()[5] ? this.qualityStatus : (java.lang.Integer) defaultValue(fields()[5]);
        record.originalSpectrum = fieldSetFlags()[6] ? this.originalSpectrum : (java.util.List<java.lang.Double>) defaultValue(fields()[6]);
        record.spectrum = fieldSetFlags()[7] ? this.spectrum : (java.util.List<java.lang.Double>) defaultValue(fields()[7]);
        record.compoundPredictions = fieldSetFlags()[8] ? this.compoundPredictions : (java.util.List<java.lang.Double>) defaultValue(fields()[8]);
        record.compoundVetos = fieldSetFlags()[9] ? this.compoundVetos : (java.util.List<java.lang.Boolean>) defaultValue(fields()[9]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
