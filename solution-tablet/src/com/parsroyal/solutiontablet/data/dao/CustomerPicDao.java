package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.searchobject.CustomerPictureSO;
import java.util.List;

/**
 * Created by Arash on 6/6/2016
 */
public interface CustomerPicDao extends BaseDao<CustomerPic, Long> {

  List<String> getAllCustomerPicForSend();

  List<String> getAllCustomerPicForSendByVisitId(Long visitId);

  List<String> getAllCustomerPicForSendByCustomerId(Long customerId);

  List<String> getAllCustomerPicTitleByCustomerId(Long customerId);

  List<CustomerPic> getAllCustomerPicturesByBackendId(long customerBackendId);

  List<CustomerPic> getAllCustomerPicturesById(long customerId);

  List<CustomerPic> findCustomerPictures(CustomerPictureSO customerPictureSO);

  void updateAllPictures();

  void updatePicturesByVisitId(Long visitId);

  void updateAllPicturesByCustomerId(Long customerId);

  void delete(String title, long customerId);

}
