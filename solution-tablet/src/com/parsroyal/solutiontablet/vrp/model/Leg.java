package com.parsroyal.solutiontablet.vrp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "shape",
    "summary",
    "maneuvers"
})
@JsonIgnoreProperties(ignoreUnknown = true)

public class Leg implements Serializable {

  private final static long serialVersionUID = 428822442772825649L;
  @JsonProperty("shape")
  private String shape;
  @JsonProperty("summary")
  private TripSummaryResponse summary;
  @JsonProperty("maneuvers")
  private List<Maneuver> maneuvers = null;

  /**
   * No args constructor for use in serialization
   */
  public Leg() {
  }

  /**
   *
   * @param summary
   * @param shape
   * @param maneuvers
   */
  public Leg(String shape, TripSummaryResponse summary, List<Maneuver> maneuvers) {
    super();
    this.shape = shape;
    this.summary = summary;
    this.maneuvers = maneuvers;
  }

  @JsonProperty("shape")
  public String getShape() {
    return shape;
  }

  @JsonProperty("shape")
  public void setShape(String shape) {
    this.shape = shape;
  }

  public Leg withShape(String shape) {
    this.shape = shape;
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

  public Leg withSummary(TripSummaryResponse summary) {
    this.summary = summary;
    return this;
  }

  @JsonProperty("maneuvers")
  public List<Maneuver> getManeuvers() {
    return maneuvers;
  }

  @JsonProperty("maneuvers")
  public void setManeuvers(List<Maneuver> maneuvers) {
    this.maneuvers = maneuvers;
  }

  public Leg withManeuvers(List<Maneuver> maneuvers) {
    this.maneuvers = maneuvers;
    return this;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("shape", shape).append("summary", summary)
        .append("maneuvers", maneuvers).toString();
  }

}
