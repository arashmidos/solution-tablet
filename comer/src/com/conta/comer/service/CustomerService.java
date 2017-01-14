package com.conta.comer.service;

import android.location.Location;

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

import java.io.File;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 * Edited by Arash on 6/29/2016
 */
public interface CustomerService
{
    Customer getCustomerById(Long customerId);

    Customer getCustomerByBackendId(Long customerId);

    void saveCustomer(Customer customer);

    List<Customer> getAllNewCustomers();

    void deleteCustomer(Long id);

    List<Customer> getAllNewCustomersForSend();

    List<CustomerLocationDto> getAllUpdatedCustomerLocation();

    List<VisitLine> getAllVisitLines();

    List<Customer> getAllCustomersByVisitLineBackendId(Long visitLineId);

    List<VisitLineListModel> getAllVisitLinesListModel();

    List<CustomerListModel> getAllCustomersListModelByVisitLineBackendId(Long visitLineId);

    List<CustomerListModel> getFilteredCustomerList(Long visitLineId, String constraint);

    List<VisitLineListModel> getAllFilteredVisitLinesListModel(String constraint);

    CustomerDto getCustomerDtoById(Long customerId);

    List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO);

    Long startVisiting(Long customerBackendId);

    Long startVisitingNewCustomer(Long customerId);

    void finishVisiting(Long visitId);

    List<VisitInformation> getAllVisitInformationForSend();

    VisitInformation getVisitInformationById(Long visitId);

    VisitInformation getVisitInformationForNewCustomer(Long customerId);

    Long saveVisit(VisitInformation visit);

    void updateVisitLocation(Long visitInformationId, Location location);

    void savePicture(CustomerPic customerPic);

    File getAllCustomerPicForSend();

    List<PositionModel> getCustomerPositions(NCustomerSO nCustomerSO);
}
