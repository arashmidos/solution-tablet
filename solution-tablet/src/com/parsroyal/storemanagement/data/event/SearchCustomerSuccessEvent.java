package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.entity.Customer;
import java.util.ArrayList;
import java.util.List;

public class SearchCustomerSuccessEvent extends DataTransferEvent {

  private List<Customer> customers = new ArrayList<>();

  public SearchCustomerSuccessEvent(String message, StatusCodes statusCode) {
    super(message, statusCode);
  }

  public SearchCustomerSuccessEvent(String message, StatusCodes statusCode,
      List<Customer> customers) {
    super(message, statusCode);
    this.customers = customers;
  }

  public SearchCustomerSuccessEvent(StatusCodes statusCode) {
    super(statusCode);
  }

  public List<Customer> getCustomers() {
    return customers;
  }

  public void setCustomers(List<Customer> customers) {
    this.customers = customers;
  }
}
