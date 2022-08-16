package com.example.aplikasiparkirpayment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CarFragment extends Fragment {
    Button btn_car_in, btn_car_out;

    public static final String VEHICLE_TYPE = "Mobil";
    public static final int VEHICLE_ID = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car, container, false);

        btn_car_in = view.findViewById(R.id.btnCarIn);
        btn_car_out = view.findViewById(R.id.btnCarOut);

        btn_car_in.setOnClickListener(v -> nextPage(TransactionInActivity.class));

        btn_car_out.setOnClickListener(v -> nextPage(TransactionList.class));

        return view;
    }

    private void nextPage(Class<?> nextClass) {
        Intent intent = new Intent(getActivity(), nextClass);
        intent.putExtra("vehicle_type", VEHICLE_TYPE);
        intent.putExtra("vehicle_id", VEHICLE_ID);
        startActivity(intent);
    }

}
