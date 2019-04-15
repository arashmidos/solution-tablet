package com.parsroyal.solutiontablet.vrp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "trip"
})
@JsonIgnoreProperties(ignoreUnknown = true)

public class OptimizedRouteResponse implements Serializable {

  private final static long serialVersionUID = -2726221506292552188L;
  @JsonProperty("id")
  private String id;
  @JsonProperty("trip")
  private TripResponse trip;
  private String error;

  /**
   * No args constructor for use in serialization
   */
  public OptimizedRouteResponse() {
  }

  /**
   *
   * @param id
   * @param trip
   */
  public OptimizedRouteResponse(String id, TripResponse trip) {
    super();
    this.id = id;
    this.trip = trip;
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public OptimizedRouteResponse withId(String id) {
    this.id = id;
    return this;
  }

  @JsonProperty("trip")
  public TripResponse getTrip() {
    return trip;
  }

  @JsonProperty("trip")
  public void setTrip(TripResponse trip) {
    this.trip = trip;
  }

  public OptimizedRouteResponse withTrip(TripResponse trip) {
    this.trip = trip;
    return this;
  }

  @Override
  public String toString() {
//    return new ToStringBuilder(this).append("id", id).append("trip", trip).toString();
    return "";
  }

  public OptimizedRouteResponse withError(String error) {
    this.error = error;
    return this;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
