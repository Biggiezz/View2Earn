package com.vuhongcat.view2earn.app.services;

import com.vuhongcat.view2earn.app.models.ReferralData;
import com.vuhongcat.view2earn.app.models.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // Đăng ký tài khoản
    @POST("api/users/register")
    Call<Response<User>> register(@Body Map<String, Object> body);

    // Đăng nhập
    @POST("api/users/login")
    Call<Response<User>> login(@Body Map<String, Object> body);

    // Lấy thông tin user / số dư
    @GET("api/users/profile/{id}")
    Call<Response<User>> getUserProfile(@Path("id") String userId);

    // Nhận thưởng khi xem quảng cáo
    @POST("api/users/reward")
    Call<Response<User>> claimReward(@Body Map<String, Object> body);

    // Lấy thông tin mã giới thiệu cá nhân & thống kê
    @GET("api/referral/me")
    Call<Response<ReferralData>> getReferralMe(@Header("Authorization") String token);

    // Lấy lịch sử nhận thưởng / rút tiền
    @GET("api/users/history")
    Call<Response<com.vuhongcat.view2earn.app.models.HistoryData>> getHistory(@Header("Authorization") String token);

    // Nhập mã giới thiệu của bạn bè nhận thưởng $1.00
    @POST("api/referral/claim")
    Call<Response<Object>> claimReferralCode(@Header("Authorization") String token, @Body Map<String, Object> body);
}
