/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.pnnl.aim.discovery.streams.avro;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class DocumentVectorMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DocumentVectorMessage\",\"namespace\":\"gov.pnnl.aim.discovery.streams.avro\",\"fields\":[{\"name\":\"dataset\",\"type\":\"string\"},{\"name\":\"file\",\"type\":\"string\"},{\"name\":\"content\",\"type\":\"string\"},{\"name\":\"vector\",\"type\":{\"type\":\"array\",\"items\":\"double\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence dataset;
  @Deprecated public java.lang.CharSequence file;
  @Deprecated public java.lang.CharSequence content;
  @Deprecated public java.util.List<java.lang.Double> vector;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public DocumentVectorMessage() {}

  /**
   * All-args constructor.
   */
  public DocumentVectorMessage(java.lang.CharSequence dataset, java.lang.CharSequence file, java.lang.CharSequence content, java.util.List<java.lang.Double> vector) {
    this.dataset = dataset;
    this.file = file;
    this.content = content;
    this.vector = vector;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return dataset;
    case 1: return file;
    case 2: return content;
    case 3: return vector;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: dataset = (java.lang.CharSequence)value$; break;
    case 1: file = (java.lang.CharSequence)value$; break;
    case 2: content = (java.lang.CharSequence)value$; break;
    case 3: vector = (java.util.List<java.lang.Double>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'dataset' field.
   */
  public java.lang.CharSequence getDataset() {
    return dataset;
  }

  /**
   * Sets the value of the 'dataset' field.
   * @param value the value to set.
   */
  public void setDataset(java.lang.CharSequence value) {
    this.dataset = value;
  }

  /**
   * Gets the value of the 'file' field.
   */
  public java.lang.CharSequence getFile() {
    return file;
  }

  /**
   * Sets the value of the 'file' field.
   * @param value the value to set.
   */
  public void setFile(java.lang.CharSequence value) {
    this.file = value;
  }

  /**
   * Gets the value of the 'content' field.
   */
  public java.lang.CharSequence getContent() {
    return content;
  }

  /**
   * Sets the value of the 'content' field.
   * @param value the value to set.
   */
  public void setContent(java.lang.CharSequence value) {
    this.content = value;
  }

  /**
   * Gets the value of the 'vector' field.
   */
  public java.util.List<java.lang.Double> getVector() {
    return vector;
  }

  /**
   * Sets the value of the 'vector' field.
   * @param value the value to set.
   */
  public void setVector(java.util.List<java.lang.Double> value) {
    this.vector = value;
  }

  /** Creates a new DocumentVectorMessage RecordBuilder */
  public static gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder newBuilder() {
    return new gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder();
  }
  
  /** Creates a new DocumentVectorMessage RecordBuilder by copying an existing Builder */
  public static gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder newBuilder(gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder other) {
    return new gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder(other);
  }
  
  /** Creates a new DocumentVectorMessage RecordBuilder by copying an existing DocumentVectorMessage instance */
  public static gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder newBuilder(gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage other) {
    return new gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder(other);
  }
  
  /**
   * RecordBuilder for DocumentVectorMessage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<DocumentVectorMessage>
    implements org.apache.avro.data.RecordBuilder<DocumentVectorMessage> {

    private java.lang.CharSequence dataset;
    private java.lang.CharSequence file;
    private java.lang.CharSequence content;
    private java.util.List<java.lang.Double> vector;

    /** Creates a new Builder */
    private Builder() {
      super(gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.dataset)) {
        this.dataset = data().deepCopy(fields()[0].schema(), other.dataset);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.file)) {
        this.file = data().deepCopy(fields()[1].schema(), other.file);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.content)) {
        this.content = data().deepCopy(fields()[2].schema(), other.content);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.vector)) {
        this.vector = data().deepCopy(fields()[3].schema(), other.vector);
        fieldSetFlags()[3] = true;
      }
    }
    
    /** Creates a Builder by copying an existing DocumentVectorMessage instance */
    private Builder(gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage other) {
            super(gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.SCHEMA$);
      if (isValidValue(fields()[0], other.dataset)) {
        this.dataset = data().deepCopy(fields()[0].schema(), other.dataset);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.file)) {
        this.file = data().deepCopy(fields()[1].schema(), other.file);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.content)) {
        this.content = data().deepCopy(fields()[2].schema(), other.content);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.vector)) {
        this.vector = data().deepCopy(fields()[3].schema(), other.vector);
        fieldSetFlags()[3] = true;
      }
    }

    /** Gets the value of the 'dataset' field */
    public java.lang.CharSequence getDataset() {
      return dataset;
    }
    
    /** Sets the value of the 'dataset' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder setDataset(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.dataset = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'dataset' field has been set */
    public boolean hasDataset() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'dataset' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder clearDataset() {
      dataset = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'file' field */
    public java.lang.CharSequence getFile() {
      return file;
    }
    
    /** Sets the value of the 'file' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder setFile(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.file = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'file' field has been set */
    public boolean hasFile() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'file' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder clearFile() {
      file = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'content' field */
    public java.lang.CharSequence getContent() {
      return content;
    }
    
    /** Sets the value of the 'content' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder setContent(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.content = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'content' field has been set */
    public boolean hasContent() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'content' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder clearContent() {
      content = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'vector' field */
    public java.util.List<java.lang.Double> getVector() {
      return vector;
    }
    
    /** Sets the value of the 'vector' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder setVector(java.util.List<java.lang.Double> value) {
      validate(fields()[3], value);
      this.vector = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'vector' field has been set */
    public boolean hasVector() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'vector' field */
    public gov.pnnl.aim.discovery.streams.avro.DocumentVectorMessage.Builder clearVector() {
      vector = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    public DocumentVectorMessage build() {
      try {
        DocumentVectorMessage record = new DocumentVectorMessage();
        record.dataset = fieldSetFlags()[0] ? this.dataset : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.file = fieldSetFlags()[1] ? this.file : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.content = fieldSetFlags()[2] ? this.content : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.vector = fieldSetFlags()[3] ? this.vector : (java.util.List<java.lang.Double>) defaultValue(fields()[3]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}