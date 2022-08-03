package com.example.aplikasiparkirpayment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionOutActivity extends AppCompatActivity {
    Toolbar toolbar;
    LoadingDialog loadingDialog;
    AppCompatImageView iv_vehicle;
    TextView tv_transaction_id, tv_license_plate, tv_date, tv_time_in, tv_cost;
    Spinner sp_select_payment;
    Button btn_pay;
    BluetoothAdapter bluetoothAdapter;
    public static final int BLUETOOTH_REQUEST_CODE = 1;
    private int price, payment_method;
    private DetailTransactionResponse result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_out);

        loadingDialog = new LoadingDialog(TransactionOutActivity.this);
        toolbar = findViewById(R.id.toolbar);
        iv_vehicle = findViewById(R.id.ivVehicle);
        tv_transaction_id = findViewById(R.id.tvTransactionId);
        tv_license_plate = findViewById(R.id.tvLicensePlate);
        tv_date = findViewById(R.id.tvDate);
        tv_time_in = findViewById(R.id.tvTimeIn);
        tv_cost = findViewById(R.id.tvCost);
        sp_select_payment = findViewById(R.id.spSelectPayment);
        btn_pay = findViewById(R.id.btnPay);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent intentData = getIntent();
        int transaction_id = intentData.getIntExtra("transaction_id", 0);
        int vehicle_id = intentData.getIntExtra("vehicle_id", 0);

        if (vehicle_id == 1) {
            iv_vehicle.setImageResource(R.drawable.ic_baseline_directions_car_24);
        } else {
            iv_vehicle.setImageResource(R.drawable.ic_baseline_directions_bike_24);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaksi Keluar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog.startLoadingDialog();
        getDetailTransactionData(transaction_id);

        sp_select_payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payment_method = sp_select_payment.getSelectedItemPosition();
                if (payment_method == 0) {
                    btn_pay.setEnabled(false);
                } else {
                    btn_pay.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Perangkat tidak mendukung Bluetooth.", Toast.LENGTH_SHORT).show();
                }

                if (!bluetoothAdapter.isEnabled()) {
                    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(bluetoothIntent, BLUETOOTH_REQUEST_CODE);
                } else {
                    finishTransaction(transaction_id);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Bluetooth dinyalakan.", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Bluetooth perlu dinyalakan untuk mencetak struk.", Toast.LENGTH_LONG).show();
        }
    }

    private void getDetailTransactionData(int transaction_id) {
        String token = Preferences.getToken(getBaseContext());
        if (token != null) {
            Call<DetailTransactionResponse> call = ApiService.endpoint().getDetailTransaction("Bearer " + token, transaction_id);
            call.enqueue(new Callback<DetailTransactionResponse>() {
                @Override
                public void onResponse(Call<DetailTransactionResponse> call, Response<DetailTransactionResponse> response) {
                    if (response.isSuccessful()) {
                        result = response.body();

                        int in_time = result.getIn_time();
                        int out_time = result.getOut_time();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        Date date = new Date((long) in_time * 1000);
                        int hour_spent = (out_time - in_time) / 3600;
                        price = (hour_spent + 1) * result.getVehicle_price();

                        tv_transaction_id.setText(String.valueOf(result.getId()));
                        tv_license_plate.setText(result.getLicense_plate());
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

    private void finishTransaction(int transaction_id) {
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
                        Toast.makeText(getApplicationContext(), "Transaksi berhasil. Mencetak struk.", Toast.LENGTH_LONG).show();

                        try {
                            printBill();
                        } catch (EscPosConnectionException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        } catch (EscPosParserException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        } catch (EscPosEncodingException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        } catch (EscPosBarcodeException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                        finish();
                    } else {
                        if (response.code() == 400) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(getApplicationContext(), "Transaksi gagal.", Toast.LENGTH_LONG).show();
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
                        printBill();
                    } catch (EscPosConnectionException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    } catch (EscPosParserException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    } catch (EscPosEncodingException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    } catch (EscPosBarcodeException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
            String parker_name = Preferences.getParkerName(getBaseContext());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat formatter_date = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat formatter_time = new SimpleDateFormat("HH:mm");
            String payment = (payment_method == 1) ? "Tunai" : "QRIS";

            EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 58f, 32);
            printer.printFormattedText(
                    "[L]================================\n" +
                    "[C]<b>Struk E-Parkir</b>\n" +
                    "[L]--------------------------------\n" +
                    "[L]ID Transaksi[R]" + tv_transaction_id.getText().toString() + "\n" +
                    "[L]Lokasi[R]" + result.getLocation() + "\n" +
                    "[L]Juru Parkir[R]" + parker_name + "\n" +
                    "[L]--------------------------------\n" +
                    "[L]Kendaraan[R]" + result.getVehicle_name() + "\n" +
                    "[L]Plat Nomor[R]" + tv_license_plate.getText().toString() + "\n" +
                    "[L]Waktu Masuk[R]" + tv_date.getText().toString() + "\n" +
                    "[R]" + tv_time_in.getText().toString() + "\n" +
                    "[L]Waktu Bayar[R]" + formatter_date.format(timestamp) + "\n" +
                    "[R]" + formatter_time.format(timestamp) + "\n" +
                    "[L]Tarif[R]" + tv_cost.getText().toString() + "\n" +
                    "[L]Metode Bayar[R]" + payment + "\n" +
                    "[L]--------------------------------\n" +
                    "[C]<b>Terima kasih!</b>\n" +
                    "[L]================================\n"
            );
            printer.disconnectPrinter();
        }
    }
}