package com.parsroyal.solutiontablet.vrp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "original_index",
    "type",
    "side_of_street",
    "lat",
    "lon",
    "name"
})
@JsonIgnoreProperties(ignoreUnknown = true)

public class LocationResponse implements Serializable {

  private final static long serialVersionUID = -3402516095735504415L;
  @JsonProperty("original_index")
  private Integer original_index;
  @JsonProperty("type")
  private String type;
  @JsonProperty("side_of_street")
  private String side_of_street;
  @JsonProperty("lat")
  private Double lat;
  @JsonProperty("lon")
  private Double lon;
  @JsonProperty("name")
  private String name;

  /**
   * No args constructor for use in serialization
   */
  public LocationResponse() {
  }

  /**
   *
   * @param lon
   * @param side_of_street
   * @param original_index
   * @param name
   * @param type
   * @param lat
   */
  public LocationResponse(Integer original_index, String type, String side_of_street, Double lat,
      Double lon, String name) {
    super();
    this.original_index = original_index;
    this.type = type;
    this.side_of_street = side_of_street;
    this.lat = lat;
    this.lon = lon;
    this.name = name;
  }

  @JsonProperty("original_index")
  public Integer getOriginal_index() {
    return original_index;
  }

  @JsonProperty("original_index")
  public void setOriginal_index(Integer original_index) {
    this.original_index = original_index;
  }

  public LocationResponse withOriginalIndex(Integer originalIndex) {
    this.original_index = originalIndex;
    return this;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  public LocationResponse withType(String type) {
    this.type = type;
    return this;
  }

  @JsonProperty("side_of_street")
  public String getSide_of_street() {
    return side_of_street;
  }

  @JsonProperty("side_of_street")
  public void setSide_of_street(String side_of_street) {
    this.side_of_street = side_of_street;
  }

  public LocationResponse withSideOfStreet(String sideOfStreet) {
    this.side_of_street = sideOfStreet;
    return this;
  }

  @JsonProperty("lat")
  public Double getLat() {
    return lat;
  }

  @JsonProperty("lat")
  public void setLat(Double lat) {
    this.lat = lat;
  }

  public LocationResponse withLat(Double lat) {
    this.lat = lat;
    return this;
  }

  @JsonProperty("lon")
  public Double getLon() {
    return lon;
  }

  @JsonProperty("lon")
  public void setLon(Double lon) {
    this.lon = lon;
  }

  public LocationResponse withLon(Double lon) {
    this.lon = lon;
    return this;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public LocationResponse withName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("original_index", original_index).append("type", type)
        .append("side_of_street", side_of_street).append("lat", lat).append("lon", lon)
        .append("name", name).toString();
  }

}
