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
    "travel_type",
    "street_names",
    "verbal_pre_transition_instruction",
    "instruction",
    "end_shape_index",
    "type",
    "time",
    "verbal_multi_cue",
    "length",
    "begin_shape_index",
    "travel_mode",
    "verbal_transition_alert_instruction",
    "verbal_post_transition_instruction"
})
@JsonIgnoreProperties(ignoreUnknown = true)

public class Maneuver implements Serializable {

  private final static long serialVersionUID = -7327294108116598848L;
  @JsonProperty("travel_type")
  private String travel_type;
  @JsonProperty("street_names")
  private List<String> street_names = null;
  @JsonProperty("verbal_pre_transition_instruction")
  private String verbal_pre_transition_instruction;
  @JsonProperty("instruction")
  private String instruction;
  @JsonProperty("end_shape_index")
  private Integer endShapeIndex;
  @JsonProperty("type")
  private Integer type;
  @JsonProperty("time")
  private Integer time;
  @JsonProperty("verbal_multi_cue")
  private Boolean verbalMultiCue;
  @JsonProperty("length")
  private Integer length;
  @JsonProperty("begin_shape_index")
  private Integer beginShapeIndex;
  @JsonProperty("travel_mode")
  private String travelMode;
  @JsonProperty("verbal_transition_alert_instruction")
  private String verbalTransitionAlertInstruction;
  @JsonProperty("verbal_post_transition_instruction")
  private String verbalPostTransitionInstruction;

  /**
   * No args constructor for use in serialization
   */
  public Maneuver() {
  }

  /**
   *
   * @param beginShapeIndex
   * @param verbalMultiCue
   * @param travel_type
   * @param type
   * @param travelMode
   * @param verbalTransitionAlertInstruction
   * @param street_names
   * @param time
   * @param length
   * @param instruction
   * @param verbal_pre_transition_instruction
   * @param verbalPostTransitionInstruction
   * @param endShapeIndex
   */
  public Maneuver(String travel_type, List<String> street_names,
      String verbal_pre_transition_instruction, String instruction, Integer endShapeIndex,
      Integer type, Integer time, Boolean verbalMultiCue, Integer length, Integer beginShapeIndex,
      String travelMode, String verbalTransitionAlertInstruction,
      String verbalPostTransitionInstruction) {
    super();
    this.travel_type = travel_type;
    this.street_names = street_names;
    this.verbal_pre_transition_instruction = verbal_pre_transition_instruction;
    this.instruction = instruction;
    this.endShapeIndex = endShapeIndex;
    this.type = type;
    this.time = time;
    this.verbalMultiCue = verbalMultiCue;
    this.length = length;
    this.beginShapeIndex = beginShapeIndex;
    this.travelMode = travelMode;
    this.verbalTransitionAlertInstruction = verbalTransitionAlertInstruction;
    this.verbalPostTransitionInstruction = verbalPostTransitionInstruction;
  }

  @JsonProperty("travel_type")
  public String getTravel_type() {
    return travel_type;
  }

  @JsonProperty("travel_type")
  public void setTravel_type(String travel_type) {
    this.travel_type = travel_type;
  }

  public Maneuver withTravelType(String travelType) {
    this.travel_type = travelType;
    return this;
  }

  @JsonProperty("street_names")
  public List<String> getStreet_names() {
    return street_names;
  }

  @JsonProperty("street_names")
  public void setStreet_names(List<String> street_names) {
    this.street_names = street_names;
  }

  public Maneuver withStreetNames(List<String> streetNames) {
    this.street_names = streetNames;
    return this;
  }

  @JsonProperty("verbal_pre_transition_instruction")
  public String getVerbal_pre_transition_instruction() {
    return verbal_pre_transition_instruction;
  }

  @JsonProperty("verbal_pre_transition_instruction")
  public void setVerbal_pre_transition_instruction(String verbal_pre_transition_instruction) {
    this.verbal_pre_transition_instruction = verbal_pre_transition_instruction;
  }

  public Maneuver withVerbalPreTransitionInstruction(String verbalPreTransitionInstruction) {
    this.verbal_pre_transition_instruction = verbalPreTransitionInstruction;
    return this;
  }

  @JsonProperty("instruction")
  public String getInstruction() {
    return instruction;
  }

