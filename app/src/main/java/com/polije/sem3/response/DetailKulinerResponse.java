package com.polije.sem3.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.polije.sem3.model.KulinerModel;

public class DetailKulinerResponse {
    @Expose
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private KulinerModel data;

    public void setMessage(String status) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
    public String getStatus() {
        return this.status;
    }
    public KulinerModel getData() {
        return data;
    }

    public void setData(KulinerModel data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
