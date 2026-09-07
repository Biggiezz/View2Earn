package com.vuhongcat.view2earn.app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.vuhongcat.view2earn.app.adapters.MainPagerAdapter;
import com.vuhongcat.view2earn.app.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnTabHome;
    private MaterialButton btnTabAccount;
    private ViewPager2 viewPager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);

        initViews();
        setupViewPager();
        setupListeners();
    }

    private void initViews() {
        btnTabHome = findViewById(R.id.btnTabHome);
        btnTabAccount = findViewById(R.id.btnTabAccount);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupViewPager() {
        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabUI(position == 0);
            }
        });
    }

    private void setupListeners() {
        btnTabHome.setOnClickListener(v -> viewPager.setCurrentItem(0, true));

        btnTabAccount.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để xem thông tin tài khoản!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return;
            }
            viewPager.setCurrentItem(1, true);
        });
    }

    private void updateTabUI(boolean isHome) {
        if (isHome) {
            btnTabHome.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
            btnTabHome.setTextColor(ContextCompat.getColor(this, R.color.on_primary));

            btnTabAccount.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnTabAccount.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            btnTabAccount.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
            btnTabAccount.setTextColor(ContextCompat.getColor(this, R.color.on_primary));

            btnTabHome.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnTabHome.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }
    }
}