package com.conta.comer.service.impl;

import android.content.Context;

import com.conta.comer.biz.impl.PositionDataTransferBizImpl;
import com.conta.comer.data.dao.PositionDao;
import com.conta.comer.data.dao.impl.PositionDaoImpl;
import com.conta.comer.data.entity.Position;
import com.conta.comer.service.PositionService;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Edited by Arash on 8/19/2016
 */
public class PositionServiceImpl implements PositionService
{

    private Context context;
    private PositionDao positionDao;

    public PositionServiceImpl(Context context)
    {
        this.context = context;
        this.positionDao = new PositionDaoImpl(context);
    }

    @Override
    public Position getPositionById(Long positionId)
    {
        return positionDao.retrieve(positionId);
    }

    @Override
    public Long savePosition(Position position)
    {
        if (Empty.isEmpty(position.getId()))
        {
            position.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            position.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            return positionDao.create(position);
        } else
        {
            position.setUpdateDateTime(new Date().toString());
            positionDao.update(position);
            return position.getId();
        }
    }

    @Override
    public void updatePosition(Position position)
    {
        positionDao.update(position);
    }

    @Override
    public List<Position> getAllPositionByStatus(Long status)
    {
        return positionDao.findPositionByStatusId(status);
    }

    @Override
    public List<Position> getAllPositionByDate(Date from, Date to)
    {
        return positionDao.findPositionByDate(from, to);
    }

    @Override
    public List<LatLng> getAllPositionLatLngByDate(Date from, Date to)
    {
        return positionDao.findPositionLatLngByDate(from, to);
    }

    @Override
    public void sendPosition(Position position)
    {
        PositionDataTransferBizImpl positionDataTransferBiz = new PositionDataTransferBizImpl(context, null);
        positionDataTransferBiz.setPositions(Collections.singletonList(position));
        positionDataTransferBiz.sendAllData();
    }

    @Override
    public List<LatLng> getAllPositionLatLng()
    {
        return positionDao.findPositionLatLng();
    }
}
