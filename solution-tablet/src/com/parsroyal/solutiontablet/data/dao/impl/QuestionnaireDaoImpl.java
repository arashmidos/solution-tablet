package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.entity.Questionnaire;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireDaoImpl extends AbstractDao<Questionnaire, Long> implements
    QuestionnaireDao {

  private Context context;

  public QuestionnaireDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(Questionnaire entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(Questionnaire.COL_ID, entity.getId());
    contentValues.put(Questionnaire.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(Questionnaire.COL_FROM_DATE, entity.getFromDate());
    contentValues.put(Questionnaire.COL_TO_DATE, entity.getToDate());
    contentValues.put(Questionnaire.COL_GOODS_GROUP_BACKEND_ID, entity.getGoodsGroupBackendId());
    contentValues
        .put(Questionnaire.COL_CUSTOMER_GROUP_BACKEND_ID, entity.getCustomerGroupBackendId());
    contentValues.put(Questionnaire.COL_DESCRIPTION, entity.getDescription());
    contentValues.put(Questionnaire.COL_STATUS, entity.getStatus());
    contentValues.put(Questionnaire.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(Questionnaire.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return Questionnaire.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return Questionnaire.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    String[] projection = {
        Questionnaire.COL_ID,
        Questionnaire.COL_BACKEND_ID,
        Questionnaire.COL_FROM_DATE,
        Questionnaire.COL_TO_DATE,
        Questionnaire.COL_GOODS_GROUP_BACKEND_ID,
        Questionnaire.COL_CUSTOMER_GROUP_BACKEND_ID,
        Questionnaire.COL_DESCRIPTION,
        Questionnaire.COL_STATUS,
        Questionnaire.COL_CREATE_DATE_TIME,
        Questionnaire.COL_UPDATE_DATE_TIME,
    };
    return projection;
  }

  @Override
  protected Questionnaire createEntityFromCursor(Cursor cursor) {
    Questionnaire questionnaire = new Questionnaire();
    questionnaire.setId(cursor.getLong(0));
    questionnaire.setBackendId(cursor.getLong(1));
    questionnaire.setFromDate(cursor.getString(2));
    questionnaire.setToDate(cursor.getString(3));
    questionnaire.setGoodsGroupBackendId(cursor.getLong(4));
    questionnaire.setCustomerGroupBackendId(cursor.getLong(5));
    questionnaire.setDescription(cursor.getString(6));
    questionnaire.setStatus(cursor.getLong(7));
    questionnaire.setCreateDateTime(cursor.getString(8));
    questionnaire.setUpdateDateTime(cursor.getString(9));
    return questionnaire;
  }

  @Override
  public List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String[] projection = {
        " qn." + Questionnaire.COL_ID,
        " qn." + Questionnaire.COL_BACKEND_ID,
        " qn." + Questionnaire.COL_DESCRIPTION,
        " qn." + Questionnaire.COL_GOODS_GROUP_BACKEND_ID,
        " COUNT(qs._id) "
    };
    String selection = " 1 = 1";
    List<String> argList = new ArrayList<>();

    String constraint = questionnaireSo.getConstraint();
    if (Empty.isNotEmpty(constraint)) {
      selection = selection.concat(" AND (" + Questionnaire.COL_DESCRIPTION + " LIKE ?)");
      argList.add(constraint);
    }

    if (questionnaireSo.isGeneral()) {
      selection = selection.concat(" AND (" + Questionnaire.COL_GOODS_GROUP_BACKEND_ID + " = 0 )");
    } else {
      selection = selection.concat(" AND (" + Questionnaire.COL_GOODS_GROUP_BACKEND_ID + " <> 0 )");
    }

    String orderBy = " qn." + Questionnaire.COL_CREATE_DATE_TIME + " DESC";
    String[] args = new String[argList.size()];
    String table = "COMMER_QUESTIONNAIRE qn " +
        "LEFT OUTER JOIN COMMER_QUESTION qs on qs.QUESTIONNAIRE_BACKEND_ID = qn.BACKEND_ID";
    String groupBy = "qn._id, qn.BACKEND_ID, qn.DESCRIPTION ";
    Cursor cursor = db
        .query(table, projection, selection, argList.toArray(args), groupBy, null, orderBy);
    List<QuestionnaireListModel> questionnaires = new ArrayList<>();
    while (cursor.moveToNext()) {
      QuestionnaireListModel questionnaireListModel = new QuestionnaireListModel();
      questionnaireListModel.setId(cursor.getLong(0));
      questionnaireListModel.setPrimaryKey(cursor.getLong(0));
      questionnaireListModel.setBackendId(cursor.getLong(1));
      questionnaireListModel.setDescription(cursor.getString(2));
      questionnaireListModel.setGoodsGroupBackendId(cursor.getLong(3));
      questionnaireListModel.setQuestionsCount(cursor.getInt(4));
      questionnaires.add(questionnaireListModel);
    }

    cursor.close();
    return questionnaires;
  }

  @Override
  public List<QuestionnaireListModel> searchForQuestionsList(QuestionnaireSo questionnaireSo) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql = "SELECT" +
        " q.QUESTIONNAIRE_BACKEND_ID," +
        " qn.DESCRIPTION," +
        " a.VISIT_ID," +
        " a.DATE," +
        " a.ANSWERS_GROUP_NO," +//4
        " a.STATUS," +
        " cu." + Customer.COL_FULL_NAME +//6
        " FROM COMMER_Q_ANSWER a " +
        " LEFT OUTER JOIN COMMER_QUESTION q ON a.QUESTION_BACKEND_ID = q.BACKEND_ID  " +
        " LEFT OUTER JOIN COMMER_VISIT_INFORMATION v on v._id = a.VISIT_ID " +
        " LEFT OUTER JOIN COMMER_QUESTIONNAIRE qn on qn.BACKEND_ID = q.QUESTIONNAIRE_BACKEND_ID " +
        " LEFT OUTER JOIN COMMER_CUSTOMER cu on a.CUSTOMER_BACKEND_ID = cu.BACKEND_ID " +
        " WHERE 1=1 ";

    String orderBy = " ORDER BY q.qOrder";
    String groupBy = " GROUP BY a.ANSWERS_GROUP_NO";

    if (questionnaireSo.isAnonymous()) {
      sql = sql.concat(" AND v.RESULT = -2");
    } else if (Empty.isNotEmpty(questionnaireSo.getCustomerBackendId())) {
      if (questionnaireSo.getCustomerBackendId() == -1L) {
        //Exclude anonymous questionnary and new customer questionnary
        sql = sql.concat(" AND v.RESULT IS NULL");
      } else {
        sql = sql.concat(" AND v.CUSTOMER_BACKEND_ID = " + questionnaireSo.getCustomerBackendId());
      }
    } else {
      if (questionnaireSo.isGeneral()) {
        sql = sql.concat(" AND " + Questionnaire.COL_GOODS_GROUP_BACKEND_ID + " = 0 ");
      } else {
        sql = sql.concat(" AND " + Questionnaire.COL_GOODS_GROUP_BACKEND_ID + " <> 0 ");
      }
      sql = sql.concat(" AND a.VISIT_ID = " + questionnaireSo.getVisitId());
    }

    sql = sql.concat(groupBy).concat(orderBy);

    Cursor cursor = db.rawQuery(sql, null);

    List<QuestionnaireListModel> questions = new ArrayList<>();
    while (cursor.moveToNext()) {
      QuestionnaireListModel listModel = new QuestionnaireListModel();
      listModel.setPrimaryKey(cursor.getLong(0));
      listModel.setDescription(cursor.getString(1));
      listModel.setVisitId(cursor.getLong(2));
      listModel.setDate(cursor.getString(3));
      listModel.setAnswersGroupNo(cursor.getLong(4));
      listModel.setStatus(cursor.getLong(5));
      listModel.setCustomerFullName(cursor.getString(6));
      questions.add(listModel);
    }

    cursor.close();
    return questions;
  }

  @Override
  public Long getNextAnswerGroupNo() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql = "SELECT max(" + QAnswer.COL_ANSWERS_GROUP_NO + ") FROM " + QAnswer.TABLE_NAME;
    Cursor cursor = db.rawQuery(sql, null);
    Long maxId = 0L;
    if (cursor.moveToNext()) {
      maxId = cursor.getLong(0) + 1;
    }
    cursor.close();
    return maxId;
  }
}
