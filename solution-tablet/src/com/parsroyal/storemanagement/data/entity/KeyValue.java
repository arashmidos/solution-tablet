package com.parsroyal.storemanagement.data.entity;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class KeyValue extends BaseEntity<Long> {

  public static final String COL_ID = "_id";
  public static final String COL_KEY = "KEY";
  public static final String COL_VALUE = "VALUE";
  public static final String TABLE_NAME = "BASE_INFO_KEY_VALUE";
  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + KeyValue.TABLE_NAME + " (" +
      KeyValue.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      KeyValue.COL_KEY + " TEXT NOT NULL, " +
      KeyValue.COL_VALUE + " TEXT NOT NULL);";
  private Long id;
  private String key;
  private String value;

  public KeyValue() {
  }

  public KeyValue(Long id, String key, String value) {
    this.id = id;
    this.key = key;
    this.value = value;
  }

  public KeyValue(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

}
