package com.example.aplikasiparkirpayment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.aplikasiparkirpayment.model.TransactionRequest;
import com.example.aplikasiparkirpayment.retrofit.ApiService;
import com.shashank.sony.fancytoastlib.FancyToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionInActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText et_region_code, et_police_number, et_last_code;
    Button btn_start;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_in);

        setUpView();

        Intent intentData = getIntent();
        String vehicle_type = intentData.getStringExtra("vehicle_type");
        int parker_id = Preferences.getParkerId(getBaseContext());
        int vehicle_id = intentData.getIntExtra("vehicle_id", 0);

        setSupportActionBar(toolbar);
        if (vehicle_type != null) {
            getSupportActionBar().setTitle("Transaksi Masuk | " + vehicle_type);
        } else {
            getSupportActionBar().setTitle("Transaksi Masuk | null");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_police_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    et_last_code.requestFocus();
                }
            }
        });

        btn_start.setOnClickListener(v -> {
            if (TextUtils.isEmpty(et_region_code.getText().toString()) || TextUtils.isEmpty(et_police_number.getText().toString()) || TextUtils.isEmpty(et_last_code.getText().toString())) {
                FancyToast.makeText(getApplicationContext(), "Mohon lengkapi plat nomor.", FancyToast.LENGTH_LONG, FancyToast.DEFAULT, false).show();
            } else {
                String license_plate = et_region_code.getText().toString().toUpperCase() + et_police_number.getText().toString() + et_last_code.getText().toString().toUpperCase();
                loadingDialog.startLoadingDialog();
                sendRequest(parker_id, vehicle_id, license_plate);
            }
        });
    }

    private void setUpView() {
        loadingDialog = new LoadingDialog(TransactionInActivity.this);
        toolbar = findViewById(R.id.toolbar);
        et_region_code = findViewById(R.id.etRegionCode);
        et_police_number = findViewById(R.id.etPoliceNumber);
        et_last_code = findViewById(R.id.etLastCode);
        btn_start = findViewById(R.id.btnStart);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void sendRequest(int parker_id, int vehicle_id, String license_plate) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setParkerId(parker_id);
        transactionRequest.setVehicleId(vehicle_id);
        transactionRequest.setLicensePlate(license_plate);

        String token = Preferences.getToken(getBaseContext());
        if (token != null) {
            Call<DefaultResponse> createTransactionCall = ApiService.endpoint().createTransaction("Bearer " + token, transactionRequest);
            createTransactionCall.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    if (response.isSuccessful()) {
                        FancyToast.makeText(getApplicationContext(), response.body().getMessage(), FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                        new Handler().postDelayed(() -> {
                            loadingDialog.dismissDialog();
                            finish();
                        }, 700);
                    } else {
                        loadingDialog.dismissDialog();
                        DefaultResponse errorResponse = ErrorUtils.parseError(response);
                        FancyToast.makeText(getApplicationContext(), errorResponse.getMessage(), FancyToast.LENGTH_LONG, FancyToast.DEFAULT, false).show();
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    loadingDialog.dismissDialog();
                    FancyToast.makeText(getApplicationContext(), "Error: " + t.getLocalizedMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            });
        } else {
            FancyToast.makeText(getApplicationContext(), "Error: Anda tidak punya akses (Token/ID null)", FancyToast.LENGTH_LONG, FancyToast.DEFAULT, false).show();
        }
    }
}