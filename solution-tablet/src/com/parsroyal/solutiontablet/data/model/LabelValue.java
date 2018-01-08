package com.parsroyal.solutiontablet.data.model;

/**
 * Created by Mahyar on 6/22/2015.
 */
public class LabelValue extends BaseModel {

  private String label;
  private Long value;
  private String code;

  public LabelValue(Long value, String label) {
    this.label = label;
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
    this.value = value;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LabelValue that = (LabelValue) o;

    if (label != null ? !label.equals(that.label) : that.label != null) {
      return false;
    }
    if (value != null ? !value.equals(that.value) : that.value != null) {
      return false;
    }
    return code != null ? code.equals(that.code) : that.code == null;

  }

  @Override
  public int hashCode() {
    int result = label != null ? label.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (code != null ? code.hashCode() : 0);
    return result;
  }
}
