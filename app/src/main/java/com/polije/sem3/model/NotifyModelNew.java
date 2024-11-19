package com.polije.sem3.model;

public class NotifyModelNew {
    private String judul;
    private String bodynotif;
    private String tanggalnotif; // Asumsi ini adalah format tanggal String

    public NotifyModelNew(String judul, String bodynotif, String tanggalnotif) {
        this.judul = judul;
        this.bodynotif = bodynotif;
        this.tanggalnotif = tanggalnotif;
    }

    public String getJudul() {
        return judul;
    }

    public String getBodynotif() {
        return bodynotif;
    }

    public String getTanggalnotif() {
        return tanggalnotif;
    }
}
