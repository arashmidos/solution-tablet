package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.CustomerPic;

import java.util.List;

/**
 * Created by Arash on 6/6/2016
 */
public interface CustomerPicDao extends BaseDao<CustomerPic, Long>
{
    List<String> getAllCustomerPicForSend();

}
