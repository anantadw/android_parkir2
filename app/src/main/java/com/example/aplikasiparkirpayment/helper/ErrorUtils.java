package com.example.aplikasiparkirpayment.helper;

import com.example.aplikasiparkirpayment.model.DefaultResponse;
import com.example.aplikasiparkirpayment.retrofit.ApiService;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {
    public static DefaultResponse parseError(Response<?> response) {
        Converter<ResponseBody, DefaultResponse> converter = ApiService.getRetrofit().responseBodyConverter(DefaultResponse.class, new Annotation[0]);

        DefaultResponse error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
            return new DefaultResponse();
        }

        return error;
    }
}
