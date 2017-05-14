package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;

import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public class QAnswerDaoImpl extends AbstractDao<QAnswer, Long> implements QAnswerDao
{

    private Context context;

    public QAnswerDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(QAnswer entity)
    {
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

        return contentValues;
    }

    @Override
    protected String getTableName()
    {
        return QAnswer.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return QAnswer.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection = {
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
                QAnswer.COL_UPDATE_DATE_TIME
        };
        return projection;
    }

    @Override
    protected QAnswer createEntityFromCursor(Cursor cursor)
    {
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

        return answer;
    }

    @Override
    public List<QAnswer> getAllQAnswersForSend()
    {
        String selection =
                " " + QAnswer.COL_BACKEND_ID + " is null or " + QAnswer.COL_BACKEND_ID + " = 0";
        return retrieveAll(selection, null, null, null, null);
    }

    @Override
    public void updateCustomerBackendIdForAnswers(long customerId, long customerBackendId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();
        String whereClause = QAnswer.COL_CUSTOMER_BACKEND_ID + " = ?";
        String[] args = {String.valueOf(customerId)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(VisitInformation.COL_CUSTOMER_BACKEND_ID, customerBackendId);
        int rows = db.update(getTableName(), contentValues, whereClause, args);
        Log.d("QAnswer", "row updated " + rows);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
