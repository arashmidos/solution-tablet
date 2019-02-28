package com.parsroyal.storemanagement.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.parsroyal.storemanagement.constants.QuestionType;

/**
 * Created by Mahyar on 7/26/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionDto extends BaseModel {

  private Long questionId;
  private Long questionnaireId;//QuestionnaireBackendId
  private Integer order;
  private String questionnaireTitle;
  private String question;
  private String description;
  private Long answerId;
  private String answer;
  private String qAnswers;
  private Long status;
  @JsonProperty("id")
  @SerializedName("id")
  private Long backendId;
  private String createDateTime;
  private QuestionType type;
  private boolean required;
  private Long prerequisite;

  public String getqAnswers() {
    return qAnswers;
  }

  public void setqAnswers(String qAnswers) {
    this.qAnswers = qAnswers;
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public String getQuestionnaireTitle() {
    return questionnaireTitle;
  }

  public void setQuestionnaireTitle(String questionnaireTitle) {
    this.questionnaireTitle = questionnaireTitle;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public Long getAnswerId() {
    return answerId;
  }

  public void setAnswerId(Long answerId) {
    this.answerId = answerId;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  /**
   * @return BackendId of this question
   */
  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }

  /**
   * @return Questionnaire backendId which this question belongs to
   */
  public Long getQuestionnaireId() {
    return questionnaireId;
  }

  /**
   * @param questionnaireId Set questionnaire backendId for this question
   */
  public void setQuestionnaireId(Long questionnaireId) {
    this.questionnaireId = questionnaireId;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public Long getPrerequisite() {
    return prerequisite;
  }

  public void setPrerequisite(Long prerequisite) {
    this.prerequisite = prerequisite;
  }
}
