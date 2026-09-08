package com.vuhongcat.view2earn.app.utils;

public class VietQrConfig {
    /**
     * Client ID và API Key lấy từ cổng quản lý VietQR (https://my.vietqr.io/)
     * Sau khi đăng ký và tạo ứng dụng trên VietQR, bạn hãy điền Client ID và API Key vào đây
     * để hệ thống tự động tra cứu tên chủ tài khoản thụ hưởng khi người dùng nhập số tài khoản.
     */
    public static String CLIENT_ID = "";
    public static String API_KEY = "";

    public static boolean hasCredentials() {
        return CLIENT_ID != null && !CLIENT_ID.trim().isEmpty() &&
               API_KEY != null && !API_KEY.trim().isEmpty();
    }
}
