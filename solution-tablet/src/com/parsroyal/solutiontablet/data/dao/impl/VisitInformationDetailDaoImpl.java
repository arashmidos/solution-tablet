package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.parsroyal.solutiontablet.data.dao.VisitInformationDetailDao;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;

import java.util.List;

/**
 * Created by Arash 2017-03-08
 */
public class VisitInformationDetailDaoImpl extends AbstractDao<VisitInformationDetail, Long>
        implements VisitInformationDetailDao
{

    private Context context;

    public VisitInformationDetailDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(VisitInformationDetail entity)
    {
        ContentValues cv = new ContentValues();
        cv.put(VisitInformationDetail.COL_ID, entity.getId());
        cv.put(VisitInformationDetail.COL_TYPE, entity.getType());
        cv.put(VisitInformationDetail.COL_TYPE_ID, entity.getTypeId());
        cv.put(VisitInformationDetail.COL_EXTRA_DATA, entity.getExtraData());
        cv.put(VisitInformationDetail.COL_VISIT_INFORMATION_ID, entity.getVisitInformationId());
        cv.put(VisitInformationDetail.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
        cv.put(VisitInformationDetail.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
        return cv;
    }

    @Override
    protected String getTableName()
    {
        return VisitInformationDetail.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return VisitInformationDetail.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        return new String[]{
                VisitInformationDetail.COL_ID,
                VisitInformationDetail.COL_TYPE,
                VisitInformationDetail.COL_TYPE_ID,
                VisitInformationDetail.COL_EXTRA_DATA,
                VisitInformationDetail.COL_VISIT_INFORMATION_ID,
                VisitInformationDetail.COL_CREATE_DATE_TIME,
                VisitInformationDetail.COL_UPDATE_DATE_TIME
        };
    }

    @Override
    protected VisitInformationDetail createEntityFromCursor(Cursor cursor)
    {
        VisitInformationDetail entity = new VisitInformationDetail();
        entity.setId(cursor.getLong(0));
        entity.setType(cursor.getLong(1));
        entity.setTypeId(cursor.getLong(2));
        entity.setExtraData(cursor.getString(3));
        entity.setVisitInformationId(cursor.getLong(4));
        entity.setCreateDateTime(cursor.getString(9));
        entity.setUpdateDateTime(cursor.getString(10));
        return entity;
    }

    @Override
    public List<VisitInformationDetail> getAllVisitDetail(Long visitId)
    {
        String selection = " " + VisitInformationDetail.COL_VISIT_INFORMATION_ID + " = ?";
        String[] args = {String.valueOf(visitId)};

        return retrieveAll(selection, args, null, null, null);
    }
}
