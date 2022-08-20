package com.example.aplikasiparkirpayment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.example.aplikasiparkirpayment.helper.ErrorUtils;
import com.example.aplikasiparkirpayment.model.DefaultResponse;
import com.example.aplikasiparkirpayment.model.LoginRequest;
import com.example.aplikasiparkirpayment.model.LoginResponse;
import com.example.aplikasiparkirpayment.retrofit.ApiService;
import com.shashank.sony.fancytoastlib.FancyToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText et_member_number, et_password;
    Button btn_login;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpView();

        et_member_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 5) {
                    et_password.requestFocus();
                }
            }
        });

        btn_login.setOnClickListener(v -> {
            if (TextUtils.isEmpty(et_member_number.getText().toString()) || TextUtils.isEmpty(et_password.getText().toString())) {
                FancyToast.makeText(getApplicationContext(), "Nomor anggota / kata sandi harus diisi.", FancyToast.LENGTH_LONG, FancyToast.DEFAULT, false).show();
            } else {
                loadingDialog.startLoadingDialog();
                login();
            }
        });
    }

    private void setUpView() {
        loadingDialog = new LoadingDialog(LoginActivity.this);
        et_member_number = findViewById(R.id.etMemberNumber);
        et_password = findViewById(R.id.etPassword);
        btn_login = findViewById(R.id.btnLogin);
    }

    private void login() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMemberNumber(et_member_number.getText().toString());
        loginRequest.setPassword(et_password.getText().toString());

        Call<LoginResponse> loginCall = ApiService.endpoint().parkerLogin(loginRequest);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    FancyToast.makeText(getApplicationContext(), response.body().getMessage(), FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                    // Set data to preferences
                    Preferences.setParkerId(getBaseContext(), response.body().getParkerId());
                    Preferences.setParkerName(getBaseContext(), response.body().getParkerName());
                    Preferences.setToken(getBaseContext(), response.body().getToken());

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        loadingDialog.dismissDialog();
                        startActivity(intent);
                        finish();
                    }, 700);
                } else {
                    loadingDialog.dismissDialog();
                    DefaultResponse errorResponse = ErrorUtils.parseError(response);
                    FancyToast.makeText(getApplicationContext(), errorResponse.getMessage(), FancyToast.LENGTH_LONG, FancyToast.DEFAULT, false).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loadingDialog.dismissDialog();
                FancyToast.makeText(getApplicationContext(), "Error: " + t.getLocalizedMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        });
    }
}