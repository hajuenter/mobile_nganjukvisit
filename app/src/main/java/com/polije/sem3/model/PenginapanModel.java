package com.polije.sem3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PenginapanModel {
    @Expose
    @SerializedName("id_penginapan")
    private String idPenginapan;
    @SerializedName("id_wisata")
    private String idWisata;
    @SerializedName("nama_penginapan")
    private String judulPenginapan;
    @SerializedName("deskripsi")
    private String deskripsi;
    @SerializedName("lokasi")
    private String lokasi;
    @SerializedName("koordinat")
    private String linkmaps;
    @SerializedName("gambar")
    private String gambar;
    @SerializedName("telepon")
    private String telepon;

    public PenginapanModel(String idPenginapan, String idWisata, String judulPenginapan,
                           String deskripsi, String lokasi, String linkmaps, String gambar, String telepon) {
        this.idPenginapan = idPenginapan;
        this.idWisata = idWisata;
        this.judulPenginapan = judulPenginapan;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.linkmaps = linkmaps;
        this.gambar = gambar;
        this.telepon = telepon;
    }

    public String getIdPenginapan() {
        return idPenginapan;
    }

    public void setIdPenginapan(String idPenginapan) {
        this.idPenginapan = idPenginapan;
    }

    public String getIdWisata() {
        return idWisata;
    }

    public void setIdWisata(String idWisata) {
        this.idWisata = idWisata;
    }

    public String getJudulPenginapan() {
        return judulPenginapan;
    }

    public void setJudulPenginapan(String judulPenginapan) {
        this.judulPenginapan = judulPenginapan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getLinkmaps() {
        return linkmaps;
    }

    public void setLinkmaps(String linkmaps) {
        this.linkmaps = linkmaps;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
}
