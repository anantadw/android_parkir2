package com.example.aplikasiparkirpayment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.aplikasiparkirpayment.helper.ErrorUtils;
import com.example.aplikasiparkirpayment.model.DefaultResponse;
import com.example.aplikasiparkirpayment.retrofit.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNav;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadingDialog = new LoadingDialog(HomeActivity.this);
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_baseline_account_circle_24);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Set parker name into toolbar
        String parker_name = Preferences.getParkerName(getBaseContext());
        if (parker_name != null) {
            getSupportActionBar().setTitle(parker_name);
        }

        // Set bottom navigation listener
        // setOnNavigationItemSelectedListener is depricated
        bottomNav.setOnItemSelectedListener(navListener);

        // Set car fragment as default display
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CarFragment()).commit();
    }

    // Set menu into toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Toolbar menu click listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemLogOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Keluar Akun")
                        .setMessage("Anda yakin ingin keluar akun?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.startLoadingDialog();
                                logout();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Logout method
    private void logout() {
        String token = Preferences.getToken(getBaseContext());
        int parker_id = Preferences.getParkerId(getBaseContext());
        if (token != null && parker_id != 0) {
            Call<DefaultResponse> defaultResponseCall = ApiService.endpoint().parkerLogout("Bearer " + token, parker_id);
            defaultResponseCall.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        Preferences.clearLoggedInUser(getBaseContext());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(getApplicationContext(), "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Bottom navigation menu listener
    // OnNavigationItemSelectedListener is depricated
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected_fragment = null;

            switch (item.getItemId()) {
                case R.id.nav_car:
                    selected_fragment = new CarFragment();
                    break;
                case R.id.nav_motorcycle:
                    selected_fragment = new MotorcycleFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected_fragment).commit();

            return true;
        }
    };

    // Show confirm dialog when back button is pressed
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Keluar Aplikasi")
                .setMessage("Anda yakin ingin keluar aplikasi?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}