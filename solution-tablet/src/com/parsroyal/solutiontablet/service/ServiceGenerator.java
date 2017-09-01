package com.parsroyal.solutiontablet.service;

import android.text.TextUtils;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Arash on 2017-02-16.
 * Generate different Retrofit services to call REST services
 */

public class ServiceGenerator {

  private static Retrofit retrofit;
  private static OkHttpClient.Builder httpClient =
      new OkHttpClient.Builder()
          .readTimeout(60, TimeUnit.SECONDS)
          .connectTimeout(60, TimeUnit.SECONDS);
  //Change different level of logging here
  private static HttpLoggingInterceptor logging =
      new HttpLoggingInterceptor()
          .setLevel(HttpLoggingInterceptor.Level.BASIC);
  private static Retrofit.Builder builder;
  private static SettingService settingService = new SettingServiceImpl(
      SolutionTabletApplication.getInstance());

  public static <S> S createService(Class<S> serviceClass) {

    String username = settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME);
    String password = settingService.getSettingValue(ApplicationKeys.SETTING_PASSWORD);

    return createService(serviceClass, username, password);
  }

  public static <S> S createService(Class<S> serviceClass, String username, String password) {
    String authToken = Credentials.basic(username, password);
    return createService(serviceClass, authToken);
  }

  public static <S> S createService(Class<S> serviceClass, final String authToken) {
    if (!TextUtils.isEmpty(authToken)) {
      AuthenticationInterceptor interceptor =
          new AuthenticationInterceptor(authToken);

      if (!httpClient.interceptors().contains(interceptor)) {
        httpClient.addInterceptor(interceptor);
      }
      if (BuildConfig.DEBUG) {
        httpClient.addInterceptor(logging);
      }

      String baseUrl = new SettingServiceImpl(SolutionTabletApplication.getInstance())
          .getSettingValue(ApplicationKeys.BACKEND_URI);
      if (Empty.isEmpty(baseUrl)) {
        baseUrl = "http://www.google.com";
      }
      builder = new Retrofit.Builder().baseUrl(baseUrl + "/");
      builder.addConverterFactory(GsonConverterFactory.create());
      builder.client(httpClient.build());
      retrofit = builder.build();
    }

    return retrofit.create(serviceClass);
  }

  static class AuthenticationInterceptor implements Interceptor {

    private String authToken;

    public AuthenticationInterceptor(String token) {
      this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request original = chain.request();

      String encodedRequest = original.url().toString();
      encodedRequest = encodedRequest.replace("|", "%7c");
      Request.Builder builder = original.newBuilder()
          .header("Authorization", authToken);

      Request request = builder.url(encodedRequest).build();
      return chain.proceed(request);
    }
  }
}
