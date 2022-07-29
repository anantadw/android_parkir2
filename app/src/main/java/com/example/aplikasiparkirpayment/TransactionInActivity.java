package com.example.aplikasiparkirpayment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.aplikasiparkirpayment.model.TransactionRequest;
import com.example.aplikasiparkirpayment.retrofit.ApiService;

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

        loadingDialog = new LoadingDialog(TransactionInActivity.this);
        toolbar = findViewById(R.id.toolbar);
        et_region_code = findViewById(R.id.etRegionCode);
        et_police_number = findViewById(R.id.etPoliceNumber);
        et_last_code = findViewById(R.id.etLastCode);
        btn_start = findViewById(R.id.btnStart);

        Intent intentData = getIntent();
        String vehicle_type = intentData.getStringExtra("vehicle_type");
        int parker_id = Preferences.getParkerId(getBaseContext());
        int vehicle_id = intentData.getIntExtra("vehicle_id", 0);

        setSupportActionBar(toolbar);
        if (vehicle_type != null) {
            getSupportActionBar().setTitle("Transaksi Masuk | " + vehicle_type);
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

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_region_code.getText().toString()) || TextUtils.isEmpty(et_police_number.getText().toString()) || TextUtils.isEmpty(et_last_code.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Mohon lengkapi plat nomor.", Toast.LENGTH_LONG).show();
                } else {
                    String license_plate = et_region_code.getText().toString().toUpperCase() + et_police_number.getText().toString() + et_last_code.getText().toString().toUpperCase();
                    loadingDialog.startLoadingDialog();
                    sendRequest(parker_id, vehicle_id, license_plate);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void sendRequest(int parker_id, int vehicle_id, String license_plate) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setParker_id(parker_id);
        transactionRequest.setVehicle_id(vehicle_id);
        transactionRequest.setLicense_plate(license_plate);

        String token = Preferences.getToken(getBaseContext());
        if (token != null) {
            Call<DefaultResponse> defaultResponseCall = ApiService.endpoint().createTransaction("Bearer " + token, transactionRequest);
            defaultResponseCall.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismissDialog();
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
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getApplicationContext(), "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}