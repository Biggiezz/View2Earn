package com.vuhongcat.view2earn.app;

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
import com.vuhongcat.view2earn.app.models.User;
import com.vuhongcat.view2earn.app.services.HttpRequest;
import com.vuhongcat.view2earn.app.services.Response;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
            String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

            if (TextUtils.isEmpty(fullName)) {
                etFullName.setError("Vui lòng nhập username / họ và tên");
                etFullName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Vui lòng nhập email");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Vui lòng nhập mật khẩu");
                etPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                etPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                etConfirmPassword.requestFocus();
                return;
            }

            btnRegister.setEnabled(false);
            btnRegister.setText("Đang đăng ký...");

            Map<String, Object> body = new HashMap<>();
            body.put("username", fullName);
            body.put("email", email);
            body.put("password", password);

            HttpRequest.getInstance().call().register(body).enqueue(new Callback<Response<User>>() {
                @Override
                public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Đăng ký");

                    if (response.isSuccessful() && response.body() != null) {
                        Response<User> res = response.body();
                        if (res.getCode() == 200 || res.getCode() == 201 || res.getData() != null) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, res.getMessage() != null ? res.getMessage() : "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<User>> call, Throwable t) {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Đăng ký");
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}
