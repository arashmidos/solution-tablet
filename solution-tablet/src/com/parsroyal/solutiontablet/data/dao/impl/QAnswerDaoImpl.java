package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.entity.Question;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.model.AnswerDetailDto;
import com.parsroyal.solutiontablet.data.model.QAnswerDto;
import com.parsroyal.solutiontablet.util.DateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public class QAnswerDaoImpl extends AbstractDao<QAnswer, Long> implements QAnswerDao {

  private Context context;

  public QAnswerDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(QAnswer entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(QAnswer.COL_ID, entity.getId());
    contentValues.put(QAnswer.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(QAnswer.COL_QUESTION_BACKEND_ID, entity.getQuestionBackendId());
    contentValues.put(QAnswer.COL_ANSWER, entity.getAnswer());
    contentValues.put(QAnswer.COL_CUSTOMER_BACKEND_ID, entity.getCustomerBackendId());
    contentValues.put(QAnswer.COL_GOODS_BACKEND_ID, entity.getGoodsBackendId());
    contentValues.put(QAnswer.COL_VISIT_ID, entity.getVisitId());
    contentValues.put(QAnswer.COL_VISIT_BACKEND_ID, entity.getVisitBackendId());
    contentValues.put(QAnswer.COL_DATE, entity.getDate());
    contentValues.put(QAnswer.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(QAnswer.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    contentValues.put(QAnswer.COL_ANSWERS_GROUP_NO, entity.getAnswersGroupNo());
    contentValues.put(QAnswer.COL_STATUS, entity.getStatus());

    return contentValues;
  }

  @Override
  protected String getTableName() {
    return QAnswer.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return QAnswer.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        QAnswer.COL_ID,
        QAnswer.COL_BACKEND_ID,
        QAnswer.COL_QUESTION_BACKEND_ID,
        QAnswer.COL_ANSWER,
        QAnswer.COL_CUSTOMER_BACKEND_ID,
        QAnswer.COL_GOODS_BACKEND_ID,
        QAnswer.COL_VISIT_BACKEND_ID,
        QAnswer.COL_VISIT_ID,
        QAnswer.COL_DATE,
        QAnswer.COL_CREATE_DATE_TIME,
        QAnswer.COL_UPDATE_DATE_TIME,//10
        QAnswer.COL_ANSWERS_GROUP_NO,
        QAnswer.COL_STATUS
    };
  }

  @Override
  protected QAnswer createEntityFromCursor(Cursor cursor) {
    QAnswer answer = new QAnswer();

    answer.setId(cursor.getLong(0));
    answer.setBackendId(cursor.getLong(1));
    answer.setQuestionBackendId(cursor.getLong(2));
    answer.setAnswer(cursor.getString(3));
    answer.setCustomerBackendId(cursor.getLong(4));
    answer.setGoodsBackendId(cursor.getLong(5));
    answer.setVisitBackendId(cursor.getLong(6));
    answer.setVisitId(cursor.getLong(7));
    answer.setDate(cursor.getString(8));
    answer.setCreateDateTime(cursor.getString(9));
    answer.setUpdateDateTime(cursor.getString(10));
    answer.setAnswersGroupNo(cursor.getLong(11));
    answer.setStatus(cursor.getLong(12));

    return answer;
  }

  @Override
  public List<QAnswerDto> getAllQAnswersDtoForSend() {

    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql = "SELECT "
        + " a." + QAnswer.COL_CUSTOMER_BACKEND_ID
        + ",a." + QAnswer.COL_VISIT_ID
        + ",a." + QAnswer.COL_UPDATE_DATE_TIME
        + ",a." + QAnswer.COL_ANSWERS_GROUP_NO
        + ",q." + Question.COL_QUESTIONNAIRE_BACKEND_ID
        + ",a." + Question.COL_ID
        + " FROM " + QAnswer.TABLE_NAME
        + " a LEFT OUTER JOIN " + Question.TABLE_NAME + " q ON a." + QAnswer.COL_QUESTION_BACKEND_ID
        + "=  q." + Question.COL_BACKEND_ID;

    String selection =
        " WHERE a." + QAnswer.COL_BACKEND_ID + " is null or a." + QAnswer.COL_BACKEND_ID + " = 0 ";
    String groupBy = "GROUP BY a." + QAnswer.COL_ANSWERS_GROUP_NO;
    sql = sql.concat(selection).concat(groupBy);

    Cursor cursor = db.rawQuery(sql, null);

    List<QAnswerDto> answersForSend = new ArrayList<>();
    while (cursor.moveToNext()) {
      answersForSend.add(createDtoFromEntity(cursor));
    }

    cursor.close();

    return answersForSend;
  }

  @Override
  public List<QAnswerDto> getAllQAnswersDtoForSend(Long visitId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql = "SELECT "
        + " a." + QAnswer.COL_CUSTOMER_BACKEND_ID
        + ",a." + QAnswer.COL_VISIT_ID
        + ",a." + QAnswer.COL_UPDATE_DATE_TIME
        + ",a." + QAnswer.COL_ANSWERS_GROUP_NO
        + ",q." + Question.COL_QUESTIONNAIRE_BACKEND_ID
        + ",a." + Question.COL_ID
        + " FROM " + QAnswer.TABLE_NAME
        + " a LEFT OUTER JOIN " + Question.TABLE_NAME + " q ON a." + QAnswer.COL_QUESTION_BACKEND_ID
        + "=  q." + Question.COL_BACKEND_ID;

    String selection =
        " WHERE (a." + QAnswer.COL_BACKEND_ID + " is null or a." + QAnswer.COL_BACKEND_ID + " = 0) "
            + "AND " + QAnswer.COL_VISIT_ID + " = " + visitId;
    String groupBy = " GROUP BY a." + QAnswer.COL_ANSWERS_GROUP_NO;
    sql = sql.concat(selection).concat(groupBy);

    Cursor cursor = db.rawQuery(sql, null);

    List<QAnswerDto> answersForSend = new ArrayList<>();
    while (cursor.moveToNext()) {
      answersForSend.add(createDtoFromEntity(cursor));
    }

    cursor.close();

    return answersForSend;
  }

  private QAnswerDto createDtoFromEntity(Cursor cursor) {
    Date date = DateUtil.convertStringToDate(cursor.getString(2),
        DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
    return new QAnswerDto(cursor.getLong(0), cursor.getLong(1), date, cursor.getLong(4),
        cursor.getLong(3));
  }

  @Override
  public void updateCustomerBackendIdForAnswers(long customerId, long customerBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause =
        QAnswer.COL_CUSTOMER_BACKEND_ID + " = ? AND " + QAnswer.COL_STATUS + " = ? ";
    String[] args = {String.valueOf(customerId), String.valueOf(SendStatus.NEW.getId())};
    ContentValues contentValues = new ContentValues();
    contentValues.put(QAnswer.COL_CUSTOMER_BACKEND_ID, customerBackendId);
    contentValues.put(QAnswer.COL_STATUS, SendStatus.UPDATED.getId());
    int rows = db.update(getTableName(), contentValues, whereClause, args);
    Log.d("QAnswer", "row updated " + rows);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  @Override
  public List<AnswerDetailDto> getAllAnswerDetailByGroupId(Long answersGroupNo) {
    String selection =
        " (" + QAnswer.COL_BACKEND_ID + " is null or " + QAnswer.COL_BACKEND_ID + " = 0) AND "
            + QAnswer.COL_ANSWERS_GROUP_NO + " = ?";
    List<QAnswer> answerDetails = retrieveAll(selection,
        new String[]{String.valueOf(answersGroupNo)}, null, null, null);

    List<AnswerDetailDto> answerDetailsForSend = new ArrayList<>();
    for (int i = 0; i < answerDetails.size(); i++) {
      QAnswer answerDetail = answerDetails.get(i);
      answerDetailsForSend.add(createAnswerDetailsDtoFromEntity(answerDetail));
    }
    return answerDetailsForSend;
  }

  @Override
  public void deleteAllAnswer(Long visitId, Long answersGroupNo) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    db.execSQL(String.format("DELETE FROM %s WHERE %s = %s AND %s = %s", getTableName(),
        QAnswer.COL_VISIT_ID, visitId, QAnswer.COL_ANSWERS_GROUP_NO, answersGroupNo));
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  private AnswerDetailDto createAnswerDetailsDtoFromEntity(QAnswer answerDetail) {
    return new AnswerDetailDto(answerDetail.getQuestionBackendId(), answerDetail.getAnswer(),
        answerDetail.getGoodsBackendId(), answerDetail.getId());
  }
}
