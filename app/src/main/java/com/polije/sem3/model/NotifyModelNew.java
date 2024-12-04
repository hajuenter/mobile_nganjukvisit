package com.polije.sem3.model;

import com.google.gson.annotations.SerializedName;

public class NotifyModelNew {
    @SerializedName("judul")
    private final String judul;
    @SerializedName("isi")
    private final String bodynotif; // Asumsi ini adalah format tanggal String

    public NotifyModelNew(String judul, String bodynotif) {
        this.judul = judul;
        this.bodynotif = bodynotif;
    }

    public String getJudul() {
        return judul;
    }

    public String getBodynotif() {
        return bodynotif;
    }

}
