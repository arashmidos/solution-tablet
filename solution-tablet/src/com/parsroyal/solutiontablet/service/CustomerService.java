package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.PositionModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import java.io.File;
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

  File getAllCustomerPicForSend();

  File getAllCustomerPicForSendByVisitId(Long visitId);
  File getAllCustomerPicForSendByCustomerId(Long customerId);

  void deleteCustomerPic(String title, long customerId);


  void deleteAllPics();

  void updateCustomerPictures();

  boolean addCustomer(Customer customer, Long visitlineBackendId);
}
