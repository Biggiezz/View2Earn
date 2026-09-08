package com.vuhongcat.view2earn.app.services;

import com.vuhongcat.view2earn.app.models.Bank;
import com.vuhongcat.view2earn.app.models.VietQrLookupData;
import com.vuhongcat.view2earn.app.models.VietQrResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface VietQrService {
    @GET("v2/banks")
    Call<VietQrResponse<List<Bank>>> getBanks();

    @POST("v2/lookup")
    Call<VietQrResponse<VietQrLookupData>> lookupAccount(
            @Header("x-client-id") String clientId,
            @Header("x-api-key") String apiKey,
            @Body Map<String, Object> body
    );
}
