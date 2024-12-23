package com.polije.sem3.network;

import com.polije.sem3.BuildConfig;
import com.polije.sem3.retrofit.UploadInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadService {
    private final UploadInterface uploadInterface;

    public UploadService() {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient().newBuilder();
        okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.retryOnConnectionFailure(true);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(interceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        uploadInterface = retrofit.create(UploadInterface.class);
    }

    public Call<BaseResponse> uploadPhotoMultipart(RequestBody action, MultipartBody.Part photo) {
        return uploadInterface.uploadPhotoMultipart(action, photo);
    }

    public Call<BaseResponse> uploadPhotoBase64(String action, String photo, Integer iduser) {
        return uploadInterface.uploadPhotoBase64(action, photo, iduser);
    }
}
