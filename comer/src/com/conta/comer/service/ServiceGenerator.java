package com.conta.comer.service;

import android.text.TextUtils;

import com.conta.comer.BuildConfig;

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

public class ServiceGenerator
{

    public static final String API_BASE_URL = "http://173.212.199.107:9998";

    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS);
    //Change different level of logging here
    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BASIC);
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass)
    {
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, String username, String password)
    {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password))
        {
            String authToken = Credentials.basic(username, password);
            return createService(serviceClass, authToken);
        }

        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken)
    {
        if (!TextUtils.isEmpty(authToken))
        {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor))
            {
                httpClient.addInterceptor(interceptor);
            }
            if (BuildConfig.DEBUG)
            {
                httpClient.addInterceptor(logging);
            }

            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }

    static class AuthenticationInterceptor implements Interceptor
    {
        private String authToken;

        public AuthenticationInterceptor(String token)
        {
            this.authToken = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException
        {
            Request original = chain.request();

            Request.Builder builder = original.newBuilder()
                    .header("Authorization", authToken);

            Request request = builder.build();
            return chain.proceed(request);
        }
    }
}
