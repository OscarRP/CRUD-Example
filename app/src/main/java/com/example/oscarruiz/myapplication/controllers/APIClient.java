package com.example.oscarruiz.myapplication.controllers;

import com.example.oscarruiz.myapplication.Utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Oscar Ruiz on 09/08/2017.
 */

public class APIClient {

    private static Retrofit retrofit = null;

    /**
     * Method to create Retrofit Obejct
     */
    public static Retrofit getClient() {

        //Create client
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        //Create object
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
