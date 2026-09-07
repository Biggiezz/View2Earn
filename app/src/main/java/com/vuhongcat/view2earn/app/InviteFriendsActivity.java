package com.vuhongcat.view2earn.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.vuhongcat.view2earn.app.models.ReferralData;
import com.vuhongcat.view2earn.app.services.HttpRequest;
import com.vuhongcat.view2earn.app.services.Response;
import com.vuhongcat.view2earn.app.utils.SessionManager;

import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;

public class InviteFriendsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvReferralCode;
    private MaterialButton btnCopyCode;
    private MaterialButton btnShare;
    private TextView tvTotalInvited;
    private TextView tvQualified;
    private TextView tvPending;
    private TextView tvTotalEarned;

    private String currentReferralCode;
    private String currentShareLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.invite_friends_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupListeners();

        // Sinh mã ngẫu nhiên mặc định (tránh fix cứng)
        currentReferralCode = generateRandomReferralCode();
        currentShareLink = "https://view2earn.app/invite/" + currentReferralCode;
        updateUI();

        // Tải mã giới thiệu và thống kê từ server nếu có session
        fetchReferralData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvReferralCode = findViewById(R.id.tvReferralCode);
        btnCopyCode = findViewById(R.id.btnCopyCode);
        btnShare = findViewById(R.id.btnShare);
        tvTotalInvited = findViewById(R.id.tvTotalInvited);
        tvQualified = findViewById(R.id.tvQualified);
        tvPending = findViewById(R.id.tvPending);
        tvTotalEarned = findViewById(R.id.tvTotalEarned);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCopyCode.setOnClickListener(v -> copyReferralCodeToClipboard());
        btnShare.setOnClickListener(v -> shareReferralLink());
    }

    private void updateUI() {
        tvReferralCode.setText(currentReferralCode);
        tvTotalInvited.setText("0");
        tvQualified.setText("0");
        tvPending.setText("0");
        tvTotalEarned.setText("$0.00");
    }

    private String generateRandomReferralCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder("V2EARN-");
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void fetchReferralData() {
        String token = SessionManager.getInstance(this).getToken();
        if (TextUtils.isEmpty(token)) {
            // Không có token, sử dụng mã sinh ngẫu nhiên
            return;
        }

        HttpRequest.getInstance().call().getReferralMe("Bearer " + token).enqueue(new Callback<Response<ReferralData>>() {
            @Override
            public void onResponse(@NonNull Call<Response<ReferralData>> call, @NonNull retrofit2.Response<Response<ReferralData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ReferralData data = response.body().getData();
                    if (data != null) {
                        if (!TextUtils.isEmpty(data.getReferralCode())) {
                            currentReferralCode = data.getReferralCode();
                        }
                        if (!TextUtils.isEmpty(data.getShareLink())) {
                            currentShareLink = data.getShareLink();
                        } else {
                            currentShareLink = "https://view2earn.app/invite/" + currentReferralCode;
                        }
                        tvReferralCode.setText(currentReferralCode);
                        tvTotalInvited.setText(String.valueOf(data.getTotalInvited()));
                        tvQualified.setText(String.valueOf(data.getQualified()));
                        tvPending.setText(String.valueOf(data.getPending()));
                        tvTotalEarned.setText(String.format(Locale.US, "$%.2f", data.getTotalEarned()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response<ReferralData>> call, @NonNull Throwable t) {
                // Nếu lỗi kết nối, giữ nguyên mã ngẫu nhiên đã tạo
            }
        });
    }

    private void copyReferralCodeToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("View2Earn Referral Code", currentReferralCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã sao chép mã giới thiệu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareReferralLink() {
        String shareMessage = "Tham gia View2Earn ngay để xem quảng cáo kiếm tiền thưởng!\n\n" +
                "Sử dụng mã giới thiệu của tôi: " + currentReferralCode + "\n" +
                "Tải ứng dụng tại: " + (TextUtils.isEmpty(currentShareLink) ? "https://view2earn.app/invite/" + currentReferralCode : currentShareLink);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mời bạn bè View2Earn");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareIntent, "Chia sẻ mã giới thiệu qua"));
    }
}
