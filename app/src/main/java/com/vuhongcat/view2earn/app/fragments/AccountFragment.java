package com.vuhongcat.view2earn.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vuhongcat.view2earn.app.LoginActivity;
import com.vuhongcat.view2earn.app.R;
import com.vuhongcat.view2earn.app.models.HistoryData;
import com.vuhongcat.view2earn.app.models.ReferralData;
import com.vuhongcat.view2earn.app.models.TransactionItem;
import com.vuhongcat.view2earn.app.models.User;
import com.vuhongcat.view2earn.app.services.HttpRequest;
import com.vuhongcat.view2earn.app.services.Response;
import com.vuhongcat.view2earn.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;

public class AccountFragment extends Fragment {

    private TextView tvAvatarChar;
    private TextView tvAccountUsername;
    private TextView tvAccountSubtext;
    private TextView tvAccountGreeting;
    private TextView tvStatAdsWatched;
    private TextView tvStatReferrals;
    private TextView tvStatDailyLimit;
    private MaterialCardView cardProfile;
    private MaterialButton btnAccountLogout;
    private LinearLayout layoutRecentWithdrawalsList;
    private View layoutRecentWithdrawalsEmpty;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            sessionManager = SessionManager.getInstance(getContext());
        }

        initViews(view);
        setupListeners();
        loadAccountData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAccountData();
    }

    private void initViews(View view) {
        tvAvatarChar = view.findViewById(R.id.tvAvatarChar);
        tvAccountUsername = view.findViewById(R.id.tvAccountUsername);
        tvAccountSubtext = view.findViewById(R.id.tvAccountSubtext);
        tvAccountGreeting = view.findViewById(R.id.tvAccountGreeting);
        tvStatAdsWatched = view.findViewById(R.id.tvStatAdsWatched);
        tvStatReferrals = view.findViewById(R.id.tvStatReferrals);
        tvStatDailyLimit = view.findViewById(R.id.tvStatDailyLimit);
        cardProfile = view.findViewById(R.id.cardProfile);
        btnAccountLogout = view.findViewById(R.id.btnAccountLogout);
        layoutRecentWithdrawalsList = view.findViewById(R.id.layoutRecentWithdrawalsList);
        layoutRecentWithdrawalsEmpty = view.findViewById(R.id.layoutRecentWithdrawalsEmpty);
    }

    private void setupListeners() {
        if (btnAccountLogout != null) {
            btnAccountLogout.setOnClickListener(v -> performLogout());
        }
    }

    private void loadAccountData() {
        if (sessionManager == null) return;

        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            username = "user_5524";
        }

        tvAccountUsername.setText(username);
        tvAccountGreeting.setText("Chào mừng trở lại, " + username);

        String avatarLetter = username.substring(0, 1).toUpperCase(Locale.getDefault());
        tvAvatarChar.setText(avatarLetter);

        double balance = sessionManager.getBalance();
        tvStatDailyLimit.setText("$" + String.format(Locale.US, "%.3f", balance));

        String userId = sessionManager.getUserId();
        if (!userId.isEmpty()) {
            // Tải thông tin tài khoản & số dư & quảng cáo đã xem từ server MongoDB
            HttpRequest.getInstance().call().getUserProfile(userId).enqueue(new Callback<Response<User>>() {
                @Override
                public void onResponse(@NonNull Call<Response<User>> call, @NonNull retrofit2.Response<Response<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess() && response.body().getData() != null) {
                        User user = response.body().getData();
                        double freshBalance = user.getBalance();
                        sessionManager.updateBalance(freshBalance);
                        if (tvStatDailyLimit != null) {
                            tvStatDailyLimit.setText("$" + String.format(Locale.US, "%.3f", freshBalance));
                        }
                        if (tvStatAdsWatched != null) {
                            tvStatAdsWatched.setText(String.valueOf(user.getAdsWatched()));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Response<User>> call, @NonNull Throwable t) {
                }
            });
        }

        // Tải số liệu thống kê Referral
        String token = sessionManager.getToken();
        if (!token.isEmpty()) {
            String bearerToken = "Bearer " + token;

            HttpRequest.getInstance().call().getReferralMe(bearerToken).enqueue(new Callback<Response<ReferralData>>() {
                @Override
                public void onResponse(@NonNull Call<Response<ReferralData>> call, @NonNull retrofit2.Response<Response<ReferralData>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        ReferralData data = response.body().getData();
                        if (data != null && tvStatReferrals != null) {
                            tvStatReferrals.setText(data.getQualified() + "/" + data.getTotalInvited());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Response<ReferralData>> call, @NonNull Throwable t) {
                }
            });

            // Tải lịch sử giao dịch / rút tiền
            HttpRequest.getInstance().call().getHistory(bearerToken).enqueue(new Callback<Response<HistoryData>>() {
                @Override
                public void onResponse(@NonNull Call<Response<HistoryData>> call, @NonNull retrofit2.Response<Response<HistoryData>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess() && response.body().getData() != null) {
                        List<TransactionItem> items = response.body().getData().getTransactions();
                        renderTransactions(items);
                    } else {
                        renderTransactions(null);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Response<HistoryData>> call, @NonNull Throwable t) {
                    renderTransactions(null);
                }
            });
        } else {
            renderTransactions(null);
        }
    }

    private void renderTransactions(List<TransactionItem> items) {
        if (layoutRecentWithdrawalsList == null) return;

        layoutRecentWithdrawalsList.removeAllViews();

        java.util.List<TransactionItem> displayItems = new java.util.ArrayList<>();
        if (items != null) {
            for (TransactionItem item : items) {
                // Ưu tiên hiển thị các giao dịch rút tiền hoặc giao dịch thưởng
                displayItems.add(item);
            }
        }

        if (displayItems.isEmpty()) {
            if (layoutRecentWithdrawalsEmpty != null) {
                layoutRecentWithdrawalsEmpty.setVisibility(View.VISIBLE);
            }
            layoutRecentWithdrawalsList.setVisibility(View.GONE);
            return;
        }

        if (layoutRecentWithdrawalsEmpty != null) {
            layoutRecentWithdrawalsEmpty.setVisibility(View.GONE);
        }
        layoutRecentWithdrawalsList.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int limit = Math.min(displayItems.size(), 10);
        for (int i = 0; i < limit; i++) {
            TransactionItem item = displayItems.get(i);
            View itemView = inflater.inflate(R.layout.item_recent_withdrawal, layoutRecentWithdrawalsList, false);

            TextView tvItemAvatarChar = itemView.findViewById(R.id.tvItemAvatarChar);
            TextView tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            TextView tvItemDate = itemView.findViewById(R.id.tvItemDate);
            TextView tvItemAmount = itemView.findViewById(R.id.tvItemAmount);

            String title = "Thưởng xem QC";
            String avatarChar = "Q";
            String amountPrefix = "+$";

            if ("WITHDRAWAL".equalsIgnoreCase(item.getType())) {
                title = "Rút tiền";
                avatarChar = "R";
                amountPrefix = "-$";
            } else if ("REFERRAL_BONUS".equalsIgnoreCase(item.getType())) {
                title = "Thưởng giới thiệu";
                avatarChar = "G";
                amountPrefix = "+$";
            }

            if (tvItemTitle != null) tvItemTitle.setText(title);
            if (tvItemAvatarChar != null) tvItemAvatarChar.setText(avatarChar);

            if (tvItemAmount != null) {
                tvItemAmount.setText(amountPrefix + String.format(Locale.US, "%.3f", item.getAmount()));
            }

            if (tvItemDate != null) {
                tvItemDate.setText(formatDate(item.getCreatedAt()));
            }

            layoutRecentWithdrawalsList.addView(itemView);
        }
    }

    private String formatDate(String isoDateStr) {
        if (isoDateStr == null || isoDateStr.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(isoDateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoDateStr;
        }
    }

    private void performLogout() {
        if (getContext() == null || sessionManager == null) return;

        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                    sessionManager.clearSession();
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
}
