package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.data.model.BaseInfoList;
import com.parsroyal.solutiontablet.data.model.CityList;
import com.parsroyal.solutiontablet.data.model.ProvinceList;
import com.parsroyal.solutiontablet.data.model.QuestionnaireDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderList;
import com.parsroyal.solutiontablet.data.model.VisitLineDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Arash on 2017-02-16.
 */

public interface PostDataRestService {


  @POST("saleorders/cancel/{orderBackendId}")
  Call<String> cancelOrders(@Path("orderBackendId") Long orderBackendId);


}
