package com.polije.sem3.network;

import androidx.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.polije.sem3.retrofit.RetrofitEndPoint;

import java.util.concurrent.TimeUnit;

public class Client {

//        public static final String BASE_URL = "http://192.168.1.62/nganjukvisit/"; // local
//    public static final String BASE_URL = "http://192.168.1.7/nganjukvisit/"; // wifi
//public static final String BASE_URL = "http://192.168.43.115/nganjukvisit/"; // local
    public static final String BASE_URL = "https://nganjukvisit.pbltifnganjuk.com/"; //ip karo folder web e
//public static final String BASE_URL = "http://192.168.1.15/web_nganjukvisit-main/";
//        public static final String BASE_URL = "https://nganjukvisit.tifnganjuk.com/"; // local
/*
    C:\xampp\htdocs\web_nganjukvisit-main\api\loginApi.php
*/

    public static final String CONTROLLERS = BASE_URL + "API/";

    public static final String IMG_DATA = BASE_URL + "public/gambar/";

    public static final String PUBLIC_IMG = "public/img/";

    public static final String USER_PHOTO_URL = BASE_URL + PUBLIC_IMG + "user-photo/";

    public static final String SUCCESSFUL_RESPONSE = "success";

    @NonNull
    public static RetrofitEndPoint getInstance(){
        return getConnection().create(RetrofitEndPoint.class);
    }

    /**
     * connect to the rest server
     */
    public static Retrofit getConnection() {

        Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(CONTROLLERS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

}
