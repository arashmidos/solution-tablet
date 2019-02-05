package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.PositionModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public interface CustomerDao extends BaseDao<Customer, Long> {

  List<CustomerDto> retrieveAllNewCustomersForSend();

  List<CustomerDto> retrieveAllNewUpdatedCustomersForSend();

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

  void updateAllSentCustomer();

  List<PositionModel> getAllCusromerPostionModel(NCustomerSO nCustomerSO);
}
