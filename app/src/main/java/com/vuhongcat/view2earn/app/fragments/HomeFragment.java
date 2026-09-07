package com.vuhongcat.view2earn.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.gms.tasks.Task;
import com.vuhongcat.view2earn.app.BuildConfig;
import com.vuhongcat.view2earn.app.InviteFriendsActivity;
import com.vuhongcat.view2earn.app.LoginActivity;
import com.vuhongcat.view2earn.app.R;
import com.vuhongcat.view2earn.app.models.User;
import com.vuhongcat.view2earn.app.services.HttpRequest;
import com.vuhongcat.view2earn.app.services.Response;
import com.vuhongcat.view2earn.app.utils.SessionManager;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {

    private static final String AD_UNIT_ID = BuildConfig.ADMOB_REWARDED_AD_ID;
    private static final double REWARD_PER_AD = 0.001;

    private TextView tvBalance;
    private TextView tvResetTimer;
    private TextView tvDailyProgressAmount;
    private TextView tvDailyProgressStatus;
    private MaterialButton btnCurrency;
    private MaterialCardView cardWatchAds;
    private MaterialCardView cardRateApp;
    private MaterialCardView cardInviteFriends;

    private RewardedAd rewardedAd;
    private boolean isLoadingAd = false;
    private CountDownTimer resetCountDownTimer;
    private SessionManager sessionManager;
    private boolean isUSD = true;
    private double currentBalance = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            sessionManager = SessionManager.getInstance(getContext());
        }

        initViews(view);
        setupListeners();
        startDailyResetTimer();

        if (getContext() != null) {
            MobileAds.initialize(getContext(), initializationStatus -> {});
            loadRewardedAd();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBalanceFromServer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (resetCountDownTimer != null) {
            resetCountDownTimer.cancel();
        }
    }

    private void initViews(View view) {
        tvBalance = view.findViewById(R.id.tvBalance);
        tvResetTimer = view.findViewById(R.id.tvResetTimer);
        tvDailyProgressAmount = view.findViewById(R.id.tvDailyProgressAmount);
        tvDailyProgressStatus = view.findViewById(R.id.tvDailyProgressStatus);
        btnCurrency = view.findViewById(R.id.btnCurrency);
        cardWatchAds = view.findViewById(R.id.cardWatchAds);
        cardRateApp = view.findViewById(R.id.cardRateApp);
        cardInviteFriends = view.findViewById(R.id.cardInviteFriends);

        if (sessionManager != null) {
            currentBalance = sessionManager.getBalance();
        }
        displayBalance(currentBalance);
    }

    private void setupListeners() {
        btnCurrency.setOnClickListener(v -> {
            isUSD = !isUSD;
            btnCurrency.setText(isUSD ? "USD ($)" : "VND (₫)");
            displayBalance(currentBalance);
        });

        cardWatchAds.setOnClickListener(v -> showRewardedAd());
        cardRateApp.setOnClickListener(v -> launchInAppReview());
        cardInviteFriends.setOnClickListener(v -> {
            if (getContext() != null) {
                startActivity(new Intent(getContext(), InviteFriendsActivity.class));
            }
        });
    }

    private void loadRewardedAd() {
        if (isLoadingAd || rewardedAd != null || getContext() == null) return;
        isLoadingAd = true;

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(getContext(), AD_UNIT_ID, adRequest, new RewardedAdLoadCallback() {
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

    private void showRewardedAd() {
        if (sessionManager == null || !sessionManager.isLoggedIn()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập tài khoản để nhận tiền thưởng!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
            return;
        }

        if (rewardedAd != null && getActivity() != null) {
            rewardedAd.show(getActivity(), rewardItem -> claimRewardFromServer(REWARD_PER_AD));
        } else {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Đang tải video quảng cáo, vui lòng thử lại sau 2 giây...", Toast.LENGTH_SHORT).show();
            }
            loadRewardedAd();
        }
    }

    private void claimRewardFromServer(double rewardAmount) {
        if (sessionManager == null) return;
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            if (getContext() != null) Toast.makeText(getContext(), "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getContext() != null) Toast.makeText(getContext(), "Đang cộng tiền thưởng vào tài khoản...", Toast.LENGTH_SHORT).show();

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("rewardAmount", rewardAmount);

        HttpRequest.getInstance().call().claimReward(body).enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(@NonNull Call<Response<User>> call, @NonNull retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<User> res = response.body();
                    if (res.isSuccess() && res.getData() != null) {
                        currentBalance = res.getData().getBalance();
                        sessionManager.updateBalance(currentBalance);
                        displayBalance(currentBalance);

                        if (getContext() != null) {
                            Toast.makeText(getContext(), "🎉 Chúc mừng! Bạn vừa nhận được +$" + String.format(Locale.US, "%.3f", rewardAmount), Toast.LENGTH_LONG).show();
                        }
                    } else if (getContext() != null) {
                        Toast.makeText(getContext(), res.getMessage() != null ? res.getMessage() : "Lỗi cộng tiền", Toast.LENGTH_SHORT).show();
                    }
                } else if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi server khi nhận thưởng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response<User>> call, @NonNull Throwable t) {
                if (getContext() != null) Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBalanceFromServer() {
        if (sessionManager == null) return;
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            displayBalance(currentBalance);
            return;
        }

        HttpRequest.getInstance().call().getUserProfile(userId).enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(@NonNull Call<Response<User>> call, @NonNull retrofit2.Response<Response<User>> response) {
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
            public void onFailure(@NonNull Call<Response<User>> call, @NonNull Throwable t) {
                displayBalance(currentBalance);
            }
        });
    }

    private void displayBalance(double balance) {
        if (tvBalance == null) return;
        if (isUSD) {
            DecimalFormat df = new DecimalFormat("#,##0.000");
            tvBalance.setText("$" + df.format(balance));
        } else {
            DecimalFormat df = new DecimalFormat("#,###");
            tvBalance.setText(df.format(balance * 25000) + " ₫");
        }
    }

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
                if (tvResetTimer != null) {
                    tvResetTimer.setText(timeFormatted);
                }
            }

            @Override
            public void onFinish() {
                if (tvResetTimer != null) tvResetTimer.setText("00:00:00");
                if (getContext() != null) Toast.makeText(getContext(), "Giới hạn hàng ngày đã được làm mới!", Toast.LENGTH_SHORT).show();
                startDailyResetTimer();
            }
        }.start();
    }

    private void launchInAppReview() {
        if (getActivity() == null) return;
        ReviewManager manager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && getActivity() != null) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnCompleteListener(reviewTask -> {});
            } else {
                openPlayStorePage();
            }
        });
    }

    private void openPlayStorePage() {
        if (getContext() == null) return;
        String packageName = getContext().getPackageName();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            intent.setPackage("com.android.vending");
            startActivity(intent);
        } catch (Exception e) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                startActivity(browserIntent);
            } catch (Exception ex) {
                Toast.makeText(getContext(), "Không thể mở Google Play Store!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
