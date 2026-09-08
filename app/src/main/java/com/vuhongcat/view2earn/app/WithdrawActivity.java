package com.vuhongcat.view2earn.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vuhongcat.view2earn.app.adapters.BankDropdownAdapter;
import com.vuhongcat.view2earn.app.models.Bank;
import com.vuhongcat.view2earn.app.models.VietQrResponse;
import com.vuhongcat.view2earn.app.services.HttpRequest;
import com.vuhongcat.view2earn.app.services.Response;
import com.vuhongcat.view2earn.app.services.VietQrClient;
import com.vuhongcat.view2earn.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class WithdrawActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvWithdrawBalance;
    private TextInputLayout tilBankName;
    private MaterialAutoCompleteTextView actvBankName;
    private TextInputLayout tilAccountNumber;
    private TextInputEditText etAccountNumber;
    private TextInputLayout tilAccountHolder;
    private TextInputEditText etAccountHolder;
    private TextInputEditText etWithdrawAmount;
    private TextView btnMaxAmount;
    private TextInputEditText etWithdrawNote;
    private MaterialButton btnSubmitWithdraw;

    private SessionManager sessionManager;
    private double currentBalance = 0.0;

    private final List<Bank> bankList = new ArrayList<>();
    private BankDropdownAdapter bankDropdownAdapter;
    private String selectedBankBin = "";
    private String selectedBankName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.withdraw_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);

        initViews();
        setupBankDropdown();
        setupListeners();
        loadBalance();
        restoreLastWithdrawAccount();
        fetchBanksFromVietQr();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvWithdrawBalance = findViewById(R.id.tvWithdrawBalance);
        tilBankName = findViewById(R.id.tilBankName);
        actvBankName = findViewById(R.id.actvBankName);
        tilAccountNumber = findViewById(R.id.tilAccountNumber);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        tilAccountHolder = findViewById(R.id.tilAccountHolder);
        etAccountHolder = findViewById(R.id.etAccountHolder);
        etWithdrawAmount = findViewById(R.id.etWithdrawAmount);
        btnMaxAmount = findViewById(R.id.btnMaxAmount);
        etWithdrawNote = findViewById(R.id.etWithdrawNote);
        btnSubmitWithdraw = findViewById(R.id.btnSubmitWithdraw);
    }

    private void setupBankDropdown() {
        populateDefaultBanks();

        bankDropdownAdapter = new BankDropdownAdapter(this, bankList);
        actvBankName.setAdapter(bankDropdownAdapter);

        actvBankName.setOnClickListener(v -> actvBankName.showDropDown());
        actvBankName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actvBankName.showDropDown();
        });

        actvBankName.setOnItemClickListener((parent, view, position, id) -> {
            Bank bank = bankDropdownAdapter.getItem(position);
            if (bank != null) {
                selectedBankBin = bank.getBin();
                selectedBankName = bank.getDisplayName();
                actvBankName.setText(bank.getDisplayName(), false);
                tilBankName.setError(null);
            }
        });
    }

    private void populateDefaultBanks() {
        bankList.clear();
        bankList.add(new Bank("970436", "VCB", "Vietcombank", "Ngân hàng Ngoại Thương Việt Nam", "https://cdn.vietqr.io/img/VCB.png"));
        bankList.add(new Bank("970422", "MBB", "MB Bank", "Ngân hàng Quân Đội", "https://cdn.vietqr.io/img/MB.png"));
        bankList.add(new Bank("970407", "TCB", "Techcombank", "Ngân hàng Kỹ Thương Việt Nam", "https://cdn.vietqr.io/img/TCB.png"));
        bankList.add(new Bank("970415", "CTG", "VietinBank", "Ngân hàng Công Thương Việt Nam", "https://cdn.vietqr.io/img/ICB.png"));
        bankList.add(new Bank("970418", "BID", "BIDV", "Ngân hàng Đầu Tư & Phát Triển Việt Nam", "https://cdn.vietqr.io/img/BIDV.png"));
        bankList.add(new Bank("970405", "VBA", "Agribank", "Ngân hàng Nông Nghiệp & PTNT", "https://cdn.vietqr.io/img/VBA.png"));
        bankList.add(new Bank("970432", "VPB", "VPBank", "Ngân hàng Việt Nam Thịnh Vượng", "https://cdn.vietqr.io/img/VPB.png"));
        bankList.add(new Bank("970416", "ACB", "ACB", "Ngân hàng Á Châu", "https://cdn.vietqr.io/img/ACB.png"));
        bankList.add(new Bank("970423", "TPB", "TPBank", "Ngân hàng Tiên Phong", "https://cdn.vietqr.io/img/TPB.png"));
        bankList.add(new Bank("970403", "STB", "Sacombank", "Ngân hàng Sài Gòn Thương Tín", "https://cdn.vietqr.io/img/STB.png"));
        bankList.add(new Bank("970441", "VIB", "VIB", "Ngân hàng Quốc Tế Việt Nam", "https://cdn.vietqr.io/img/VIB.png"));
        bankList.add(new Bank("970437", "HDB", "HDBank", "Ngân hàng Phát Triển TP.HCM", "https://cdn.vietqr.io/img/HDB.png"));
        bankList.add(new Bank("970443", "SHB", "SHB", "Ngân hàng Sài Gòn - Hà Nội", "https://cdn.vietqr.io/img/SHB.png"));
        bankList.add(new Bank("970426", "MSB", "MSB", "Ngân hàng Hàng Hải", "https://cdn.vietqr.io/img/MSB.png"));
        bankList.add(new Bank("970448", "OCB", "OCB", "Ngân hàng Phương Đông", "https://cdn.vietqr.io/img/OCB.png"));
        bankList.add(new Bank("970440", "SSB", "SeABank", "Ngân hàng Đông Nam Á", "https://cdn.vietqr.io/img/SSB.png"));
        bankList.add(new Bank("970409", "BAB", "Bac A Bank", "Ngân hàng Bắc Á", "https://cdn.vietqr.io/img/BAB.png"));
        bankList.add(new Bank("970428", "NAB", "Nam A Bank", "Ngân hàng Nam Á", "https://cdn.vietqr.io/img/NAB.png"));
        bankList.add(new Bank("970449", "LPB", "LPBank", "Ngân hàng Lộc Phát Việt Nam", "https://cdn.vietqr.io/img/LPB.png"));
        bankList.add(new Bank("970452", "KLB", "Kienlongbank", "Ngân hàng Kiên Long", "https://cdn.vietqr.io/img/KLB.png"));
        bankList.add(new Bank("970454", "BVB", "BVBank", "Ngân hàng Bản Việt", "https://cdn.vietqr.io/img/BVB.png"));
        bankList.add(new Bank("970431", "EIB", "Eximbank", "Ngân hàng Xuất Nhập Khẩu", "https://cdn.vietqr.io/img/EIB.png"));
        bankList.add(new Bank("970412", "PVC", "PVcomBank", "Ngân hàng Đại Chúng", "https://cdn.vietqr.io/img/PVC.png"));
        bankList.add(new Bank("970400", "SGB", "Saigonbank", "Ngân hàng Sài Gòn Công Thương", "https://cdn.vietqr.io/img/SGB.png"));
        bankList.add(new Bank("970438", "BVB", "BaoViet Bank", "Ngân hàng Bảo Việt", "https://cdn.vietqr.io/img/BVB.png"));
        bankList.add(new Bank("546034", "CAKE", "CAKE", "Ngân hàng số CAKE by VPBank", "https://cdn.vietqr.io/img/CAKE.png"));
        bankList.add(new Bank("963388", "TIMO", "Timo", "Ngân hàng số Timo by BVBank", "https://cdn.vietqr.io/img/TIMO.png"));
    }

    private void fetchBanksFromVietQr() {
        VietQrClient.getInstance().getService().getBanks().enqueue(new Callback<VietQrResponse<List<Bank>>>() {
            @Override
            public void onResponse(@NonNull Call<VietQrResponse<List<Bank>>> call,
                                   @NonNull retrofit2.Response<VietQrResponse<List<Bank>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Bank> apiBanks = response.body().getData();
                    if (apiBanks != null && !apiBanks.isEmpty()) {
                        bankList.clear();
                        bankList.addAll(apiBanks);
                        if (bankDropdownAdapter != null) {
                            bankDropdownAdapter.updateBanks(bankList);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VietQrResponse<List<Bank>>> call, @NonNull Throwable t) {
                // Giữ danh sách ngân hàng mặc định khi mất kết nối mạng
            }
        });
    }

    private void restoreLastWithdrawAccount() {
        if (sessionManager == null) return;
        String lastBank = sessionManager.getLastWithdrawBankName();
        String lastAccNum = sessionManager.getLastWithdrawAccountNumber();
        String lastAccHolder = sessionManager.getLastWithdrawAccountHolder();

        if (!TextUtils.isEmpty(lastBank) && !TextUtils.isEmpty(lastAccNum)) {
            actvBankName.setText(lastBank, false);
            selectedBankName = lastBank;
            selectedBankBin = sessionManager.getLastWithdrawBankBin();
            etAccountNumber.setText(lastAccNum);
            if (!TextUtils.isEmpty(lastAccHolder)) {
                etAccountHolder.setText(lastAccHolder);
            }
            tilAccountHolder.setHelperText("Đã tự động nạp tài khoản rút gần nhất của bạn");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSubmitWithdraw.setOnClickListener(v -> handleSubmitWithdraw());

        btnMaxAmount.setOnClickListener(v -> {
            if (currentBalance > 0) {
                java.text.DecimalFormat df = new java.text.DecimalFormat("0.###", new java.text.DecimalFormatSymbols(Locale.US));
                etWithdrawAmount.setText(df.format(currentBalance));
                if (etWithdrawAmount.getText() != null) {
                    etWithdrawAmount.setSelection(etWithdrawAmount.getText().length());
                }
                etWithdrawAmount.setError(null);
            } else {
                etWithdrawAmount.setText("0");
                if (etWithdrawAmount.getText() != null) {
                    etWithdrawAmount.setSelection(etWithdrawAmount.getText().length());
                }
            }
        });

        // Tự động viết HOA khi người dùng nhập tên chủ tài khoản chuẩn định dạng ngân hàng
        etAccountHolder.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting || s == null) return;
                String original = s.toString();
                String upper = original.toUpperCase(Locale.ROOT);
                if (!original.equals(upper)) {
                    isFormatting = true;
                    int selStart = etAccountHolder.getSelectionStart();
                    int selEnd = etAccountHolder.getSelectionEnd();
                    etAccountHolder.setText(upper);
                    etAccountHolder.setSelection(
                            Math.min(upper.length(), selStart),
                            Math.min(upper.length(), selEnd)
                    );
                    isFormatting = false;
                }
            }
        });
    }

    private void loadBalance() {
        if (sessionManager != null) {
            currentBalance = sessionManager.getBalance();
        }
        if (tvWithdrawBalance != null) {
            tvWithdrawBalance.setText(String.format(Locale.US, "$%.3f", currentBalance));
        }
    }

    private void handleSubmitWithdraw() {
        String bankName = actvBankName.getText() != null ? actvBankName.getText().toString().trim() : "";
        String accountNumber = etAccountNumber.getText() != null ? etAccountNumber.getText().toString().trim() : "";
        String accountHolder = etAccountHolder.getText() != null ? etAccountHolder.getText().toString().trim() : "";
        String amountStr = etWithdrawAmount.getText() != null ? etWithdrawAmount.getText().toString().trim() : "";
        String note = etWithdrawNote.getText() != null ? etWithdrawNote.getText().toString().trim() : "";

        if (TextUtils.isEmpty(bankName)) {
            tilBankName.setError("Vui lòng chọn ngân hàng nhận tiền");
            actvBankName.requestFocus();
            return;
        } else {
            tilBankName.setError(null);
        }

        if (TextUtils.isEmpty(accountNumber)) {
            etAccountNumber.setError("Vui lòng nhập số tài khoản");
            etAccountNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(accountHolder)) {
            etAccountHolder.setError("Vui lòng nhập họ và tên chủ tài khoản");
            etAccountHolder.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            etWithdrawAmount.setError("Vui lòng nhập số tiền muốn rút");
            etWithdrawAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etWithdrawAmount.setError("Số tiền không hợp lệ");
            etWithdrawAmount.requestFocus();
            return;
        }

        if (amount <= 0) {
            etWithdrawAmount.setError("Số tiền rút phải lớn hơn 0");
            etWithdrawAmount.requestFocus();
            return;
        }

        if (amount > currentBalance) {
            etWithdrawAmount.setError("Số dư hiện tại không đủ để thực hiện rút số tiền này!");
            etWithdrawAmount.requestFocus();
            return;
        }

        btnSubmitWithdraw.setEnabled(false);
        btnSubmitWithdraw.setText("Đang gửi yêu cầu...");

        String token = sessionManager != null ? sessionManager.getToken() : "";
        if (!token.isEmpty()) {
            String bearerToken = "Bearer " + token;
            Map<String, Object> body = new HashMap<>();
            body.put("accountHolder", accountHolder.toUpperCase(Locale.ROOT));
            body.put("bankName", bankName);
            body.put("accountNumber", accountNumber);
            body.put("amount", amount);
            body.put("note", note);

            HttpRequest.getInstance().call().requestWithdraw(bearerToken, body).enqueue(new Callback<Response<Object>>() {
                @Override
                public void onResponse(@NonNull Call<Response<Object>> call, @NonNull retrofit2.Response<Response<Object>> response) {
                    btnSubmitWithdraw.setEnabled(true);
                    btnSubmitWithdraw.setText(R.string.btn_submit_withdraw);

                    if (response.isSuccessful() && response.body() != null) {
                        Response<Object> res = response.body();
                        if (res.isSuccess()) {
                            // Trừ số dư và lưu thông tin tài khoản cho lần rút sau
                            double newBalance = Math.max(0.0, currentBalance - amount);
                            if (sessionManager != null) {
                                sessionManager.updateBalance(newBalance);
                                sessionManager.saveLastWithdrawAccount(bankName, selectedBankBin, accountNumber, accountHolder);
                            }
                            showSuccessDialog(amount, bankName, accountNumber);
                        } else {
                            Toast.makeText(WithdrawActivity.this, res.getMessage() != null ? res.getMessage() : "Yêu cầu rút tiền không thành công!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String errMsg = "Lỗi xử lý từ máy chủ (" + response.code() + ")!";
                        try {
                            if (response.errorBody() != null) {
                                String errBody = response.errorBody().string();
                                if (errBody.contains("message")) {
                                    org.json.JSONObject jObj = new org.json.JSONObject(errBody);
                                    if (jObj.has("message")) errMsg = jObj.getString("message");
                                }
                            }
                        } catch (Exception ignored) {}
                        Toast.makeText(WithdrawActivity.this, errMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Response<Object>> call, @NonNull Throwable t) {
                    btnSubmitWithdraw.setEnabled(true);
                    btnSubmitWithdraw.setText(R.string.btn_submit_withdraw);
                    Toast.makeText(WithdrawActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            btnSubmitWithdraw.setEnabled(true);
            btnSubmitWithdraw.setText(R.string.btn_submit_withdraw);
            Toast.makeText(this, "Vui lòng đăng nhập lại để thực hiện giao dịch!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessDialog(double amount, String bankName, String accountNumber) {
        String msg = String.format(Locale.US,
                "Yêu cầu rút tiền $%.3f đã được gửi thành công!\n\nNgân hàng: %s\nSố tài khoản: %s\n\nHệ thống đang xử lý và sẽ chuyển khoản cho bạn sớm nhất.",
                amount, bankName, accountNumber);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Gửi yêu cầu thành công")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }
}
