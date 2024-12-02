package com.polije.sem3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UlasanModel {

    @Expose
    @SerializedName("id_ulasan")
    private String idUlasan;
    @SerializedName("nama")
    private String namaPengguna;
    @SerializedName("komentar")
    private String ulasan;
    @SerializedName("tanggal")
    private String dateTime;
    @SerializedName("id_wisata")
    private String idWisata;
    @SerializedName("id_user")
    private String idUser;
    @SerializedName("rating")
    private Float rating;

    public UlasanModel(String idUlasan, String namaPengguna, String ulasan, String dateTime, String idWisata, String idUser,Float rating) {
        this.idUlasan = idUlasan;
        this.namaPengguna = namaPengguna;
        this.ulasan = ulasan;
        this.dateTime = dateTime;
        this.idWisata = idWisata;
        this.idUser = idUser;
        this.rating = rating;
    }

    public String getIdUlasan() {
        return idUlasan;
    }

    public void setIdUlasan(String idUlasan) {
        this.idUlasan = idUlasan;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public void setNamaPengguna(String namaPengguna) {
        this.namaPengguna = namaPengguna;
    }

    public String getUlasan() {
        return ulasan;
    }

    public void setUlasan(String ulasan) {
        this.ulasan = ulasan;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public Float getrating() {
        return rating;
    }

    public void setrating(String idUser) {
        this.rating = rating;
    }
}
