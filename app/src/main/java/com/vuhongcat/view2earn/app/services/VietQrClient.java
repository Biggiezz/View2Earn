package com.vuhongcat.view2earn.app.services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class VietQrClient {
    public static final String BASE_URL = "https://api.vietqr.io/";
    private static VietQrClient instance;
    private final VietQrService service;

    private VietQrClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(VietQrService.class);
    }

    public static synchronized VietQrClient getInstance() {
        if (instance == null) {
            instance = new VietQrClient();
        }
        return instance;
    }

    public VietQrService getService() {
        return service;
    }
}
