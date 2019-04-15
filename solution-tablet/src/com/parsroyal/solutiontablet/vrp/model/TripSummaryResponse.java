package com.parsroyal.solutiontablet.vrp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "max_lon",
    "max_lat",
    "time",
    "length",
    "min_lat",
    "min_lon"
})
@JsonIgnoreProperties(ignoreUnknown = true)

public class TripSummaryResponse implements Serializable {

  private final static long serialVersionUID = 6273693034329969906L;
  @JsonProperty("max_lon")
  private Double max_lon;
  @JsonProperty("max_lat")
  private Double max_lat;
  @JsonProperty("time")
  private Integer time;
  @JsonProperty("length")
  private Double length;
  @JsonProperty("min_lat")
  private Double min_lat;
  @JsonProperty("min_lon")
  private Double min_lon;

  /**
   * No args constructor for use in serialization
   */
  public TripSummaryResponse() {
  }

  /**
   *
   * @param time
   * @param min_lat
   * @param max_lat
   * @param min_lon
   * @param max_lon
   * @param length
   */
  public TripSummaryResponse(Double max_lon, Double max_lat, Integer time, Double length,
      Double min_lat, Double min_lon) {
    super();
    this.max_lon = max_lon;
    this.max_lat = max_lat;
    this.time = time;
    this.length = length;
    this.min_lat = min_lat;
    this.min_lon = min_lon;
  }

  @JsonProperty("max_lon")
  public Double getMax_lon() {
    return max_lon;
  }

  @JsonProperty("max_lon")
  public void setMax_lon(Double max_lon) {
    this.max_lon = max_lon;
  }

  public TripSummaryResponse withMaxLon(Double maxLon) {
    this.max_lon = maxLon;
    return this;
  }

  @JsonProperty("max_lat")
  public Double getMax_lat() {
    return max_lat;
  }

  @JsonProperty("max_lat")
  public void setMax_lat(Double max_lat) {
    this.max_lat = max_lat;
  }

  public TripSummaryResponse withMaxLat(Double maxLat) {
    this.max_lat = maxLat;
    return this;
  }

  @JsonProperty("time")
  public Integer getTime() {
    return time;
  }

  @JsonProperty("time")
  public void setTime(Integer time) {
    this.time = time;
  }

  public TripSummaryResponse withTime(Integer time) {
    this.time = time;
    return this;
  }

  @JsonProperty("length")
  public Double getLength() {
    return length;
  }

  @JsonProperty("length")
  public void setLength(Double length) {
    this.length = length;
  }

  public TripSummaryResponse withLength(Double length) {
    this.length = length;
    return this;
  }

  @JsonProperty("min_lat")
  public Double getMin_lat() {
    return min_lat;
  }

  @JsonProperty("min_lat")
  public void setMin_lat(Double min_lat) {
    this.min_lat = min_lat;
  }

  public TripSummaryResponse withMinLat(Double minLat) {
    this.min_lat = minLat;
    return this;
  }

  @JsonProperty("min_lon")
  public Double getMin_lon() {
    return min_lon;
  }

  @JsonProperty("min_lon")
  public void setMin_lon(Double min_lon) {
    this.min_lon = min_lon;
  }

  public TripSummaryResponse withMinLon(Double minLon) {
    this.min_lon = minLon;
    return this;
  }

  @Override
  public String toString() {
//        return new ToStringBuilder(this).append("max_lon", max_lon).append("max_lat", max_lat).append("time", time).append("length", length).append("min_lat", min_lat).append("min_lon", min_lon).toString();
    return "";
  }
}
