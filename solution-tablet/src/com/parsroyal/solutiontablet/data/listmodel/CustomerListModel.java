package com.parsroyal.solutiontablet.data.listmodel;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import java.util.HashSet;

/**
 * Created by Arash on 31/12/2017.
 */
public class CustomerListModel extends BaseListModel implements ClusterItem {

  protected String address;
  protected String phoneNumber;
  protected String cellPhone;
  protected boolean hasLocation;
  protected boolean isVisited;
  protected double xlocation;
  protected double ylocation;
  protected boolean hasOrder;
  protected boolean hasRejection;
  protected boolean hasAnswers;
  protected Long codeNumber;
  protected Float distance;
  protected Long backendId;
  protected String lastVisit;
  protected String shopName;
  protected boolean addLocation;
  protected boolean hasPayment;
  protected boolean hasPicture;
  protected HashSet<VisitInformationDetailType> details = new HashSet<>();
  private boolean hasRejectOrder;
  private boolean hasDelivery;
  private Long visitlineBackendId;
  private boolean hasNoOrder;
  private boolean phoneVisit;
  private boolean hasFreeDelivery;

  public Long getVisitlineBackendId() {
    return visitlineBackendId;
  }

  public void setVisitlineBackendId(Long visitlineBackendId) {
    this.visitlineBackendId = visitlineBackendId;
  }

  public CustomerListModel withBackendId(Long backendId) {
    this.backendId = backendId;
    return this;
  }

  public boolean isHasDelivery() {
    return hasDelivery;
  }

  public void setHasDelivery(boolean hasDelivery) {
    this.hasDelivery = hasDelivery;
  }

  public boolean isHasRejectOrder() {
    return hasRejectOrder;
  }

  public void addDetail(VisitInformationDetailType detailType) {
    details.add(detailType);
  }

  public HashSet<VisitInformationDetailType> getDetails() {
    return details;
  }

  public boolean isAddLocation() {
    return addLocation;
  }

  public void setAddLocation(boolean addLocation) {
    this.addLocation = addLocation;
  }

  public boolean isHasPayment() {
    return hasPayment;
  }

  public void setHasPayment(boolean hasPayment) {
    this.hasPayment = hasPayment;
  }

  public boolean isHasPicture() {
    return hasPicture;
  }

  public void setHasPicture(boolean hasPicture) {
    this.hasPicture = hasPicture;
  }

  public boolean hasAnswers() {
    return hasAnswers;
  }

  public void setHasAnswers(boolean hasAnswers) {
    this.hasAnswers = hasAnswers;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public String getLastVisit() {
    return lastVisit;
  }

  public void setLastVisit(String lastVisit) {
    this.lastVisit = lastVisit;
  }

  public boolean hasOrder() {
    return hasOrder;
  }

  public void setHasOrder(boolean hasOrder) {
    this.hasOrder = hasOrder;
  }

  public boolean hasRejection() {
    return hasRejection;
  }

  public void setHasRejection(boolean hasRejection) {
    this.hasRejection = hasRejection;
  }

  public double getXlocation() {
    return xlocation;
  }

  public void setXlocation(double xlocation) {
    this.xlocation = xlocation;
  }

  public double getYlocation() {
    return ylocation;
  }

  public void setYlocation(double ylocation) {
    this.ylocation = ylocation;
  }

  public boolean hasLocation() {
    return hasLocation;
  }

  public void setHasLocation(boolean hasLocation) {
    this.hasLocation = hasLocation;
  }

  public boolean isVisited() {
    return isVisited;
  }

  public void setVisited(boolean visited) {
    isVisited = visited;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getCellPhone() {
    return cellPhone;
  }

  public void setCellPhone(String cellPhone) {
    this.cellPhone = cellPhone;
  }

  public Float getDistance() {
    return distance;
  }

  public void setDistance(Float distance) {
    this.distance = distance;
  }

  public Long getCodeNumber() {
    return codeNumber;
  }

  public void setCodeNumber(Long codeNumber) {
    this.codeNumber = codeNumber;
  }

  @Override
  public LatLng getPosition() {
    return new LatLng(xlocation, ylocation);
  }

  @Override
  public String getSnippet() {
    return address;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }


  public void sethasRejectOrder(boolean hasRejectOrder) {
    this.hasRejectOrder = hasRejectOrder;
  }

  public void setHasNoOrder(boolean hasNoOrder) {
    this.hasNoOrder = hasNoOrder;
  }

  public boolean hasNoOrder() {
    return hasNoOrder;
  }

  public boolean isPhoneVisit() {
    return phoneVisit;
  }

  public void setPhoneVisit(boolean phoneVisit) {
    this.phoneVisit = phoneVisit;
  }

  public boolean hasFreeDelivery() {
    return hasFreeDelivery;
  }

  public void setHasFreeDelivery(boolean hasFreeDelivery) {
    this.hasFreeDelivery = hasFreeDelivery;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CustomerListModel that = (CustomerListModel) o;

    return backendId != null ? backendId.equals(that.backendId) : that.backendId == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (backendId != null ? backendId.hashCode() : 0);
    return result;
  }
}
