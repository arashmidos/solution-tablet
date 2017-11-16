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

  void saveCustomer(Customer customer);

  List<Customer> getAllNewCustomers();

  void deleteCustomer(Long id);

  List<Customer> getAllNewCustomersForSend();

  List<CustomerLocationDto> getAllUpdatedCustomerLocation();

  List<Customer> getAllCustomersByVisitLineBackendId(Long visitLineId);

  List<CustomerListModel> getAllCustomersListModelByVisitLineBackendId(Long visitLineId);

  List<CustomerListModel> getFilteredCustomerList(Long visitLineId, String constraint);

  CustomerDto getCustomerDtoById(Long customerId);

  List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO);

  long savePicture(CustomerPic customerPic);

  List<CustomerPic> getAllPicturesByCustomerBackendId(long customerBackendId);

  File getAllCustomerPicForSend();

  File getAllCustomerPicForSendByVisitId(Long visitId);

  List<PositionModel> getCustomerPositions(NCustomerSO nCustomerSO);

  void deleteAllPics();

  void updateCustomerPictures();
}
