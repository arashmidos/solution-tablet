package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.constants.QuestionType;
import com.parsroyal.solutiontablet.util.DateUtil;
import java.util.Date;

/**
 * Created by Mahyar on 7/21/2015.
 */
public class Question extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_QUESTION";

  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_QUESTIONNAIRE_BACKEND_ID = "QUESTIONNAIRE_BACKEND_ID";
  public static final String COL_QUESTION = "QUESTION";
  public static final String COL_ANSWER = "ANSWER";
  public static final String COL_STATUS = "STATUS";
  public static final String COL_ORDER = "qORDER";
  public static final String COL_TYPE = "TYPE";
  public static final String COL_REQUIRED = "REQUIRED";
  public static final String COL_PREREQUISITE = "PREREQUISITE";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + Question.TABLE_NAME + " (" +
      " " + Question.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + Question.COL_BACKEND_ID + " INTEGER," +
      " " + Question.COL_QUESTIONNAIRE_BACKEND_ID + " INTEGER NOT NULL," +
      " " + Question.COL_QUESTION + " TEXT," +
      " " + Question.COL_ANSWER + " TEXT," +
      " " + Question.COL_STATUS + " INTEGER," +
      " " + Question.COL_ORDER + " INTEGER," +
      " " + Question.COL_CREATE_DATE_TIME + " TEXT," +
      " " + Question.COL_UPDATE_DATE_TIME + " TEXT," +
      " " + Question.COL_TYPE + " INTEGER," +
      " " + Question.COL_REQUIRED + " INTEGER," +
      " " + Question.COL_PREREQUISITE + " INTEGER" +
      " );";

  private Long id;
  private Long backendId;
  private Long questionnaireBackendId;
  private String question;
  private String answer;
  private Long status;
  private Integer order;
  private QuestionType type;
  private boolean required;
  private Long preRequisite;

  public Question(Long backendId, Long questionnaireBackendId, String question,
      String answer, Long status, Integer order, QuestionType type, boolean required,
      Long prerequisite) {
    this.backendId = backendId;
    this.questionnaireBackendId = questionnaireBackendId;
    this.question = question;
    this.answer = answer;
    this.status = status;
    this.order = order;
    this.type = type;
    this.createDateTime = DateUtil
        .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
    this.updateDateTime = DateUtil
        .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
    this.required = required;
    this.preRequisite = prerequisite;
  }

  public Question() {
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public Long getQuestionnaireBackendId() {
    return questionnaireBackendId;
  }

  public void setQuestionnaireBackendId(Long questionnaireBackendId) {
    this.questionnaireBackendId = questionnaireBackendId;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public Long getPreRequisite() {
    return preRequisite;
  }

  public void setPreRequisite(Long preRequisite) {
    this.preRequisite = preRequisite;
  }
}
