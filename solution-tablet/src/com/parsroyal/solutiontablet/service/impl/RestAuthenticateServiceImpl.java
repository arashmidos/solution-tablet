package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleType;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.SettingDto;
import com.parsroyal.solutiontablet.data.response.CompanyInfoResponse;
import com.parsroyal.solutiontablet.data.response.SettingResponse;
import com.parsroyal.solutiontablet.service.RestSettingService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Arash on 2017-08-30
 */
public class RestAuthenticateServiceImpl {

  private static final String TAG = RestAuthenticateServiceImpl.class.getName();
  private static final String API_AUTHENTICATE_URI = "%s/users?action=authenticate";
  private static final String API_SETTING_URI = "http://173.212.199.107:50004/appcenter/app/%s/%s";
  private static final String API_SETTING_URI2 = "http://79.175.163.195:50004/appcenter/app/%s/%s";
  private static int retry = 0;

  public static void getCompanyInfo(final Context context, String companyKey) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      return;
    }
    if (retry > 1) {
      return;
    }

    RestSettingService restSettingService = null;
    try {
      restSettingService = ServiceGenerator.createService(RestSettingService.class,
          Constants.UPDATE_USER, Constants.UPDATE_PASS);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Service Generator", "can not create update service " + ex.getMessage());
      Timber.e(ex);
      return;
    }

    String url;
    if (retry == 0) {
      url = String.format(API_SETTING_URI, Constants.ApplicationKey, companyKey);
    } else {
      url = String.format(API_SETTING_URI2, Constants.ApplicationKey, companyKey);
      retry++;
    }

    Call<CompanyInfoResponse> call = restSettingService.getCompanyInfo(url);
    call.enqueue(new Callback<CompanyInfoResponse>() {
      @Override
      public void onResponse(Call<CompanyInfoResponse> call,
          Response<CompanyInfoResponse> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {
            retry = 0;
            CompanyInfoResponse updateResponse = response.body();
            EventBus.getDefault().post(updateResponse);
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.INVALID_DATA));
          }
        } else {
          if (retry == 0) {
            retry++;
            getCompanyInfo(context, companyKey);
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
            retry = 0;
          }
        }
      }

      @Override
      public void onFailure(Call<CompanyInfoResponse> call, Throwable t) {
        if (retry == 0) {
          retry++;
          getCompanyInfo(context, companyKey);
        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
          retry = 0;
        }
      }
    });
  }

  public static void getCompanySetting(final Context context, String uri, String username,
      String password, SaleType role) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      return;
    }

    SettingDto settingDto = new SettingDto(username, password, role.toString());
    RestSettingService restSettingService = null;
    try {
      restSettingService = ServiceGenerator
          .createService(RestSettingService.class, username, password);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Service Generator", "can not create update service " + ex.getMessage());
      Timber.e(ex);
      return;
    }

    Call<SettingResponse> call = restSettingService
        .getCompanySetting(String.format(API_AUTHENTICATE_URI, uri), settingDto);
    call.enqueue(new Callback<SettingResponse>() {
      @Override
      public void onResponse(Call<SettingResponse> call,
          Response<SettingResponse> response) {
        if (response.isSuccessful()) {

          if (response.body() != null) {
            SettingResponse settingResponse = response.body();
            EventBus.getDefault().post(settingResponse);

          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.INVALID_DATA));
          }
        } else {
          if (response.code() == 401) {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.INVALID_DATA));
          } else if (response.code() == 403) {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.FORBIDDEN));
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
          }
        }
      }

      @Override
      public void onFailure(Call<SettingResponse> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
