package com.nguyenmanhphuc.view2earn.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nguyenmanhphuc.view2earn.app.models.User;
import com.nguyenmanhphuc.view2earn.app.services.HttpRequest;
import com.nguyenmanhphuc.view2earn.app.services.Response;
import com.nguyenmanhphuc.view2earn.app.utils.SessionManager;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    // Lấy AdMob Rewarded Ad Unit ID từ BuildConfig (tự động theo Debug / Release)
    private static final String AD_UNIT_ID = BuildConfig.ADMOB_REWARDED_AD_ID;
    private static final double REWARD_PER_AD = 0.50; // Mỗi lượt xem nhận $0.50

    private TextView tvBalance;
    private TextView tvResetTimer;
    private TextView tvDailyProgressAmount;
    private TextView tvDailyProgressStatus;
    private MaterialButton btnTabHome;
    private MaterialButton btnTabAccount;
    private MaterialButton btnLogout;
    private MaterialButton btnCurrency;
    private MaterialCardView cardWatchAds;
    private MaterialCardView cardRateApp;

    private RewardedAd rewardedAd;
    private boolean isLoadingAd = false;

    private CountDownTimer resetCountDownTimer;
    private SessionManager sessionManager;
    private boolean isUSD = true;
    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Áp dụng WindowInsets an toàn với viền màn hình
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);

        initViews();
        setupListeners();
        startDailyResetTimer();

        // Khởi tạo Google Mobile Ads SDK và tải trước video quảng cáo
        MobileAds.initialize(this, initializationStatus -> {});
        loadRewardedAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBalanceFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resetCountDownTimer != null) {
            resetCountDownTimer.cancel();
        }
    }

    private void initViews() {
        tvBalance = findViewById(R.id.tvBalance);
        tvResetTimer = findViewById(R.id.tvResetTimer);
        tvDailyProgressAmount = findViewById(R.id.tvDailyProgressAmount);
        tvDailyProgressStatus = findViewById(R.id.tvDailyProgressStatus);
        btnTabHome = findViewById(R.id.btnTabHome);
        btnTabAccount = findViewById(R.id.btnTabAccount);
        btnLogout = findViewById(R.id.btnLogout);
        btnCurrency = findViewById(R.id.btnCurrency);
        cardWatchAds = findViewById(R.id.cardWatchAds);
        cardRateApp = findViewById(R.id.cardRateApp);

        // Hiển thị số dư tạm từ session trước khi gọi API
        currentBalance = sessionManager.getBalance();
        displayBalance(currentBalance);
    }

    private void setupListeners() {
        btnTabHome.setOnClickListener(v -> {
            Toast.makeText(this, "Đang ở Trang chủ", Toast.LENGTH_SHORT).show();
        });

        btnTabAccount.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Tài khoản của bạn")
                        .setMessage("Tên người dùng: " + sessionManager.getUsername() + "\nSố dư: $" + String.format(Locale.US, "%.2f", currentBalance))
                        .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                        .setNegativeButton("Đóng", null)
                        .show();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý nút Đăng xuất
        btnLogout.setOnClickListener(v -> performLogout());

        btnCurrency.setOnClickListener(v -> {
            isUSD = !isUSD;
            btnCurrency.setText(isUSD ? "USD ($)" : "VND (₫)");
            displayBalance(currentBalance);
        });

        // Xử lý khi nhấn nút Xem quảng cáo kiếm tiền
        cardWatchAds.setOnClickListener(v -> {
            showRewardedAd();
        });

        cardRateApp.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Thực hiện đăng xuất tài khoản và xóa cache đăng nhập
     */
    private void performLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                    // Xóa toàn bộ session & cache đã lưu
                    sessionManager.clearSession();
                    Toast.makeText(MainActivity.this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển về màn hình đăng nhập và xóa ngăn xếp Activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    /**
     * Tải trước quảng cáo có thưởng (Rewarded Ad)
     */
    private void loadRewardedAd() {
        if (isLoadingAd || rewardedAd != null) return;
        isLoadingAd = true;

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, AD_UNIT_ID, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardedAd = null;
                isLoadingAd = false;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                isLoadingAd = false;

                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        rewardedAd = null;
                        // Tải sẵn quảng cáo cho lượt xem tiếp theo
                        loadRewardedAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        rewardedAd = null;
                        loadRewardedAd();
                    }
                });
            }
        });
    }

    /**
     * Hiển thị quảng cáo và nhận thưởng khi xem hết
     */
    private void showRewardedAd() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập tài khoản để nhận tiền thưởng!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }

        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem -> {
                // Người dùng đã xem xong quảng cáo hợp lệ
                claimRewardFromServer(REWARD_PER_AD);
            });
        } else {
            Toast.makeText(this, "Đang tải video quảng cáo, vui lòng thử lại sau 2 giây...", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
        }
    }

    /**
     * Gửi request lên server để cộng tiền thưởng vào tài khoản MongoDB
     */
    private void claimRewardFromServer(double rewardAmount) {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đang cộng tiền thưởng vào tài khoản...", Toast.LENGTH_SHORT).show();

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("rewardAmount", rewardAmount);

        HttpRequest.getInstance().call().claimReward(body).enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<User> res = response.body();
                    if (res.isSuccess() && res.getData() != null) {
                        currentBalance = res.getData().getBalance();
                        sessionManager.updateBalance(currentBalance);
                        displayBalance(currentBalance);

                        Toast.makeText(MainActivity.this, "🎉 Chúc mừng! Bạn vừa nhận được +$" + String.format(Locale.US, "%.2f", rewardAmount), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, res.getMessage() != null ? res.getMessage() : "Lỗi cộng tiền", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi server khi nhận thưởng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Lấy số dư khả dụng thực tế từ server MongoDB
     */
    private void loadBalanceFromServer() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            displayBalance(currentBalance);
            return;
        }

        HttpRequest.getInstance().call().getUserProfile(userId).enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<User> res = response.body();
                    if (res.isSuccess() && res.getData() != null) {
                        currentBalance = res.getData().getBalance();
                        sessionManager.updateBalance(currentBalance);
                        displayBalance(currentBalance);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                // Giữ nguyên số dư đã lưu từ session
                displayBalance(currentBalance);
            }
        });
    }

    private void displayBalance(double balance) {
        if (isUSD) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            tvBalance.setText("$" + df.format(balance));
        } else {
            // Quy đổi sang VND tỷ giá x25,000
            DecimalFormat df = new DecimalFormat("#,###");
            tvBalance.setText(df.format(balance * 25000) + " ₫");
        }
    }

    /**
     * Bộ đếm ngược tự động 24h đặt lại giới hạn hàng ngày (Reset vào 00:00:00 mỗi ngày)
     */
    private void startDailyResetTimer() {
        if (resetCountDownTimer != null) {
            resetCountDownTimer.cancel();
        }

        Calendar now = Calendar.getInstance();
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 24);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        long millisUntilMidnight = midnight.getTimeInMillis() - now.getTimeInMillis();
        if (millisUntilMidnight <= 0) {
            millisUntilMidnight = 24 * 60 * 60 * 1000L;
        }

        resetCountDownTimer = new CountDownTimer(millisUntilMidnight, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                tvResetTimer.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                tvResetTimer.setText("00:00:00");
                Toast.makeText(MainActivity.this, "Giới hạn hàng ngày đã được làm mới!", Toast.LENGTH_SHORT).show();
                // Tự động lặp lại cho ngày mới
                startDailyResetTimer();
            }
        }.start();
    }
}
