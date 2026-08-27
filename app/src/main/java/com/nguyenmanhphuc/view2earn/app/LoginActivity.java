package com.nguyenmanhphuc.view2earn.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nguyenmanhphuc.view2earn.app.models.User;
import com.nguyenmanhphuc.view2earn.app.services.HttpRequest;
import com.nguyenmanhphuc.view2earn.app.services.Response;
import com.nguyenmanhphuc.view2earn.app.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sessionManager = SessionManager.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {

        btnLogin.setOnClickListener(v -> {
            String emailOrUsername = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (TextUtils.isEmpty(emailOrUsername)) {
                etEmail.setError("Vui lòng nhập email hoặc username");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Vui lòng nhập mật khẩu");
                etPassword.requestFocus();
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("Đang đăng nhập...");

            Map<String, Object> body = new HashMap<>();
            if (emailOrUsername.contains("@")) {
                body.put("email", emailOrUsername);
            } else {
                body.put("username", emailOrUsername);
            }
            body.put("password", password);

            HttpRequest.getInstance().call().login(body).enqueue(new Callback<Response<User>>() {
                @Override
                public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");

                    if (response.isSuccessful() && response.body() != null) {
                        Response<User> res = response.body();
                        if (res.isSuccess() || res.getData() != null) {
                            // Lưu session người dùng và số dư
                            sessionManager.saveUser(res.getData());

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            
                            // Chuyển sang MainActivity và xoá ngăn xếp activity cũ
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, res.getMessage() != null ? res.getMessage() : "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<User>> call, Throwable t) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
