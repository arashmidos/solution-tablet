package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.PositionDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.PositionModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.LocationUtil;
import com.parsroyal.solutiontablet.util.MediaUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
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
    public List<Customer> getAllCustomersByVisitLineBackendId(Long visitLineId)
    {
        return customerDao.retrieveAllCustomersByVisitLineBackendId(visitLineId);
    }

    @Override
    public List<CustomerListModel> getAllCustomersListModelByVisitLineBackendId(Long visitLineId)
    {
        return getFilteredCustomerList(visitLineId, null);
    }

    @Override
    public List<CustomerListModel> getFilteredCustomerList(Long visitLineId, String constraint)
    {
        List<CustomerListModel> listModel = customerDao.getAllCustomersListModelByVisitLineWithConstraint(visitLineId, constraint);
        Position position = new PositionServiceImpl(context).getLastPosition();

        //Set distances from current location
        for (int i = 0; i < listModel.size(); i++)
        {
            CustomerListModel item = listModel.get(i);

            if (Empty.isEmpty(position))
            {
                item.setDistance(0.0f);
            } else
            {
                item.setDistance(LocationUtil.distanceTo(position.getLatitude(), position.getLongitude(),
                        item.getXlocation(), item.getYlocation()));
            }
        }

        return listModel;
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
    public long savePicture(CustomerPic customerPic)
    {
        if (Empty.isEmpty(customerPic.getId()))
        {
            customerPic.setCreateDateTime(DateUtil.convertDate(new Date(),
                    DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            customerPic.setStatus(CustomerStatus.NEW.getId());
            return customerPicDao.create(customerPic);
        } else
        {
            //            customer.setUpdateDateTime(new Date().toString());
            //            customerDao.update(customer);
            //There is no need for update yet. we'll delete all pic records after uploading
            return 0;
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
