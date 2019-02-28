package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.Customer;
import com.parsroyal.storemanagement.data.listmodel.CustomerListModel;
import com.parsroyal.storemanagement.data.listmodel.NCustomerListModel;
import com.parsroyal.storemanagement.data.model.CustomerDto;
import com.parsroyal.storemanagement.data.model.CustomerLocationDto;
import com.parsroyal.storemanagement.data.model.PositionModel;
import com.parsroyal.storemanagement.data.searchobject.NCustomerSO;
import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public interface CustomerDao extends BaseDao<Customer, Long> {

  List<CustomerDto> retrieveAllNewCustomersForSend();

  List<Customer> retrieveAllNewCustomers();

  List<CustomerLocationDto> retrieveAllUpdatedCustomerLocationDto();

  CustomerLocationDto findCustomerLocationDtoByCustomerBackendId(Long customerBackendId);

  void deleteAllCustomersRelatedToVisitLines();

  List<Customer> retrieveAllCustomersByVisitLineBackendId(Long visitLineId);

  Customer retrieveCustomerByVisitLineBackendId(Long customerBackendId, Long visitLineId);

  List<CustomerListModel> getAllCustomersListModelByVisitLineWithConstraint(Long visitLineId,
      String constraint, boolean showOnMap);

  CustomerDto getCustomerDtoById(Long customerId);

  List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO);

  List<Customer> getCustomersVisitLineBackendId(Long backendId);

  Customer retrieveByBackendId(Long customerBackendId);

  List<PositionModel> getAllCusromerPostionModel(NCustomerSO nCustomerSO);
}
