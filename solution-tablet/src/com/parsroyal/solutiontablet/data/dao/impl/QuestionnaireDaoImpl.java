package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
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
        QuestionnaireDao
{

    private Context context;

    public QuestionnaireDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(Questionnaire entity)
    {
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
    protected String getTableName()
    {
        return Questionnaire.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return Questionnaire.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
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
    protected Questionnaire createEntityFromCursor(Cursor cursor)
    {
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
    public List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo)
    {
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
        if (Empty.isNotEmpty(constraint))
        {
            selection = selection.concat(" AND (" + Questionnaire.COL_DESCRIPTION + " LIKE ?)");
            argList.add(constraint);
        }

        if (questionnaireSo.isGeneral())
        {
            selection = selection.concat(" AND (" + Questionnaire.COL_GOODS_GROUP_BACKEND_ID + " = 0 )");
        } else
        {
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
        while (cursor.moveToNext())
        {
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
}
