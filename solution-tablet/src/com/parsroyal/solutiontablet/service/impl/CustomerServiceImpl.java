package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.PositionModel;
import com.parsroyal.solutiontablet.data.searchobject.CustomerPictureSO;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.LocationUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 */
public class CustomerServiceImpl implements CustomerService {

  private Context context;
  private CustomerDao customerDao;
  private CustomerPicDao customerPicDao;

  public CustomerServiceImpl(Context context) {
    this.context = context;
    this.customerDao = new CustomerDaoImpl(context);
    this.customerPicDao = new CustomerPicDaoImpl(context);
  }

  @Override
  public Customer getCustomerById(Long customerId) {
    return customerDao.retrieve(customerId);
  }

  @Override
  public Customer getCustomerByBackendId(Long customerBackendId) {
    return customerDao.retrieveByBackendId(customerBackendId);
  }

  @Override
  public Long saveCustomer(Customer customer) {
    try {
      if (Empty.isEmpty(customer.getId())) {
        customer.setCreateDateTime(
            DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        customer.setUpdateDateTime(
            DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        customer.setStatus(CustomerStatus.NEW.getId());
        return customerDao.create(customer);
      } else {
        customer.setUpdateDateTime(
            DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        customer.setStatus(CustomerStatus.UPDATED.getId());
        customerDao.update(customer);
        return customer.getId();
      }
    } catch (SQLiteException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Customer> getAllNewCustomers() {
    return customerDao.retrieveAllNewCustomers();
  }

  @Override
  public void deleteCustomer(Long id) {
    customerDao.delete(id);
  }

  @Override
  public List<CustomerDto> getAllNewCustomersForSend() {
    return customerDao.retrieveAllNewCustomersForSend();
  }

  @Override
  public List<CustomerLocationDto> getAllUpdatedCustomerLocation() {
    return customerDao.retrieveAllUpdatedCustomerLocationDto();
  }

  @Override
  public List<CustomerLocationDto> getAllCustomersLocation(long visitLineBackendId) {
    return customerDao.retrieveAllCustomersLocationDto(visitLineBackendId);
  }

  @Override
  public CustomerLocationDto findCustomerLocationDtoByCustomerBackendId(Long customerBackendId) {
    return customerDao.findCustomerLocationDtoByCustomerBackendId(customerBackendId);
  }

  @Override
  public List<Customer> getAllCustomersByVisitLineBackendId(Long visitLineId) {
    return customerDao.retrieveAllCustomersByVisitLineBackendId(visitLineId);
  }

  @Override
  public List<CustomerListModel> getAllCustomersListModelByVisitLineBackendId(Long visitLineId) {
    return getFilteredCustomerList(visitLineId, null, false);
  }

  @Override
  public List<CustomerListModel> getFilteredCustomerList(Long visitLineId, String constraint,
      boolean showOnMap) {
    List<CustomerListModel> listModel = customerDao
        .getAllCustomersListModelByVisitLineWithConstraint(visitLineId, constraint, showOnMap);

    Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();

    //Set distances from current location
    for (int i = 0; i < listModel.size(); i++) {
      CustomerListModel item = listModel.get(i);

      if (Empty.isEmpty(position)) {
        item.setDistance(0.0f);
      } else {
        item.setDistance(
            LocationUtil.distanceBetween(position.getLatitude(), position.getLongitude(),
                item.getXlocation(), item.getYlocation()));
      }
    }

    return listModel;
  }

  @Override
  public CustomerDto getCustomerDtoById(Long customerId) {
    return customerDao.getCustomerDtoById(customerId);
  }

  @Override
  public List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO) {
    return customerDao.searchForNCustomers(nCustomerSO);
  }

  @Override
  public long savePicture(CustomerPic customerPic) {
    if (Empty.isEmpty(customerPic.getId())) {
      customerPic.setCreateDateTime(DateUtil.convertDate(new Date(),
          DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      customerPic.setStatus(CustomerStatus.NEW.getId());
      return customerPicDao.create(customerPic);
    } else {
      //            customer.setUpdateDateTime(new Date().toString());
      //            customerDao.update(customer);
      //There is no need for update yet. we'll delete all pic records after uploading
      return 0;
    }
  }

  @Override
  public void savePicture(List<CustomerPic> customerPics, Long customerId) {
    customerPicDao.deleteAll(CustomerPic.COL_CUSTOMER_ID, String.valueOf(customerId));
    customerPicDao.bulkInsert(customerPics);
  }

  @Override
  public List<CustomerPic> getAllPicturesByCustomerBackendId(long customerBackendId) {
    return customerPicDao.getAllCustomerPicturesByBackendId(customerBackendId);
  }

  @Override
  public List<CustomerPic> getAllPicturesByCustomerId(long customerId) {
    return customerPicDao.getAllCustomerPicturesByBackendId(customerId);
  }

  @Override
  public List<String> getAllPicturesTitleByCustomerId(long customerId) {
    return customerPicDao.getAllCustomerPicTitleByCustomerId(customerId);
  }

  @Override
  public List<CustomerPic> getAllCustomerPicForSend() {
    return customerPicDao.getAllCustomerPicForSend();
  }

  @Override
  public List<CustomerPic> findCustomerPic(CustomerPictureSO pictureSO) {
    return customerPicDao.findCustomerPictures(pictureSO);
  }

  @Override
  public List<CustomerPic> getAllCustomerPicForSendByCustomerId(Long customerId) {
    return customerPicDao.getAllCustomerPicForSendByCustomerId(customerId);
  }

  @Override
  public void deleteCustomerPic(String title, long customerId) {
    customerPicDao.delete(title, customerId);
  }

  @Override
  public List<PositionModel> getCustomerPositions(NCustomerSO nCustomerSO) {
    return customerDao.getAllCusromerPostionModel(nCustomerSO);
  }

  @Override
  public void deleteAllPics() {
    customerPicDao.deleteAll();
  }

  @Override
  public void updateCustomerPictures() {
    customerPicDao.updateAllPictures();
  }

  @Override
  public void updateCustomerPicForNewCustomers(Long id, Long backendId) {
    customerPicDao.updateCustomerPicForNewCustomers(id, backendId);
  }

  @Override
  public void deleteAll() {
    customerDao.deleteAll();
  }

  @Override
  public boolean addCustomer(Customer customer, Long visitLineBackendId) {
    if (customerDao.retrieveCustomerByVisitLineBackendId(
        customer.getBackendId(), visitLineBackendId) != null) {
      //We already added this customer
      return false;
    } else {
      customer.setVisitLineBackendId(visitLineBackendId);
      customer.setCreateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      customer.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      customerDao.create(customer);
      return true;
    }
  }
}
