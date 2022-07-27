package com.example.aplikasiparkirpayment.retrofit;

import com.example.aplikasiparkirpayment.model.DetailTransactionResponse;
import com.example.aplikasiparkirpayment.model.LoginRequest;
import com.example.aplikasiparkirpayment.model.LoginResponse;
import com.example.aplikasiparkirpayment.model.DefaultResponse;
import com.example.aplikasiparkirpayment.model.TransactionRequest;
import com.example.aplikasiparkirpayment.model.TransactionResponse;
import com.example.aplikasiparkirpayment.model.TransactionUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("login")
    Call<LoginResponse> parkerLogin(@Body LoginRequest loginRequest);

    @POST("logout/{parker_id}")
    Call<DefaultResponse> parkerLogout(@Header("Authorization") String authorization, @Path("parker_id") int parker_id);

    @POST("transactions")
    Call<DefaultResponse> createTransaction(@Header("Authorization") String authorization, @Body TransactionRequest transactionRequest);

    @GET("transactions/parker/{parker_id}/vehicle/{vehicle_id}")
    Call<TransactionResponse> getTransactions(@Header("Authorization") String authorization, @Path("parker_id") int parker_id, @Path("vehicle_id") int vehicle_id);

    @GET("transactions/{transaction_id}")
    Call<DetailTransactionResponse> getDetailTransaction(@Header("Authorization") String authorization, @Path("transaction_id") int transaction_id);

    @PATCH("transactions/{transaction_id}")
    Call<DefaultResponse> updateTransactionStatus(@Header("Authorization") String authorization, @Path("transaction_id") int transaction_id, @Body TransactionUpdate transactionUpdate);
}
