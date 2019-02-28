package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.entity.Goods;
import com.parsroyal.storemanagement.data.entity.GoodsGroup;
import com.parsroyal.storemanagement.data.model.BaseInfoList;
import com.parsroyal.storemanagement.data.model.CityList;
import com.parsroyal.storemanagement.data.model.ProvinceList;
import com.parsroyal.storemanagement.data.model.QuestionnaireDtoList;
import com.parsroyal.storemanagement.data.model.SaleOrderList;
import com.parsroyal.storemanagement.data.model.VisitLineDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Arash on 2017-02-16.
 */

public interface GetDataRestService {

  @GET("baseinfo/provinces")
  Call<ProvinceList> getAllProvinces();

  @GET("baseinfo/cities")
  Call<CityList> getAllCities();

  @GET("baseinfo/all")
  Call<BaseInfoList> getAllBaseInfo();

  @GET("goods/groups/{salesman_id}")
  Call<List<GoodsGroup>> getAllGoodGroups(@Path("salesman_id") String salesmanId);

  @GET("questionnaire/{today}")
  Call<QuestionnaireDtoList> getAllQuestionnaire(@Path("today") String today);

  @GET("saleorders/{saleType}/{orderId}/giftResult")
  Call<List<String>> getGiftResult(@Path("saleType") String saleType,
      @Path("orderId") String orderId);

  @GET("goods/{company_id}/{stock_id}/{sale_type}/{salesman_id}")
  Call<List<Goods>> getAllGoods(@Path("company_id") String companyId,
      @Path("stock_id") String stockId,
      @Path("sale_type") String SaleType, @Path("salesman_id") String salesmanId);

  @GET("visit-lines/{sale_type}/{salesman_id}/{checkCredit}")
  Call<List<VisitLineDto>> getAllVisitLines(@Path("sale_type") String SaleType,
      @Path("salesman_id") String salesmanId,@Path("checkCredit") String checkCredit);

  @GET("visit-lines/delivery/{distributor_id}")
  Call<List<VisitLineDto>> getAllVisitLinesForDelivery(
      @Path("distributor_id") String distributorId);

  @GET("saleorders/{distributor_id}/deliverable")
  Call<SaleOrderList> getAllSaleOrderForDelivery(@Path("distributor_id") String distributorId);

  @GET("users/goodsrequest/{usercode}")
  Call<String> getGoodsRequest(@Path("usercode") String userCode);
}
