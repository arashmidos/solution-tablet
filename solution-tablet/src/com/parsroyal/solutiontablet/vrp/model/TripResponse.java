package com.parsroyal.solutiontablet.vrp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "language",
    "summary",
    "locations",
    "units",
    "legs",
    "status_message",
    "status"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripResponse implements Serializable {

  private final static long serialVersionUID = 8750622604293463015L;
  @JsonProperty("language")
  private String language;
  @JsonProperty("summary")
  private TripSummaryResponse summary;
  @JsonProperty("locations")
  private List<LocationResponse> locations = null;
  @JsonProperty("units")
  private String units;
  @JsonProperty("legs")
  private List<Leg> legs = null;
  @JsonProperty("status_message")
  private String status_message;
  @JsonProperty("status")
  private Integer status;

  /**
   * No args constructor for use in serialization
   */
  public TripResponse() {
  }

  /**
   *
   * @param summary
   * @param status
   * @param locations
   * @param legs
   * @param language
   * @param units
   * @param status_message
   */
  public TripResponse(String language, TripSummaryResponse summary,
      List<LocationResponse> locations, String units,
      List<Leg> legs, String status_message, Integer status) {
    super();
    this.language = language;
    this.summary = summary;
    this.locations = locations;
    this.units = units;
    this.legs = legs;
    this.status_message = status_message;
    this.status = status;
  }

  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  @JsonProperty("language")
  public void setLanguage(String language) {
    this.language = language;
  }

  public TripResponse withLanguage(String language) {
    this.language = language;
    return this;
  }

  @JsonProperty("summary")
  public TripSummaryResponse getSummary() {
    return summary;
  }

  @JsonProperty("summary")
  public void setSummary(TripSummaryResponse summary) {
    this.summary = summary;
  }

  public TripResponse withSummary(TripSummaryResponse summary) {
    this.summary = summary;
    return this;
  }

  @JsonProperty("locations")
  public List<LocationResponse> getLocations() {
    return locations;
  }

  @JsonProperty("locations")
  public void setLocations(List<LocationResponse> locations) {
    this.locations = locations;
  }

  public TripResponse withLocations(List<LocationResponse> locations) {
    this.locations = locations;
    return this;
  }

  @JsonProperty("units")
  public String getUnits() {
    return units;
  }

  @JsonProperty("units")
  public void setUnits(String units) {
    this.units = units;
  }

  public TripResponse withUnits(String units) {
    this.units = units;
    return this;
  }

  @JsonProperty("legs")
  public List<Leg> getLegs() {
    return legs;
  }

  @JsonProperty("legs")
  public void setLegs(List<Leg> legs) {
    this.legs = legs;
  }

  public TripResponse withLegs(List<Leg> legs) {
    this.legs = legs;
    return this;
  }

  @JsonProperty("status_message")
  public String getStatus_message() {
    return status_message;
  }

  @JsonProperty("status_message")
  public void setStatus_message(String status_message) {
    this.status_message = status_message;
  }

  public TripResponse withStatusMessage(String statusMessage) {
    this.status_message = statusMessage;
    return this;
  }

  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(Integer status) {
    this.status = status;
  }

  public TripResponse withStatus(Integer status) {
    this.status = status;
    return this;
  }

  @Override
  public String toString() {
//        return new ToStringBuilder(this).append("language", language).append("summary", summary).append("locations", locations).append("units", units).append("legs", legs).append("status_message", status_message).append("status", status).toString();
    return "";
  }

}
