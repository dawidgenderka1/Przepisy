package com.example.przepisy.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:3000/"; // Dla emulatora Android, 10.0.2.2 wskazuje na localhost Twojego komputera

    public static UserApiService getUserService() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApiService.class);
    }
}
