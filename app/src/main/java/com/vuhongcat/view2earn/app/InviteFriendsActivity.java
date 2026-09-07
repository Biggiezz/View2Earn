package com.vuhongcat.view2earn.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class InviteFriendsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvReferralCode;
    private MaterialButton btnCopyCode;
    private MaterialButton btnShare;
    private TextView tvTotalInvited;
    private TextView tvQualified;
    private TextView tvPending;
    private TextView tvTotalEarned;

    private String currentReferralCode = "V2EARN-A8F92K";

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
        loadReferralData();
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

    private void loadReferralData() {
        tvReferralCode.setText(currentReferralCode);
        tvTotalInvited.setText("0");
        tvQualified.setText("0");
        tvPending.setText("0");
        tvTotalEarned.setText("$0.00");
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
                "Tải ứng dụng tại: https://view2earn.app/invite/" + currentReferralCode;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mời bạn bè View2Earn");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareIntent, "Chia sẻ mã giới thiệu qua"));
    }
}
