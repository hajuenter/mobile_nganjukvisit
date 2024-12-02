package com.polije.sem3.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.polije.sem3.model.UlasanModel;

public class UlasanResponse1 {
    @Expose
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UlasanModel data; // Ubah menjadi objek, bukan ArrayList

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UlasanModel getData() {
        return data; // Pastikan getter sesuai
    }

    public void setData(UlasanModel data) {
        this.data = data; // Sesuaikan setter
    }
}
