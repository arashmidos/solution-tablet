package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.constants.QuestionType;
import com.parsroyal.solutiontablet.data.dao.QuestionDao;
import com.parsroyal.solutiontablet.data.entity.Question;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionDaoImpl extends AbstractDao<Question, Long> implements QuestionDao {

  private Context context;

  public QuestionDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(Question entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(Question.COL_ID, entity.getId());
    contentValues.put(Question.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(Question.COL_QUESTIONNAIRE_BACKEND_ID, entity.getQuestionnaireBackendId());
    contentValues.put(Question.COL_QUESTION, entity.getQuestion());
    contentValues.put(Question.COL_ANSWER, entity.getAnswer());
    contentValues.put(Question.COL_STATUS, entity.getStatus());
    contentValues.put(Question.COL_ORDER, entity.getOrder());
    contentValues.put(Question.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(Question.COL_UPDATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(Question.COL_TYPE, entity.getType().getValue());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return Question.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return Question.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    String[] projection = {
        Question.COL_ID,
        Question.COL_BACKEND_ID,
        Question.COL_QUESTIONNAIRE_BACKEND_ID,
        Question.COL_QUESTION,
        Question.COL_ANSWER,
        Question.COL_STATUS,
        Question.COL_ORDER,
        Question.COL_CREATE_DATE_TIME,
        Question.COL_UPDATE_DATE_TIME,
        Question.COL_TYPE
    };
    return projection;
  }

  @Override
  protected Question createEntityFromCursor(Cursor cursor) {
    Question question = new Question();
    question.setId(cursor.getLong(0));
    question.setBackendId(cursor.getLong(1));
    question.setQuestionnaireBackendId(cursor.getLong(2));
    question.setQuestion(cursor.getString(3));
    question.setAnswer(cursor.getString(4));
    question.setStatus(cursor.getLong(5));
    question.setOrder(cursor.getInt(6));
    question.setCreateDateTime(cursor.getString(7));
    question.setUpdateDateTime(cursor.getString(8));
    question.setType(QuestionType.getByValue(cursor.getInt(9)));
    return question;
  }

  @Override
  public List<QuestionListModel> searchForQuestions(QuestionSo questionSo) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql = "SELECT" +
        "  q._id," +
        "  q.QUESTION," +
        "  q.qOrder," +
        "  q.TYPE," +
        "  a.ANSWER" +
        " FROM COMMER_QUESTION q " +
        "  LEFT OUTER JOIN COMMER_Q_ANSWER a ON a.QUESTION_BACKEND_ID = q.BACKEND_ID  " +
        " and a.VISIT_ID = ? and (a.GOODS_BACKEND_ID = ? or  '-1' = ?)" +
        " LEFT OUTER JOIN COMMER_VISIT_INFORMATION v on v._id = a.VISIT_ID " +
        " WHERE q.QUESTIONNAIRE_BACKEND_ID = ? ORDER BY q.qOrder ";
    sql = sql.concat(" ");

    String[] args = {String.valueOf(questionSo.getVisitId()),
        Empty.isNotEmpty(questionSo.getGoodsBackendId()) ? String
            .valueOf(questionSo.getGoodsBackendId()) : "-1",
        Empty.isNotEmpty(questionSo.getGoodsBackendId()) ? String
            .valueOf(questionSo.getGoodsBackendId()) : "-1",
        String.valueOf(questionSo.getQuestionnaireBackendId())
    };
    Cursor cursor = db.rawQuery(sql, args);

    List<QuestionListModel> questions = new ArrayList<>();
    while (cursor.moveToNext()) {
      QuestionListModel questionListModel = new QuestionListModel();
      questionListModel.setPrimaryKey(cursor.getLong(0));
      questionListModel.setQuestion(cursor.getString(1));
      questionListModel.setqOrder(cursor.getInt(2));
      questionListModel.setType(QuestionType.getByValue(cursor.getInt(3)));
      questionListModel.setAnswer(cursor.getString(4));
      questions.add(questionListModel);
    }

    cursor.close();
    return questions;
  }

  @Override
  public QuestionDto getQuestionDto(Long questionId, Long visitId, Long goodsBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    QuestionDto questionDto = null;

    String sql = "SELECT" +
        " q._id," +//0
        " q.QUESTION," +//1
        " q.ANSWER," +//2
        " q.qORDER," +//3
        " qn.DESCRIPTION," +//4
        " IFNULL(an._id, NULL)," +//5
        " IFNULL(an.ANSWER, NULL)," +//6
        " q.BACKEND_ID," +//7
        " q.TYPE" +//8
        " FROM COMMER_QUESTION q" +
        " INNER JOIN COMMER_QUESTIONNAIRE qn on qn.BACKEND_ID= q.QUESTIONNAIRE_BACKEND_ID" +
        " Left OUTER JOIN COMMER_Q_ANSWER an on an.QUESTION_BACKEND_ID = q.BACKEND_ID and an.VISIT_ID = ?"
        +
        " and (an.GOODS_BACKEND_ID = ? or  '-1' = ?)" +
        " where q._id = ?";

    String[] args = {String.valueOf(visitId),
        Empty.isNotEmpty(goodsBackendId) ? String.valueOf(goodsBackendId) : "-1",
        Empty.isNotEmpty(goodsBackendId) ? String.valueOf(goodsBackendId) : "-1",
        String.valueOf(questionId)};
    Cursor cursor = db.rawQuery(sql, args);

    if (cursor.moveToNext()) {
      questionDto = new QuestionDto();
      questionDto.setQuestionId(cursor.getLong(0));
      questionDto.setQuestion(cursor.getString(1));
      questionDto.setqAnswers(cursor.getString(2));
      questionDto.setqOrder(cursor.getInt(3));
      questionDto.setDescription(cursor.getString(4));
      questionDto.setAnswerId(cursor.isNull(5) ? null : cursor.getLong(5));
      questionDto.setAnswer(cursor.getString(6));
      questionDto.setQuestionBackendId(cursor.getLong(7));
      questionDto.setType(QuestionType.getByValue(cursor.getInt(8)));
    }

    cursor.close();
    return questionDto;
  }

  @Override
  public QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order,
      Long goodsBackendId, boolean isNext) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    QuestionDto questionDto = null;

    String sql = "SELECT" +
        " q._id," +//0
        " q.QUESTION," +//1
        " q.ANSWER," +//2
        " q.qORDER," +//3
        " qn.DESCRIPTION," +//4
        " IFNULL(an._id, NULL)," +//5
        " IFNULL(an.ANSWER, NULL)," +//6
        " q.BACKEND_ID," +//7
        " q.TYPE" +//8
        " FROM COMMER_QUESTION q" +
        " INNER JOIN COMMER_QUESTIONNAIRE qn on qn.BACKEND_ID= q.QUESTIONNAIRE_BACKEND_ID" +
        " Left OUTER JOIN COMMER_Q_ANSWER an on an.QUESTION_BACKEND_ID = q.BACKEND_ID" +
        " and an.VISIT_ID = ? and (an.GOODS_BACKEND_ID = ? or  '-1' = ?)" +
        " where qn.BACKEND_ID = ? AND q.qOrder " +
        (isNext ? "> ? " : "< ? ") + "ORDER BY q.qORDER " + (isNext ? "ASC" : "DESC") + " limit 1";

    String[] args = {
        String.valueOf(visitId),
        Empty.isNotEmpty(goodsBackendId) ? String.valueOf(goodsBackendId) : "-1",
        Empty.isNotEmpty(goodsBackendId) ? String.valueOf(goodsBackendId) : "-1",
        String.valueOf(questionnaireBackendId),
        String.valueOf(order)};
    Cursor cursor = db.rawQuery(sql, args);

    if (cursor.moveToNext()) {
      questionDto = new QuestionDto();
      questionDto.setQuestionId(cursor.getLong(0));
      questionDto.setQuestion(cursor.getString(1));
      questionDto.setqAnswers(cursor.getString(2));
      questionDto.setqOrder(cursor.getInt(3));
      questionDto.setDescription(cursor.getString(4));
      questionDto.setAnswerId(cursor.isNull(5) ? null : cursor.getLong(5));
      questionDto.setAnswer(cursor.getString(6));
      questionDto.setQuestionBackendId(cursor.getLong(7));
      questionDto.setType(QuestionType.getByValue(cursor.getInt(8)));
    }

    cursor.close();
    return questionDto;
  }
}
