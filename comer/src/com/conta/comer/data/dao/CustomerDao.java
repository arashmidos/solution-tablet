package com.conta.comer.data.dao;

import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.listmodel.CustomerListModel;
import com.conta.comer.data.listmodel.NCustomerListModel;
import com.conta.comer.data.model.CustomerDto;
import com.conta.comer.data.model.CustomerLocationDto;
import com.conta.comer.data.model.PositionModel;
import com.conta.comer.data.searchobject.NCustomerSO;

import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public interface CustomerDao extends BaseDao<Customer, Long>
{
    List<Customer> retrieveAllNewCustomersForSend();

    List<Customer> retrieveAllNewCustomers();

    List<CustomerLocationDto> retrieveAllUpdatedCustomerLocationDto();

    void deleteAllCustomersRelatedToVisitLines();

    List<Customer> retrieveAllCustomersByVisitLineBackendId(Long visitLineId);

    List<CustomerListModel> getAllCustomersListModelByVisitLineBackendId(Long visitLineId);

    List<CustomerListModel> getAllCustomersListModelByVisitLineWithConstraint(Long visitLineId, String constraint);

    CustomerDto getCustomerDtoById(Long customerId);

    List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO);

    List<Customer> getCustomersVisitLineBackendId(Long backendId);

    Customer retrieveByBackendId(Long customerBackendId);

    void updateAllSentCustomer();

    List<PositionModel> getAllCusromerPostionModel(NCustomerSO nCustomerSO);
}
