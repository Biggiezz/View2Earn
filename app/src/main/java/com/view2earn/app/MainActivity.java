package com.view2earn.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private TextView tvBalance;
    private TextView tvResetTimer;
    private MaterialButton btnTabHome;
    private MaterialButton btnTabAccount;
    private MaterialButton btnCurrency;
    private MaterialCardView cardWatchAds;
    private MaterialCardView cardRateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvBalance = findViewById(R.id.tvBalance);
        tvResetTimer = findViewById(R.id.tvResetTimer);
        btnTabHome = findViewById(R.id.btnTabHome);
        btnTabAccount = findViewById(R.id.btnTabAccount);
        btnCurrency = findViewById(R.id.btnCurrency);
        cardWatchAds = findViewById(R.id.cardWatchAds);
        cardRateApp = findViewById(R.id.cardRateApp);
    }

    private void setupListeners() {
        btnTabHome.setOnClickListener(v -> {
            Toast.makeText(this, "Đang ở Trang chủ", Toast.LENGTH_SHORT).show();
        });

        btnTabAccount.setOnClickListener(v -> {
            // Chuyển sang màn hình Login / Tài khoản
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnCurrency.setOnClickListener(v -> {
            Toast.makeText(this, "Chọn đơn vị tiền tệ", Toast.LENGTH_SHORT).show();
        });

        cardWatchAds.setOnClickListener(v -> {
            Toast.makeText(this, "Đang tải video quảng cáo...", Toast.LENGTH_SHORT).show();
        });

        cardRateApp.setOnClickListener(v -> {
            Toast.makeText(this, "Mở trang đánh giá ứng dụng...", Toast.LENGTH_SHORT).show();
        });
    }
}
