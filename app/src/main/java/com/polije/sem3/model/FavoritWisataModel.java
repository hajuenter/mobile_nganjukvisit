package com.polije.sem3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FavoritWisataModel {

    @Expose
    @SerializedName("id_favorit")
    private String id;
    @SerializedName("id_wisata")
    private String idWisata;
    @SerializedName("id_user")
    private String idUser;
    @SerializedName("nama_wisata")
    private String namaWisata;
    @SerializedName("deskripsi")
    private String deskripsi;
    @SerializedName("gambar")
    private String gambar;

    public FavoritWisataModel(String id, String idWisata, String idUser, String namaWisata, String deskripsi, String gambar) {
        this.id = id;
        this.idWisata = idWisata;
        this.idUser = idUser;
        this.namaWisata = namaWisata;
        this.deskripsi = deskripsi;
        this.gambar = gambar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdWisata() {
        return idWisata;
    }

    public void setIdWisata(String idWisata) {
        this.idWisata = idWisata;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamaWisata() {
        return namaWisata;
    }

    public void setNamaWisata(String namaWisata) {
        this.namaWisata = namaWisata;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
