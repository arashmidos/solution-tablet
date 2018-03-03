package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.KPIDetail;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Arash on 2016-09-21.
 */
public interface KPIService {

  @GET("kpi/activityReport/0/0")
  Call<List<KPIDetail>> getCustomerReportsList();

  @GET("kpi/activityReport/100/0")
  Call<List<KPIDetail>> getSalesmanReportsList();

  @GET("kpi/activityReport/{report_id}/{serial_id}")
  Call<List<KPIDetail>> getReport(@Path("report_id") int reportId,
      @Path("serial_id") long serialId);
}
