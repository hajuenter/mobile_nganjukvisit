package com.polije.sem3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WisataModel {

    @Expose
    @SerializedName("id_wisata")
    private String idwisata;
    @SerializedName("nama_wisata")
    private String nama;
    @SerializedName("deskripsi")
    private String deskripsi;
    @SerializedName("koordinat")
    private String coordinate;
    @SerializedName("link_maps")
    private String linkmaps;
    @SerializedName("gambar")
    private String gambar;
    @SerializedName("jadwal")
    private String jadwal;
    @SerializedName("harga_tiket")
    private String harga_tiket;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("no_hp_pengelola")
    private String no_hp;

    public WisataModel(String idwisata, String nama, String deskripsi, String coordinate, String linkmaps, String gambar, String jadwal, String harga_tiket, String alamat, String no_hp) {
        this.idwisata = idwisata;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.coordinate = coordinate;
        this.linkmaps = linkmaps;
        this.gambar = gambar;
        this.jadwal = jadwal;
        this.harga_tiket = harga_tiket;
        this.alamat = alamat;
        this.no_hp = no_hp;
    }

    public String getIdwisata() {
        return idwisata;
    }

    public void setIdwisata(String idwisata) {
        this.idwisata = idwisata;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
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

    public String getJadwal() {
        return jadwal;
    }

    public void setJadwal(String jadwal) {
        this.jadwal = jadwal;
    }

    public String getHarga_tiket() {
        return harga_tiket;
    }

    public void setHarga_tiket(String harga_tiket) {
        this.harga_tiket = harga_tiket;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }
}
