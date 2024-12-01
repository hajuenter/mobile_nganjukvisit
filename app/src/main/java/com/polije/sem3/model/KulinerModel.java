package com.polije.sem3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KulinerModel {
    @Expose
    @SerializedName("id_kuliner")
    private String idKuliner;
    @SerializedName("nama_kuliner")
    private String nama;
    @SerializedName("harga")
    private String harga;
    private String deskripsi;
    @SerializedName("alamat")
    private String lokasi;
    private String gambar;
    @SerializedName("link_maps")
    private String linkmaps;
    @SerializedName("koordinat")
    private String coordinate;

    public KulinerModel(String idKuliner, String nama, String harga, String deskripsi, String lokasi, String gambar, String linkmaps, String coordinate) {
        this.idKuliner = idKuliner;
        this.nama = nama;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.gambar = gambar;
        this.linkmaps = linkmaps;
        this.coordinate = coordinate;
    }

    public String getIdKuliner() {
        return idKuliner;
    }

    public void setIdKuliner(String idKuliner) {
        this.idKuliner = idKuliner;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
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

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getLinkmaps() {
        return linkmaps;
    }

    public void setLinkmaps(String linkmaps) {
        this.linkmaps = linkmaps;
    }
    public void setCoordinate(String coordinate){this.coordinate = coordinate;
    }
    public String getCoordinate() {
        return coordinate;
    }
}