  @JsonProperty("instruction")
  public void setInstruction(String instruction) {
    this.instruction = instruction;
  }

  public Maneuver withInstruction(String instruction) {
    this.instruction = instruction;
    return this;
  }

  @JsonProperty("end_shape_index")
  public Integer getEndShapeIndex() {
    return endShapeIndex;
  }

  @JsonProperty("end_shape_index")
  public void setEndShapeIndex(Integer endShapeIndex) {
    this.endShapeIndex = endShapeIndex;
  }

  public Maneuver withEndShapeIndex(Integer endShapeIndex) {
    this.endShapeIndex = endShapeIndex;
    return this;
  }

  @JsonProperty("type")
  public Integer getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(Integer type) {
    this.type = type;
  }

  public Maneuver withType(Integer type) {
    this.type = type;
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

  public Maneuver withTime(Integer time) {
    this.time = time;
    return this;
  }

  @JsonProperty("verbal_multi_cue")
  public Boolean getVerbalMultiCue() {
    return verbalMultiCue;
  }

  @JsonProperty("verbal_multi_cue")
  public void setVerbalMultiCue(Boolean verbalMultiCue) {
    this.verbalMultiCue = verbalMultiCue;
  }

  public Maneuver withVerbalMultiCue(Boolean verbalMultiCue) {
    this.verbalMultiCue = verbalMultiCue;
    return this;
  }

  @JsonProperty("length")
  public Integer getLength() {
    return length;
  }

  @JsonProperty("length")
  public void setLength(Integer length) {
    this.length = length;
  }

  public Maneuver withLength(Integer length) {
    this.length = length;
    return this;
  }

  @JsonProperty("begin_shape_index")
  public Integer getBeginShapeIndex() {
    return beginShapeIndex;
  }

  @JsonProperty("begin_shape_index")
  public void setBeginShapeIndex(Integer beginShapeIndex) {
    this.beginShapeIndex = beginShapeIndex;
  }

  public Maneuver withBeginShapeIndex(Integer beginShapeIndex) {
    this.beginShapeIndex = beginShapeIndex;
    return this;
  }

  @JsonProperty("travel_mode")
  public String getTravelMode() {
    return travelMode;
  }

  @JsonProperty("travel_mode")
  public void setTravelMode(String travelMode) {
    this.travelMode = travelMode;
  }

  public Maneuver withTravelMode(String travelMode) {
    this.travelMode = travelMode;
    return this;
  }

  @JsonProperty("verbal_transition_alert_instruction")
  public String getVerbalTransitionAlertInstruction() {
    return verbalTransitionAlertInstruction;
  }

  @JsonProperty("verbal_transition_alert_instruction")
  public void setVerbalTransitionAlertInstruction(String verbalTransitionAlertInstruction) {
    this.verbalTransitionAlertInstruction = verbalTransitionAlertInstruction;
  }

  public Maneuver withVerbalTransitionAlertInstruction(String verbalTransitionAlertInstruction) {
    this.verbalTransitionAlertInstruction = verbalTransitionAlertInstruction;
    return this;
  }

  @JsonProperty("verbal_post_transition_instruction")
  public String getVerbalPostTransitionInstruction() {
    return verbalPostTransitionInstruction;
  }

  @JsonProperty("verbal_post_transition_instruction")
  public void setVerbalPostTransitionInstruction(String verbalPostTransitionInstruction) {
    this.verbalPostTransitionInstruction = verbalPostTransitionInstruction;
  }

  public Maneuver withVerbalPostTransitionInstruction(String verbalPostTransitionInstruction) {
    this.verbalPostTransitionInstruction = verbalPostTransitionInstruction;
    return this;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("travel_type", travel_type)
        .append("street_names", street_names)
        .append("verbal_pre_transition_instruction", verbal_pre_transition_instruction)
        .append("instruction", instruction).append("endShapeIndex", endShapeIndex)
        .append("type", type).append("time", time).append("verbalMultiCue", verbalMultiCue)
        .append("length", length).append("beginShapeIndex", beginShapeIndex)
        .append("travelMode", travelMode)
        .append("verbalTransitionAlertInstruction", verbalTransitionAlertInstruction)
        .append("verbalPostTransitionInstruction", verbalPostTransitionInstruction).toString();

  }

}
