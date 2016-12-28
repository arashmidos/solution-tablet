package com.conta.comer.service.impl;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.location.Location;

import com.conta.comer.constants.CustomerStatus;
import com.conta.comer.data.dao.CustomerDao;
import com.conta.comer.data.dao.CustomerPicDao;
import com.conta.comer.data.dao.VisitInformationDao;
import com.conta.comer.data.dao.VisitLineDao;
import com.conta.comer.data.dao.impl.CustomerDaoImpl;
import com.conta.comer.data.dao.impl.CustomerPicDaoImpl;
import com.conta.comer.data.dao.impl.VisitInformationDaoImpl;
import com.conta.comer.data.dao.impl.VisitLineDaoImpl;
import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.entity.CustomerPic;
import com.conta.comer.data.entity.VisitInformation;
import com.conta.comer.data.entity.VisitLine;
import com.conta.comer.data.listmodel.CustomerListModel;
import com.conta.comer.data.listmodel.NCustomerListModel;
import com.conta.comer.data.listmodel.VisitLineListModel;
import com.conta.comer.data.model.CustomerDto;
import com.conta.comer.data.model.CustomerLocationDto;
import com.conta.comer.data.model.PositionModel;
import com.conta.comer.data.searchobject.NCustomerSO;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.LocationService;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;
import com.conta.comer.util.MediaUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 * Edited by Arash on 6/29/2016
 */
public class CustomerServiceImpl implements CustomerService
{

    private Context context;
    private CustomerDao customerDao;
    private CustomerPicDao customerPicDao;
    private VisitLineDao visitLineDao;
    private LocationService locationService;
    private VisitInformationDao visitInformationDao;

    public CustomerServiceImpl(Context context)
    {
        this.context = context;
        this.customerDao = new CustomerDaoImpl(context);
        this.customerPicDao = new CustomerPicDaoImpl(context);
        this.visitLineDao = new VisitLineDaoImpl(context);
        this.visitInformationDao = new VisitInformationDaoImpl(context);
        this.locationService = new LocationServiceImpl(context);
    }

    @Override
    public Customer getCustomerById(Long customerId)
    {
        return customerDao.retrieve(customerId);
    }

    @Override
    public Customer getCustomerByBackendId(Long customerBackendId)
    {
        return customerDao.retrieveByBackendId(customerBackendId);
    }

    @Override
    public void saveCustomer(Customer customer)
    {
        try
        {
            if (Empty.isEmpty(customer.getId()))
            {
                customer.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                customer.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                customer.setStatus(CustomerStatus.NEW.getId());
                customerDao.create(customer);
            } else
            {
                customer.setUpdateDateTime(new Date().toString());
                customerDao.update(customer);
            }
        } catch (SQLiteException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Customer> getAllNewCustomers()
    {
        return customerDao.retrieveAllNewCustomers();
    }

    @Override
    public void deleteCustomer(Long id)
    {
        customerDao.delete(id);
    }

    @Override
    public List<Customer> getAllNewCustomersForSend()
    {
        return customerDao.retrieveAllNewCustomersForSend();
    }

    @Override
    public List<CustomerLocationDto> getAllUpdatedCustomerLocation()
    {
        return customerDao.retrieveAllUpdatedCustomerLocationDto();
    }

    @Override
    public List<VisitLine> getAllVisitLines()
    {
        return visitLineDao.retrieveAll();
    }

    @Override
    public List<Customer> getAllCustomersByVisitLineBackendId(Long visitLineId)
    {
        return customerDao.retrieveAllCustomersByVisitLineBackendId(visitLineId);
    }

    @Override
    public List<VisitLineListModel> getAllVisitLinesListModel()
    {
        return visitLineDao.getAllVisitLineListModel();
    }

    @Override
    public List<CustomerListModel> getAllCustomersListModelByVisitLineBackendId(Long visitLineId)
    {
        return customerDao.getAllCustomersListModelByVisitLineBackendId(visitLineId);
    }

    @Override
    public List<CustomerListModel> getFilteredCustomerList(Long visitLineId, String constraint)
    {
        return customerDao.getAllCustomersListModelByVisitLineWithConstraint(visitLineId, constraint);
    }

    @Override
    public List<VisitLineListModel> getAllFilteredVisitLinesListModel(String constraint)
    {
        return visitLineDao.getAllVisitLinesListModelByConstraint(constraint);
    }

    @Override
    public CustomerDto getCustomerDtoById(Long customerId)
    {
        return customerDao.getCustomerDtoById(customerId);
    }

    @Override
    public List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO)
    {
        return customerDao.searchForNCustomers(nCustomerSO);
    }

    @Override
    public Long startVisiting(Long customerBackendId)
    {
        VisitInformation visitInformation = new VisitInformation();
        visitInformation.setCustomerBackendId(customerBackendId);
        visitInformation.setVisitDate(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        visitInformation.setStartTime(DateUtil.convertDate(new Date(), DateUtil.TIME_24, "EN"));
        visitInformation.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        Long visitId = saveVisit(visitInformation);
        return visitId;
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
        Long visitId = saveVisit(visitInformation);
        return visitId;
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
    public List<VisitInformation> getVisitInformationForNewCustomer(Long customerId)
    {
        return visitInformationDao.retrieveForNewCustomer(customerId);
    }

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
    public void savePicture(CustomerPic customerPic)
    {
        if (Empty.isEmpty(customerPic.getId()))
        {
            customerPic.setCreateDateTime(DateUtil.convertDate(new Date(),
                    DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            customerPic.setStatus(CustomerStatus.NEW.getId());
            customerPicDao.create(customerPic);
        } else
        {
            //            customer.setUpdateDateTime(new Date().toString());
            //            customerDao.update(customer);
            //There is no need for update yet. we'll delete all pic records after uploading
        }
    }

    @Override
    public File getAllCustomerPicForSend()
    {
        List<String> pics = customerPicDao.getAllCustomerPicForSend();
        if (pics.size() == 0)
        {
            return null;
        }
        String[] picArray = new String[pics.size()];
        picArray = pics.toArray(picArray);
        File zip = MediaUtil.zipFiles(picArray);
        return zip;
    }

    @Override
    public List<PositionModel> getCustomerPositions(NCustomerSO nCustomerSO)
    {
        return customerDao.getAllCusromerPostionModel(nCustomerSO);
    }
}
