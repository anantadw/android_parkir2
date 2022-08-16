package com.example.aplikasiparkirpayment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.aplikasiparkirpayment.model.TransactionData;
import com.example.aplikasiparkirpayment.model.TransactionResponse;
import com.example.aplikasiparkirpayment.retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionList extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    LoadingDialog loadingDialog;
    List<TransactionData> transactionLists;
    private int parker_id, vehicle_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        setUpView();

        Intent intentData = getIntent();
        String vehicle_type = intentData.getStringExtra("vehicle_type");
        parker_id = Preferences.getParkerId(getBaseContext());
        vehicle_id = intentData.getIntExtra("vehicle_id", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar Transaksi | " + vehicle_type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog.startLoadingDialog();
        getTransactionsData(parker_id, vehicle_id);
    }

    private void setUpView() {
        loadingDialog = new LoadingDialog(TransactionList.this);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getTransactionsData(parker_id, vehicle_id);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getTransactionsData(int parker_id, int vehicle_id) {
        String token = Preferences.getToken(getBaseContext());
        if (token != null) {
            Call<TransactionResponse> getTransactionsCall = ApiService.endpoint().getTransactions("Bearer " + token, parker_id, vehicle_id);
            getTransactionsCall.enqueue(new Callback<TransactionResponse>() {
                @Override
                public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                    if (response.isSuccessful()) {
                        transactionLists = response.body().getData();
                        TransactionAdapter adapter = new TransactionAdapter(transactionLists, new TransactionAdapter.OnAdapterListener() {
                            @Override
                            public void onClick(TransactionData transaction) {
                                Intent intent = new Intent(TransactionList.this, TransactionOutActivity.class);
                                intent.putExtra("transaction_id", transaction.getId());
                                intent.putExtra("vehicle_id", vehicle_id);
                                startActivity(intent);
                            }
                        });
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        loadingDialog.dismissDialog();
                    }
                }

                @Override
                public void onFailure(Call<TransactionResponse> call, Throwable t) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getApplicationContext(), "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Error: Anda tidak punya akses (Token/ID null).", Toast.LENGTH_LONG).show();
        }
    }
}