package com.parsroyal.solutiontablet.service;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Arash on 2017-02-16
 * Generate different Retrofit services to call REST services
 */

public class ServiceGenerator {

  private static Retrofit retrofit;
  private static OkHttpClient.Builder httpClient =
      new OkHttpClient.Builder()
          .readTimeout(300, TimeUnit.SECONDS)
          .connectTimeout(60, TimeUnit.SECONDS);
  //Change different level of logging here
  private static HttpLoggingInterceptor logging =
      new HttpLoggingInterceptor()
          .setLevel(Level.BODY);
  private static Retrofit.Builder builder;

  public static <S> S createService(Class<S> serviceClass) {

    return createService(serviceClass, null, null);
  }

  public static <S> S createService(Class<S> serviceClass, String username, String password) {
    String authToken = null;
    if (Empty.isNotEmpty(username) && Empty.isNotEmpty(password)) {
      authToken = Credentials.basic(username, password);
    }
    return createService(serviceClass, authToken);
  }

  public static <S> S createService(Class<S> serviceClass, final String authToken) {
    AuthenticationInterceptor interceptor;
    if (!TextUtils.isEmpty(authToken)) {
      interceptor = new AuthenticationInterceptor(authToken);
    } else {
      interceptor = new AuthenticationInterceptor(null);
    }
    httpClient.interceptors().clear();
//    if (!httpClient.interceptors().contains(interceptor)) {
      httpClient.addInterceptor(interceptor);
//    }
    if (BuildConfig.DEBUG) {
      httpClient.addInterceptor(logging);
    }

    String baseUrl = new SettingServiceImpl()
        .getSettingValue(ApplicationKeys.BACKEND_URI);
    if (Empty.isEmpty(baseUrl)) {
      baseUrl = "http://www.google.com";
    }
    builder = new Retrofit.Builder().baseUrl(baseUrl + "/");

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(BigDecimal.class,
        (JsonSerializer<BigDecimal>) (src, typeOfSrc, context) -> new JsonPrimitive(src));

    Gson gson = gsonBuilder.setLenient().create();

    builder.addConverterFactory(GsonConverterFactory.create(gson));
    builder.client(httpClient.build());
    retrofit = builder.build();
//    Gson gson = new GsonBuilder()
//        .registerTypeAdapter(Id.class, new IdTypeAdapter())
//        .enableComplexMapKeySerialization()
//        .serializeNulls()
//        .setDateFormat(DateFormat.LONG)
//        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//        .setPrettyPrinting()
//        .setVersion(1.0)
//        .create();
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

      Request.Builder builder;
      if (Empty.isEmpty(authToken)) {
        String token = new SettingServiceImpl(
        ).getSettingValue(ApplicationKeys.TOKEN);
        builder = original.newBuilder().addHeader("Authorization", "Bearer " + token);
      } else {
        builder = original.newBuilder().header("Authorization", authToken);
      }

      builder.addHeader("User-Agent", "SolutionTablet");
      Request request = builder.url(encodedRequest).build();
      return chain.proceed(request);
    }
  }
}
