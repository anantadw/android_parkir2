package com.example.aplikasiparkirpayment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.example.aplikasiparkirpayment.model.DefaultResponse;
import com.example.aplikasiparkirpayment.model.DetailTransactionResponse;
import com.example.aplikasiparkirpayment.model.TransactionUpdate;
import com.example.aplikasiparkirpayment.retrofit.ApiService;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionOutActivity extends AppCompatActivity {
    Toolbar toolbar;
    LoadingDialog loadingDialog;
    TextView tv_vehicle, tv_transaction_id, tv_license_plate, tv_date, tv_time_in, tv_cost;
    Button btn_done;
    private int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_out);

        loadingDialog = new LoadingDialog(TransactionOutActivity.this);
        toolbar = findViewById(R.id.toolbar);
        tv_vehicle = findViewById(R.id.tvVehicle);
        tv_transaction_id = findViewById(R.id.tvTransactionId);
        tv_license_plate = findViewById(R.id.tvLicensePlate);
        tv_date = findViewById(R.id.tvDate);
        tv_time_in = findViewById(R.id.tvTimeIn);
        tv_cost = findViewById(R.id.tvCost);
        btn_done = findViewById(R.id.btnDone);

        Intent intentData = getIntent();
        int transaction_id = intentData.getIntExtra("transaction_id", 0);
        int vehicle_id = intentData.getIntExtra("vehicle_id", 0);

        if (vehicle_id == 1) {
            tv_vehicle.setText("Mobil");
        } else {
            tv_vehicle.setText("Motor");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaksi Keluar");

        loadingDialog.startLoadingDialog();
        getDetailTransactionData(transaction_id);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markDone(transaction_id);
            }
        });
    }

    private void getDetailTransactionData(int transaction_id) {
        String token = Preferences.getToken(getBaseContext());
        if (token != null) {
            Call<DetailTransactionResponse> call = ApiService.endpoint().getDetailTransaction("Bearer " + token, transaction_id);
            call.enqueue(new Callback<DetailTransactionResponse>() {
                @Override
                public void onResponse(Call<DetailTransactionResponse> call, Response<DetailTransactionResponse> response) {
                    if (response.isSuccessful()) {
                        int in_time = response.body().getIn_time();
                        int out_time = response.body().getOut_time();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        Date date = new Date((long) in_time * 1000);
                        int hour_spent = (out_time - in_time) / 3600;
                        price = (hour_spent + 1) * response.body().getVehicle_price();

                        tv_transaction_id.setText(String.valueOf(response.body().getId()));
                        tv_license_plate.setText(response.body().getLicense_plate());
                        tv_date.setText(dateFormat.format(date));
                        tv_time_in.setText(timeFormat.format(date));
                        tv_cost.setText("Rp" + String.format("%,d", price));
                        loadingDialog.dismissDialog();
                    } else {
                        if (response.code() == 404) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(getApplicationContext(), "Gagal. Data tidak ditemukan.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DetailTransactionResponse> call, Throwable t) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getApplicationContext(), "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void markDone(int transaction_id) {
        TransactionUpdate transactionUpdate = new TransactionUpdate();
        transactionUpdate.setTotal_price(price);

        String token = Preferences.getToken(getBaseContext());
        if (token != null) {
            Call<DefaultResponse> call = ApiService.endpoint().updateTransactionStatus("Bearer " + token, transaction_id, transactionUpdate);
            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    if (response.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "Berhasil. Data diubah.", Toast.LENGTH_LONG).show();
                        try {
                            printBill();
                        } catch (EscPosConnectionException e) {
                            e.printStackTrace();
                        } catch (EscPosParserException e) {
                            e.printStackTrace();
                        } catch (EscPosEncodingException e) {
                            e.printStackTrace();
                        } catch (EscPosBarcodeException e) {
                            e.printStackTrace();
                        }

                        finish();
                    } else {
                        if (response.code() == 400) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(getApplicationContext(), "Gagal. Data tidak diubah.", Toast.LENGTH_LONG).show();
                        }
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

    // BLUETOOTH PRINTER
    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case TransactionOutActivity.PERMISSION_BLUETOOTH:
                case TransactionOutActivity.PERMISSION_BLUETOOTH_ADMIN:
                case TransactionOutActivity.PERMISSION_BLUETOOTH_CONNECT:
                case TransactionOutActivity.PERMISSION_BLUETOOTH_SCAN:
                    try {
                        this.printBill();
                    } catch (EscPosConnectionException e) {
                        e.printStackTrace();
                    } catch (EscPosParserException e) {
                        e.printStackTrace();
                    } catch (EscPosEncodingException e) {
                        e.printStackTrace();
                    } catch (EscPosBarcodeException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    private void printBill() throws EscPosConnectionException, EscPosParserException, EscPosEncodingException, EscPosBarcodeException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, TransactionOutActivity.PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, TransactionOutActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, TransactionOutActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, TransactionOutActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 58f, 15);
            printer.printFormattedText(
                    "[L]===============\n" +
                    "[C] Struk  Parkir \n" +
                    "[L]===============\n"
            );
        }
    }
}