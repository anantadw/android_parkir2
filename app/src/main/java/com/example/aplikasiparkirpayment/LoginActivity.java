package com.example.aplikasiparkirpayment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aplikasiparkirpayment.helper.ErrorUtils;
import com.example.aplikasiparkirpayment.model.DefaultResponse;
import com.example.aplikasiparkirpayment.model.LoginRequest;
import com.example.aplikasiparkirpayment.model.LoginResponse;
import com.example.aplikasiparkirpayment.retrofit.ApiService;

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

        loadingDialog = new LoadingDialog(LoginActivity.this);
        et_member_number = findViewById(R.id.etMemberNumber);
        et_password = findViewById(R.id.etPassword);
        btn_login = findViewById(R.id.btnLogin);

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

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_member_number.getText().toString()) || TextUtils.isEmpty(et_password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Nomor anggota / kata sandi harus diisi.", Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.startLoadingDialog();
                    login();
                }
            }
        });
    }

    private void login() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMember_number(et_member_number.getText().toString());
        loginRequest.setPassword(et_password.getText().toString());

        Call<LoginResponse> loginResponseCall = ApiService.endpoint().parkerLogin(loginRequest);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                    // Set data to preferences
                    Preferences.setParkerId(getBaseContext(), response.body().getParker_id());
                    Preferences.setParkerName(getBaseContext(), response.body().getParker_name());
                    Preferences.setToken(getBaseContext(), response.body().getToken());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            loadingDialog.dismissDialog();
                            startActivity(intent);
                            finish();
                        }
                    }, 700);
                } else {
                    loadingDialog.dismissDialog();
                    DefaultResponse errorResponse = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), errorResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loadingDialog.dismissDialog();
                Toast.makeText(getApplicationContext(), "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}