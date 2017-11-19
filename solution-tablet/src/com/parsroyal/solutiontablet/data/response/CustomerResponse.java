package com.parsroyal.solutiontablet.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import java.io.Serializable;

/**
 * Created by arash on 11/19/17.
 */

public class CustomerResponse implements Serializable {

  @JsonProperty("Customer")
  private CustomerDto Customer;


  @JsonProperty("Customer")
  public CustomerDto getCustomer() {
    return Customer;
  }


  @JsonProperty("Customer")
  public void setCustomer(CustomerDto customer) {
    this.Customer = customer;
  }
}
