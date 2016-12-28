package com.conta.comer.data.dao;

import com.conta.comer.data.entity.CustomerPic;

import java.util.List;

/**
 * Created by Arash on 6/6/2016
 */
public interface CustomerPicDao extends BaseDao<CustomerPic, Long>
{
    List<String> getAllCustomerPicForSend();

}
