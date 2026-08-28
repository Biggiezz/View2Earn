package com.vuhongcat.view2earn.app.services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class HttpRequest {
    // 10.0.2.2 là localhost trên Android Emulator, đổi sang IP LAN nếu chạy máy thật (vd: 192.168.1.x)
//    public static final String BASE_URL = "http://10.0.2.2:5001/";
    public static final String BASE_URL = "https://view2earnserver.vercel.app/";
    private static HttpRequest instance;
    private final ApiService apiService;

    public HttpRequest() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized HttpRequest getInstance() {
        if (instance == null) {
            instance = new HttpRequest();
        }
        return instance;
    }

    public ApiService call() {
        return apiService;
    }
}
