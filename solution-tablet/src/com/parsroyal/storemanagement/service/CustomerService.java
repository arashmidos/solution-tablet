package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.entity.Customer;
import com.parsroyal.storemanagement.data.entity.CustomerPic;
import com.parsroyal.storemanagement.data.listmodel.CustomerListModel;
import com.parsroyal.storemanagement.data.listmodel.NCustomerListModel;
import com.parsroyal.storemanagement.data.model.CustomerDto;
import com.parsroyal.storemanagement.data.model.CustomerLocationDto;
import com.parsroyal.storemanagement.data.model.PositionModel;
import com.parsroyal.storemanagement.data.searchobject.CustomerPictureSO;
import com.parsroyal.storemanagement.data.searchobject.NCustomerSO;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 */
public interface CustomerService extends BaseService {

  Customer getCustomerById(Long customerId);

  Customer getCustomerByBackendId(Long customerId);

  Long saveCustomer(Customer customer);

  List<Customer> getAllNewCustomers();

  void deleteCustomer(Long id);

  List<CustomerDto> getAllNewCustomersForSend();

  List<CustomerLocationDto> getAllUpdatedCustomerLocation();

  CustomerLocationDto findCustomerLocationDtoByCustomerBackendId(Long customerBackendId);

  List<Customer> getAllCustomersByVisitLineBackendId(Long visitLineId);

  List<CustomerListModel> getAllCustomersListModelByVisitLineBackendId(Long visitLineId);

  List<CustomerListModel> getFilteredCustomerList(Long visitLineId, String constraint,
      boolean showOnMap);

  CustomerDto getCustomerDtoById(Long customerId);

  List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO);

  List<PositionModel> getCustomerPositions(NCustomerSO nCustomerSO);

  long savePicture(CustomerPic customerPic);

  void savePicture(List<CustomerPic> customerPics, Long customerId);

  List<CustomerPic> getAllPicturesByCustomerBackendId(long customerBackendId);

  List<CustomerPic> getAllPicturesByCustomerId(long customerId);

  List<String> getAllPicturesTitleByCustomerId(long customerId);

  List<CustomerPic> getAllCustomerPicForSend();

  List<CustomerPic> findCustomerPic(CustomerPictureSO pictureSO);

  List<CustomerPic> getAllCustomerPicForSendByCustomerId(Long customerId);

  void deleteCustomerPic(String title, long customerId);

  void deleteAllPics();

  void updateCustomerPictures();

  void updateCustomerPicForNewCustomers(Long id, Long backendId);

  boolean addCustomer(Customer customer, Long visitlineBackendId);

}
