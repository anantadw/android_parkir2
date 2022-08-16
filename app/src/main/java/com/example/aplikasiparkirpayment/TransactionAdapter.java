package com.example.aplikasiparkirpayment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiparkirpayment.model.TransactionData;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<TransactionData> transactionLists;
    private OnAdapterListener listener;

    public TransactionAdapter(List<TransactionData> transactionLists, OnAdapterListener listener) {
        this.transactionLists = transactionLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        TransactionData transaction = transactionLists.get(position);

        holder.tv_license_plate.setText(transaction.getLicensePlate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_license_plate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_license_plate = itemView.findViewById(R.id.tvLicensePlate);
        }
    }

    interface OnAdapterListener {
        void onClick(TransactionData transaction);
    }
}
