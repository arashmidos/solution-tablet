package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.CustomerPic;
import com.parsroyal.storemanagement.data.searchobject.CustomerPictureSO;
import java.util.List;

/**
 * Created by Arash on 6/6/2016
 */
public interface CustomerPicDao extends BaseDao<CustomerPic, Long> {

  List<CustomerPic> getAllCustomerPicForSend();

  List<String> getAllCustomerPicForSendByVisitId(Long visitId);

  List<CustomerPic> getAllCustomerPicForSendByCustomerId(Long customerId);

  List<String> getAllCustomerPicTitleByCustomerId(Long customerId);

  List<CustomerPic> getAllCustomerPicturesByBackendId(long customerBackendId);

  List<CustomerPic> getAllCustomerPicturesById(long customerId);

  List<CustomerPic> findCustomerPictures(CustomerPictureSO customerPictureSO);

  void updateAllPictures();

  void updatePicturesByVisitId(Long visitId);

  void updateAllPicturesByCustomerId(Long customerId);
  void updateCustomerPicForNewCustomers(Long id, Long backendId);

  void delete(String title, long customerId);

}
