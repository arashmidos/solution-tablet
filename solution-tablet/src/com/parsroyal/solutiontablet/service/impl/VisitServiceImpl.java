package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.location.Location;

import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDetailDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDetailDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.PositionModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MediaUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 2017-03-08
 */
public class VisitServiceImpl implements VisitService
{
    private Context context;
    private CustomerDao customerDao;
    private CustomerPicDao customerPicDao;
    private VisitLineDao visitLineDao;
    private LocationService locationService;
    private VisitInformationDao visitInformationDao;
    private VisitInformationDetailDao visitInformationDetailDao;

    public VisitServiceImpl(Context context)
    {
        this.context = context;
        this.customerDao = new CustomerDaoImpl(context);
        this.customerPicDao = new CustomerPicDaoImpl(context);
        this.visitLineDao = new VisitLineDaoImpl(context);
        this.visitInformationDao = new VisitInformationDaoImpl(context);
        this.visitInformationDetailDao = new VisitInformationDetailDaoImpl(context);
        this.locationService = new LocationServiceImpl(context);
    }

    @Override
    public List<VisitLine> getAllVisitLines()
    {
        return visitLineDao.retrieveAll();
    }

    @Override
    public List<VisitLineListModel> getAllVisitLinesListModel()
    {
        return visitLineDao.getAllVisitLineListModel();
    }

    @Override
    public List<VisitLineListModel> getAllFilteredVisitLinesListModel(String constraint)
    {
        return visitLineDao.getAllVisitLinesListModelByConstraint(constraint);
    }

    @Override
    public Long startVisiting(Long customerBackendId)
    {
        VisitInformation visitInformation = new VisitInformation();
        visitInformation.setCustomerBackendId(customerBackendId);
        visitInformation.setVisitDate(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        visitInformation.setStartTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));
        visitInformation.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        return saveVisit(visitInformation);
    }

    @Override
    public Long startVisitingNewCustomer(Long customerId)
    {
        VisitInformation visitInformation = new VisitInformation();
        visitInformation.setCustomerId(customerId);
        visitInformation.setVisitDate(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        visitInformation.setStartTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));
        visitInformation.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        // -1 Means new customer without backendId
        visitInformation.setResult(-1L);
        return saveVisit(visitInformation);
    }

    @Override
    public void finishVisiting(Long visitId)
    {
        VisitInformation visitInformation = visitInformationDao.retrieve(visitId);
        visitInformation.setEndTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));
        locationService.stopFindingLocation();
        saveVisit(visitInformation);
    }

    @Override
    public List<VisitInformation> getAllVisitInformationForSend()
    {
        return visitInformationDao.getAllVisitInformationForSend();
    }

    @Override
    public VisitInformation getVisitInformationById(Long visitId)
    {
        return visitInformationDao.retrieve(visitId);
    }

    @Override
    public VisitInformation getVisitInformationForNewCustomer(Long customerId)
    {
        return  visitInformationDao.retrieveForNewCustomer(customerId);
    }

    @Override
    public Long saveVisit(VisitInformation visitInformation)
    {
        if (Empty.isEmpty(visitInformation.getId()))
        {
            visitInformation.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            visitInformation.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            return visitInformationDao.create(visitInformation);
        } else
        {
            visitInformation.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            visitInformationDao.update(visitInformation);
            return visitInformation.getId();
        }
    }

    @Override
    public void updateVisitLocation(Long visitInformationId, Location location)
    {
        visitInformationDao.updateLocation(visitInformationId, location);
    }

    @Override
    public void saveVisitDetail(VisitInformationDetail visitDetail)
    {
        if (Empty.isEmpty(visitDetail.getId()))
        {
            visitDetail.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            visitDetail.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            visitInformationDetailDao.create(visitDetail);
        } else
        {
            visitDetail.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            visitInformationDetailDao.update(visitDetail);
        }
    }

    @Override
    public List<VisitInformationDetail> getAllVisitDetailById(Long visitId)
    {
        return visitInformationDetailDao.getAllVisitDetail(visitId);
    }
}
