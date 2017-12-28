package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.data.model.BaseInfoList;
import com.parsroyal.solutiontablet.data.model.CityList;
import com.parsroyal.solutiontablet.data.model.ProvinceList;
import com.parsroyal.solutiontablet.data.model.QuestionnaireDtoList;
import com.parsroyal.solutiontablet.data.model.VisitLineDto;
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

  @GET("goods/{company_id}/{stock_id}/{sale_type}/{salesman_id}")
  Call<List<Goods>> getAllGoods(@Path("company_id") String companyId,
      @Path("stock_id") String stockId,
      @Path("sale_type") String SaleType, @Path("salesman_id") String salesmanId);

  @GET("visit-lines/{sale_type}/{salesman_id}")
  Call<List<VisitLineDto>> getAllVisitLines(@Path("sale_type") String SaleType,
      @Path("salesman_id") String salesmanId);

}
